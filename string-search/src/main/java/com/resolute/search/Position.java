package com.resolute.search;

import java.util.Objects;

class Position {
  private final int line;
  private final int character;

  static Position copyOf(Position position) {
    return new Position(position.getLine(), position.getCharacter());
  }

  Position(int line, int character) {
    this.line = line;
    this.character = character;
  }

  Position() {
    this(1, 0);
  }

  Position advanceCharacter() {
    return new Position(line, character + 1);
  }

  Position advanceLine() {
    return new Position(line + 1, 0);
  }

  int getLine() {
    return line;
  }

  int getCharacter() {
    return character;
  }

  @Override
  public int hashCode() {
    return Objects.hash(character, line);
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
    return character == other.character && line == other.line;
  }

  @Override
  public String toString() {
    return "line " + line + ", character " + character;
  }


}
