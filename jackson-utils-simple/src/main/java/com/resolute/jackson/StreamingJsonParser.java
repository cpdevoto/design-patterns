package com.resolute.jackson;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toMap;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class StreamingJsonParser {

  private final JsonParser jParser;
  private Action head;
  private Action tail;

  public static StreamingJsonParser create(String json) throws IOException {
    return new StreamingJsonParser(json);
  }

  public static StreamingJsonParser create(InputStream in) throws IOException {
    return new StreamingJsonParser(in);
  }

  public static Attribute attribute(String name, ParseConsumer consumer) {
    requireNonNull(name, "name cannot be null");
    requireNonNull(consumer, "consumer cannot be null");
    return new Attribute(name, (parser, next) -> {
      consumer.execute(parser);
      parser.nextToken();
    });
  }

  public static Attribute attribute(String name) {
    requireNonNull(name, "name cannot be null");
    return new Attribute(name, (parser, next) -> {
      next.execute(parser);
    });
  }

  public static ObjectListener onObjectStart(Runnable runnable) {
    return new ObjectListener(ObjectListener.Type.START, runnable);
  }

  public static ObjectListener onObjectEnd(Runnable runnable) {
    return new ObjectListener(ObjectListener.Type.END, runnable);
  }

  private StreamingJsonParser(String json) throws IOException {
    requireNonNull(json, "json cannot be null");
    JsonFactory jFactory = new JsonFactory();
    jParser = jFactory.createParser(json);
  }

  private StreamingJsonParser(InputStream in) throws IOException {
    requireNonNull(in, "in cannot be null");
    JsonFactory jFactory = new JsonFactory();
    jParser = jFactory.createParser(in);
  }

  public StreamingJsonParser parseArray() {
    addAction((parser, next) -> {
      if (parser.currentToken() == null) {
        return;
      }
      expect(parser.currentToken(), JsonToken.START_ARRAY);
      parser.nextToken();
      do {
        if (parser.currentToken() != JsonToken.END_ARRAY) {
          next.execute(parser);
        }
      } while (parser.currentToken() != JsonToken.END_ARRAY);
      parser.nextToken();
    });
    return this;
  }

  public StreamingJsonParser parseObject(ObjectListener... listeners) {
    ObjectListeners objectListeners = new ObjectListeners(listeners);
    addAction((parser, next) -> {
      if (parser.currentToken() == null) {
        return;
      }
      expect(parser.currentToken(), JsonToken.START_OBJECT);
      objectListeners.notifyStartListeners();
      do {
        parser.nextToken();
        if (parser.currentToken() != JsonToken.END_OBJECT) {
          next.execute(parser);
        }
      } while (parser.currentToken() != JsonToken.END_OBJECT);
      objectListeners.notifyEndListeners();
      parser.nextToken();
    });
    return this;
  }

  public StreamingJsonParser find(Attribute... attributes) {
    checkArgument(attributes.length > 0, "expected at least one attribute");
    for (Attribute attribute : attributes) {
      requireNonNull(attribute, "attribute cannot be null");
    }
    Map<String, Attribute> attributeMap = Arrays.stream(attributes)
        .collect(toMap(Attribute::getName, Function.identity()));
    addAction((parser, next) -> {
      if (parser.currentToken() == null) {
        return;
      }
      do {
        String fieldName = parser.getCurrentName();
        if (attributeMap.containsKey(fieldName)
            && parser.getCurrentToken() != JsonToken.FIELD_NAME) {
          attributeMap.get(fieldName).parseFunction.execute(parser, next);
          continue;
        }
        if (parser.currentToken() == JsonToken.START_OBJECT
            || parser.currentToken() == JsonToken.START_ARRAY) {
          parser.skipChildren();
        }
        parser.nextToken();
      } while (parser.currentToken() != JsonToken.END_OBJECT);
    });
    return this;
  }

  public void execute() {
    checkState(head != null, "expected at least one action");
    try {
      jParser.nextToken();
    } catch (IOException e) {
      throw new ParseException(e);
    }
    head.execute(jParser);
  }

  private void addAction(ParseFunction parseFunction) {
    Action currentAction = new Action(parseFunction);
    Action previousAction = tail;
    if (previousAction != null) {
      previousAction.nextAction = currentAction;
    }
    if (head == null) {
      head = currentAction;
    }
    tail = currentAction;
  }

  private void expect(JsonToken actual, JsonToken expected) {
    if (actual != expected) {
      throw new ParseException("Expected a token of type '" + expected
          + "' but found a token of type '" + actual + "' instead.");
    }
  }

  @FunctionalInterface
  public static interface ParseConsumer {
    void execute(JsonParser parser) throws IOException;
  }

  @FunctionalInterface
  private static interface ParseFunction {
    void execute(JsonParser parser, Next next) throws IOException;
  }

  @FunctionalInterface
  private static interface Next {
    void execute(JsonParser parser) throws IOException;
  }

  private static class Action {
    private Action nextAction;
    private ParseFunction parseFunction;

    private Action(ParseFunction parseFunction) {
      this.parseFunction = parseFunction;
    }

    public void execute(JsonParser parser) {
      try {
        parseFunction.execute(parser, p -> {
          if (nextAction != null) {
            nextAction.execute(p);
          }
        });
      } catch (IOException e) {
        throw new ParseException(e);
      }
    }
  }


  public static class Attribute {
    private String name;
    private ParseFunction parseFunction;

    private Attribute(String name, ParseFunction parseFunction) {
      super();
      this.name = name;
      this.parseFunction = parseFunction;
    }

    String getName() {
      return name;
    }

    ParseFunction getParseFunction() {
      return parseFunction;
    }
  }

  private static class ObjectListeners {
    private final Map<ObjectListener.Type, List<ObjectListener>> listenersByType;

    private ObjectListeners(ObjectListener... listeners) {
      listenersByType = Arrays.stream(listeners)
          .collect(Collectors.groupingBy(ObjectListener::getType));
    }

    public void notifyStartListeners() {
      notifyListeners(ObjectListener.Type.START);
    }

    public void notifyEndListeners() {
      notifyListeners(ObjectListener.Type.END);
    }

    private void notifyListeners(ObjectListener.Type type) {
      if (listenersByType.containsKey(type)) {
        listenersByType.get(type)
            .forEach(listener -> listener.getRunnable().run());
      }

    }
  }

  public static class ObjectListener {
    private static enum Type {
      START, END
    }

    private final Type type;
    private final Runnable runnable;

    private ObjectListener(Type type, Runnable runnable) {
      this.type = type;
      this.runnable = requireNonNull(runnable, "runnable cannot be null");
    }

    public Type getType() {
      return type;
    }

    public Runnable getRunnable() {
      return runnable;
    }


  }


}
