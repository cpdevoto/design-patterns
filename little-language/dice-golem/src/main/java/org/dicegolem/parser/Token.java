package org.dicegolem.parser;

import static java.util.Objects.requireNonNull;

class Token {
  static enum Type {
    // @formatter:off
    DIE("'d'"), 
    PLUS("'+'"), 
    MINUS("'-'"), 
    KEEP_HIGHEST("kh"), 
    KEEP_LOWEST("kl"), 
    DROP_HIGHEST("dh"), 
    DROP_LOWEST("dl"),
    REROLL_ONCE("ro<"),
    NUMBER("a number"), 
    LEFT_PAREN("'('"), 
    RIGHT_PAREN("')'"), 
    EOF("end of string");
    // @formatter:on

    private final String name;

    private Type(String name) {
      this.name = name;
    }

    public String toString() {
      return name;
    }
  }

  private final Position position;
  private final Type type;

  Token(Position position, Type type) {
    this.position = requireNonNull(position, "position cannot be null");
    this.type = requireNonNull(type, "type cannot be null");
  }

  final Type getType() {
    return type;
  }

  final Position getPosition() {
    return position;
  }

}
