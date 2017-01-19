package org.devoware.json.model;

public class NullValue extends AbstractValue<Object> implements JsonNode {
  
  public static final NullValue NULL = new NullValue();

  private NullValue() {
    super(Type.NULL, null);
  }


}
