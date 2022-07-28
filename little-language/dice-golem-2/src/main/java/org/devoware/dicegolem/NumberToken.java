package org.devoware.dicegolem;

class NumberToken extends Token {

  private final int value;

  NumberToken(Position position, int value) {
    super(position, Type.NUMBER);
    this.value = value;
  }

  int getValue() {
    return value;
  }

}
