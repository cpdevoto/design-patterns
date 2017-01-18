package org.devoware.dieroll.lexer;

public class NumberToken extends Token {

  private final int value;
  
  public NumberToken(int value) {
    super(Type.NUMBER);
    this.value = value;
  }
  
  public int getValue () {
    return value;
  }

}
