package org.devoware.json.lexer;

class BasicPosition implements Position {
  private int line = 1;
  private int character = -1;
  
  BasicPosition(int line, int character) {
    this.line = line;
    this.character = character;
  }

  BasicPosition() {}
  
  Position advanceCharacter () {
    character += 1;
    return this;
  }

  Position advanceLine () {
    line += 1;
    character = -1;
    return this;
  }

  @Override
  public int getLine() {
    return line;
  }

  @Override
  public int getCharacter() {
    return Math.max(character, 0);
  }
  
  @Override
  public String toString() {
    return "line " + line + ", character " + character;
  }
  
}
