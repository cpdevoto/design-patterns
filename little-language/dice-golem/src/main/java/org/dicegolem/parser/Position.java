package org.dicegolem.parser;

class Position {
  private int line = 1;
  private int character = 0;

  static Position copyOf(Position position) {
    return new Position(position.getLine(), position.getCharacter());
  }

  Position(int line, int character) {
    this.line = line;
    this.character = character;
  }

  Position() {}

  Position advanceCharacter() {
    character += 1;
    return this;
  }

  Position advanceLine() {
    line += 1;
    character = 0;
    return this;
  }

  int getLine() {
    return line;
  }

  int getCharacter() {
    return character;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + character;
    result = prime * result + line;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Position other = (Position) obj;
    if (character != other.character)
      return false;
    if (line != other.line)
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "line " + line + ", character " + character;
  }

}
