package org.devoware.json.symbols;

import org.devoware.json.lexer.Position;

public class NumberToken extends Token {

  private final Double value;
  
  public NumberToken(double value, Position position) {
    super(Type.NUMBER, position);
    this.value = value;
  }
  
  public double value () {
    return value;
  }
  
}
