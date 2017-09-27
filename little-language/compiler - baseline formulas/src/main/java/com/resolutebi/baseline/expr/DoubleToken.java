package com.resolutebi.baseline.expr;

class DoubleToken extends Token {

  private final double value;
  
  DoubleToken(double value, Position position) {
    super(Type.DOUBLE, position);
    this.value = value;
  }
  
  double value () {
    return value;
  }
  
}
