package org.devoware.json.symbols;

import org.devoware.json.lexer.Position;

public class LongToken extends Token {

  private final long value;
  
  public LongToken(long value, Position position) {
    super(Type.LONG, position);
    this.value = value;
  }
  
  public long value () {
    return value;
  }
  
}
