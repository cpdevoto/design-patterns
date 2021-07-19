package com.resolute.search;

import static java.util.Objects.requireNonNull;

import java.util.Optional;

class Token {
  private final TokenType type;
  private final Position position;
  private final String lexeme;

  Token(Position position, TokenType type) {
    this.position = requireNonNull(position, "position cannot be null");
    this.type = requireNonNull(type, "type cannot be null");
    this.lexeme = null;
  }

  Token(Position position, TokenType type, String lexeme) {
    this.position = requireNonNull(position, "position cannot be null");
    this.type = requireNonNull(type, "type cannot be null");
    this.lexeme = requireNonNull(lexeme, "lexeme cannot be null");
  }

  TokenType getType() {
    return type;
  }

  Position getPosition() {
    return position;
  }

  Optional<String> getLexeme() {
    return Optional.ofNullable(lexeme);
  }

  @Override
  public String toString() {
    if (lexeme != null) {
      return lexeme;
    }
    return type.getValue();
  }
}
