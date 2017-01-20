package org.devoware.json.model;

public class BooleanValue extends AbstractValue<Boolean> implements JsonNode {
  public static final BooleanValue TRUE = new BooleanValue(true);
  public static final BooleanValue FALSE = new BooleanValue(false);
  
  public static BooleanValue get (boolean value) {
    if (value) {
      return TRUE;
    }
    return FALSE;
  }

  private BooleanValue(boolean value) {
    super(Type.BOOLEAN, value);
  }

}
