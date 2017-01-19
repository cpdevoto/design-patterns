package org.devoware.json.symbols;

import org.devoware.json.lexer.Position;

public class StringToken extends Token {

  private final String value;
  
  public StringToken(String value, Position position) {
    super(Type.STRING, position);
    this.value = value;
  }
  
  public String value () {
    return value;
  }
  
}
