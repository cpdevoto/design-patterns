package org.devoware.json.model;

public class BooleanValue extends AbstractValue<Boolean> implements JsonNode {
  public static final BooleanValue TRUE = new BooleanValue(true);
  public static final BooleanValue FALSE = new BooleanValue(false);

  private BooleanValue(boolean value) {
    super(Type.BOOLEAN, value);
  }

}
