package org.devoware.dicegolem;

import java.util.Objects;

public class LexicalAnalysisException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  private final char character;
  private final Position position;

  public LexicalAnalysisException(char character, Position position) {
    super("Unexpected character '" + character + "' at " + position);
    Objects.requireNonNull(position, "position cannot be null");
    this.character = character;
    this.position = position;
  }

  char getCharacter() {
    return character;
  }

  Position getPosition() {
    return position;
  }



}
