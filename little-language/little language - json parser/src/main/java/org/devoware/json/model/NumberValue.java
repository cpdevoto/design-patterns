package org.devoware.json.model;

public class NumberValue extends AbstractValue<Double> implements JsonNode {
  
  public NumberValue(double value) {
    super(Type.NUMBER, value);
  }

}
