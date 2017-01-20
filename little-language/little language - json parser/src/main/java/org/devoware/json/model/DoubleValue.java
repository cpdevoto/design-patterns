package org.devoware.json.model;

public class DoubleValue extends AbstractValue<Double> implements JsonNode {
  
  public DoubleValue(double value) {
    super(Type.DOUBLE, value);
  }

}
