package com.resolutebi.baseline.expr;

import static java.util.Objects.requireNonNull;

class BooleanLiteral implements Expression<Boolean> {
  static final BooleanLiteral TRUE = new BooleanLiteral(true);
  static final BooleanLiteral FALSE = new BooleanLiteral(false);

  private final boolean value;
  
  private BooleanLiteral(boolean value) {
    this.value = value;
  }

  @Override
  public Boolean evaluate(Inputs inputs) {
    requireNonNull(inputs, "inputs cannot be null");
    return value;
  }

  @Override
  public Class<Boolean> getType() {
    return Boolean.class;
  }

  boolean getValue() {
    return value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }

}
