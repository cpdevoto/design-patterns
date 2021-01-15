package com.resolute.pojo.processor.types;

import static java.util.Objects.requireNonNull;

class Token {
  static enum Type {
    // @formatter:off
    LEFT_ANGLE_BRACKET("'<'"), 
    RIGHT_ANGLE_BRACKET("'>'"), 
    LEFT_SQUARE_BRACKET("']'"), 
    RIGHT_SQUARE_BRACKET("']'"), 
    COMMA("','"), 
    IDENTIFIER("an identifier"), 
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
