package org.devoware.json.lexer;



public interface Position {
  
  public static Position copyOf(Position position) {
    return new BasicPosition(position.getLine(), position.getCharacter());
  }

  public int getLine();
  public int getCharacter();
  
}
