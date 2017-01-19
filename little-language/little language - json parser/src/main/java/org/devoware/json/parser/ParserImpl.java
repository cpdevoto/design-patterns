package org.devoware.json.parser;

import static java.util.Objects.requireNonNull;
import static org.devoware.json.symbols.Token.Type.COLON;
import static org.devoware.json.symbols.Token.Type.COMMA;
import static org.devoware.json.symbols.Token.Type.EOF;
import static org.devoware.json.symbols.Token.Type.LEFT_CURLY_BRACKET;
import static org.devoware.json.symbols.Token.Type.LEFT_SQUARE_BRACKET;
import static org.devoware.json.symbols.Token.Type.RIGHT_CURLY_BRACKET;
import static org.devoware.json.symbols.Token.Type.RIGHT_SQUARE_BRACKET;
import static org.devoware.json.symbols.Token.Type.STRING;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.devoware.json.lexer.LexicalAnalyzer;
import org.devoware.json.lexer.LexicalAnalyzerFactory;
import org.devoware.json.model.BooleanValue;
import org.devoware.json.model.JsonArray;
import org.devoware.json.model.JsonNode;
import org.devoware.json.model.JsonObject;
import org.devoware.json.model.NullValue;
import org.devoware.json.model.NumberValue;
import org.devoware.json.model.StringValue;
import org.devoware.json.symbols.NumberToken;
import org.devoware.json.symbols.StringToken;
import org.devoware.json.symbols.Token;
import org.devoware.json.symbols.Token.Type;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class ParserImpl implements Parser {

  private final LexicalAnalyzerFactory factory;
  private LexicalAnalyzer lexer;
  private Token token;
  
  static ParserImpl create (LexicalAnalyzerFactory factory) {
    return new ParserImpl(factory);
  }

  private ParserImpl(LexicalAnalyzerFactory factory) {
    this.factory = requireNonNull(factory, "factory cannot be null");
  }
  
  @Override
  public <T extends JsonNode> T parse(String expression) {
    try (Reader in = new StringReader(expression)) {
      return parse(in);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T extends JsonNode> T parse(Reader in) {
    lexer = this.factory.create(in);
    nextToken();
    JsonNode c = jsonObject();
    expect(EOF);
    return (T) c;
  }
  
  private JsonNode jsonObject() {
    if (token.getType() == LEFT_CURLY_BRACKET) {
      nextToken();
      Map<StringValue,JsonNode> properties = Maps.newLinkedHashMap();
      if (token.getType() != RIGHT_CURLY_BRACKET) {
        boolean firstLoop = true;
        do {
          if (firstLoop) {
            firstLoop = false;
          } else {
            nextToken();
          }
          Entry<StringValue, JsonNode> property = jsonProperty();
          properties.put(property.getKey(), property.getValue());
        } while (token.getType() == COMMA);
        expect(RIGHT_CURLY_BRACKET);
      }
      nextToken();
      return new JsonObject(properties);
    }
    return jsonArray();
  }
  
  private JsonNode jsonArray() {
    if (token.getType() == LEFT_SQUARE_BRACKET) {
      nextToken();
      List<JsonNode> elements = Lists.newArrayList();
      if (token.getType() != RIGHT_SQUARE_BRACKET) { 
        boolean firstLoop = true;
        do {
          if (firstLoop) {
            firstLoop = false;
          } else {
            nextToken();
          }
          elements.add(jsonObject());
        } while (token.getType() == COMMA);
        expect(RIGHT_SQUARE_BRACKET);
      }
      nextToken();
      return new JsonArray(elements);
    }
    return jsonValue();
  }
  
  private Entry<StringValue, JsonNode> jsonProperty () {
    expect(STRING);
    StringValue key = new StringValue(((StringToken) token).value());
    nextToken();
    expect(COLON);
    nextToken();
    JsonNode value = jsonObject();
    return new Entry<StringValue, JsonNode>() {

      @Override
      public StringValue getKey() {
        return key;
      }

      @Override
      public JsonNode getValue() {
        return value;
      }

      @Override
      public JsonNode setValue(JsonNode value) {
        throw new UnsupportedOperationException();
      }
      
    };
  }
  
  private JsonNode jsonValue() {
    switch (token.getType()) {
      case TRUE:
        nextToken();
        return BooleanValue.TRUE;
      case FALSE:
        nextToken();
        return BooleanValue.FALSE;
      case NULL:
        nextToken();
        return NullValue.NULL;
      case NUMBER:
        NumberToken n = NumberToken.class.cast(token);
        nextToken();
        return new NumberValue(n.value());
      case STRING:
        StringToken s = StringToken.class.cast(token);
        nextToken();
        return new StringValue(s.value());
      default:
        throw new SyntaxException("Syntax error at " + token.getPosition() + ": found " + token.getType() + " when expecting a value");
    }
  }

  private void nextToken () {
    token = lexer.nextToken();
  }

  private void expect(Type type) {
    if (token.getType() != type) {
      throw new SyntaxException("Syntax error at " + token.getPosition() + ": found " + token.getType() + " when expecting " + type);
    }
  }

}
