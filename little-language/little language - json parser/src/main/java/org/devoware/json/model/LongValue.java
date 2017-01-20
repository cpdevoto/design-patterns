package org.devoware.json.model;

public class LongValue extends AbstractValue<Long> implements JsonNode {
  
  public LongValue(long value) {
    super(Type.LONG, value);
  }

}
