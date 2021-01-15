package com.resolute.pojo.processor.types;

class IdentifierToken extends Token {

  private final String value;

  IdentifierToken(Position position, String value) {
    super(position, Type.IDENTIFIER);
    this.value = value;
  }

  String getValue() {
    return value;
  }

}
