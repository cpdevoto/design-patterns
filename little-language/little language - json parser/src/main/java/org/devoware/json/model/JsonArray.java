package org.devoware.json.model;

import static java.util.Objects.requireNonNull;

import java.util.Iterator;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class JsonArray extends AbstractJsonNode implements Iterable<JsonNode> {
  private final List<JsonNode> elements;

  public static Builder builder () {
    return new Builder();
  }
  
  public JsonArray(List<JsonNode> elements) {
    super(Type.ARRAY);
    requireNonNull(elements, "elements cannot be null");
    this.elements = ImmutableList.copyOf(elements);
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder("[");
    boolean firstLoop = true;
    for (JsonNode element : elements) {
      if (firstLoop) {
        firstLoop = false;
      } else {
        buf.append(",");
      }
      buf.append(element.toString());
    }
    buf.append("]");
    return buf.toString();
  }

  @Override
  public Iterator<JsonNode> iterator() {
    return elements.iterator();
  }
  
  public int size () {
    return elements.size();
  }
 
  public boolean isEmpty() {
    return elements.isEmpty();
  }
  
  @SuppressWarnings({"unchecked", "rawtypes"})
  public <T> T get (int idx) {
    Object node = elements.get(idx);
    if (node instanceof Value) {
      return (T) ((Value) node).value();
    }
    return (T) node;
  }

  public static class Builder {
    private final List<JsonNode> elements = Lists.newArrayList();
    
    private Builder () {}
    
    public Builder withElement(JsonObject value) {
      if (value == null) {
        this.elements.add(NullValue.NULL);
      } else {
        this.elements.add(value);
      }
      return this;
    }
    
    public Builder withElement(JsonArray value) {
      if (value == null) {
        this.elements.add(NullValue.NULL);
      } else {
        this.elements.add(value);
      }
      return this;
    }
    
    public Builder withElement(String value) {
      if (value == null) {
        this.elements.add(NullValue.NULL);
      } else {
        this.elements.add(new StringValue(value));
      }
      return this;
    }
    
    public Builder withElement(double value) {
      this.elements.add(new DoubleValue(value));
      return this;
    }

    public Builder withElement(boolean value) {
      this.elements.add(BooleanValue.get(value));
      return this;
    }

    public JsonArray build () {
      return new JsonArray(this.elements);
    }

  }
}
