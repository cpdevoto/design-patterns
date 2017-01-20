package org.devoware.json.symbols;

import org.devoware.json.lexer.Position;

public class DoubleToken extends Token {

  private final double value;
  
  public DoubleToken(double value, Position position) {
    super(Type.DOUBLE, position);
    this.value = value;
  }
  
  public double value () {
    return value;
  }
  
}
