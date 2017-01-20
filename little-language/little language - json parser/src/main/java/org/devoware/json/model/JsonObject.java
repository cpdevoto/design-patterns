package org.devoware.json.model;

import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public class JsonObject extends AbstractJsonNode implements JsonNode {
  private final Map<StringValue,JsonNode> valueProperties;
  private final Map<String,JsonNode> properties;
  
  public static Builder builder () {
    return new Builder();
  }

  public JsonObject(Map<StringValue,JsonNode> properties) {
    super(Type.OBJECT);
    requireNonNull(properties, "properties cannot be null");
    this.valueProperties = ImmutableMap.copyOf(properties);
    Map<String,JsonNode> props = properties.entrySet().stream()
        .collect(Collectors.toMap(e -> e.getKey().value(), Entry::getValue));
    this.properties = ImmutableMap.copyOf(props);
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder("{");
    boolean firstLoop = true;
    for (Entry<StringValue,JsonNode> property : valueProperties.entrySet()) {
      if (firstLoop) {
        firstLoop = false;
      } else {
        buf.append(",");
      }
      buf.append(property.getKey().toString());
      buf.append(":");
      buf.append(property.getValue().toString());
    }
    buf.append("}");
    return buf.toString();
  }

  public int size () {
    return properties.size();
  }
  
  public boolean isEmpty() {
    return properties.isEmpty();
  }
  
  @SuppressWarnings({"unchecked", "rawtypes"})
  public <T> T get (String name) {
    Object node = properties.get(name);
    if (node instanceof Value) {
      return (T) ((Value) node).value();
    }
    return (T) properties.get(name);
  }

  public Set<String> keySet () {
    return properties.keySet();
  }
  
  public Collection<JsonNode> values () {
    return properties.values();
  }
  
  public Set<Entry<String,JsonNode>> entrySet () {
    return properties.entrySet();
  }
  
  public static class Builder {
    private final Map<StringValue,JsonNode> properties = Maps.newLinkedHashMap();
    
    private Builder () {}
    
    public Builder withProperty(String name, JsonObject value) {
      requireNonNull(name, "name cannot be null");
      if (value == null) {
        this.properties.put(new StringValue(name), NullValue.NULL);       
      } else {
        this.properties.put(new StringValue(name), value);
      }
      return this;
    }
    
    public Builder withProperty(String name, JsonArray value) {
      requireNonNull(name, "name cannot be null");
      if (value == null) {
        this.properties.put(new StringValue(name), NullValue.NULL);       
      } else {
        this.properties.put(new StringValue(name), value);
      }
      return this;
    }

    public Builder withProperty(String name, String value) {
      requireNonNull(name, "name cannot be null");
      if (value == null) {
        this.properties.put(new StringValue(name), NullValue.NULL);       
      } else {
        this.properties.put(new StringValue(name), new StringValue(value));
      }
      return this;
    }
    
    public Builder withProperty(String name, double value) {
      requireNonNull(name, "name cannot be null");
      this.properties.put(new StringValue(name), new DoubleValue(value));
      return this;
    }

    public Builder withProperty(String name, boolean value) {
      requireNonNull(name, "name cannot be null");
      this.properties.put(new StringValue(name), BooleanValue.get(value));
      return this;
    }
    
    public JsonObject build () {
      return new JsonObject(this.properties);
    }

  }

}
