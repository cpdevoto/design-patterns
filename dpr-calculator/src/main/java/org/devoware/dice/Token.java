package org.devoware.dice;

import static java.util.Objects.requireNonNull;

import java.util.Optional;

class Token {
  private final TokenType type;
  private final Optional<String> lexeme;
  private final Position position;


  Token(Position position, TokenType type) {
    this(position, type, Optional.empty());
  }

  Token(Position position, TokenType type, String lexeme) {
    this(position, type, Optional.of(requireNonNull(lexeme, "lexeme cannot be null")));
  }

  private Token(Position position, TokenType type, Optional<String> lexeme) {
    this.position = requireNonNull(position, "position cannot be null");
    this.type = requireNonNull(type, "type cannot be null");
    this.lexeme = requireNonNull(lexeme, "lexeme cannot be null");
  }

  Position getPosition() {
    return position;
  }

  TokenType getType() {
    return type;
  }

  String getLexeme() {
    return lexeme.isPresent() ? lexeme.get() : null;
  }

  @Override
  public String toString() {
    if (lexeme.isPresent()) {
      return lexeme.get();
    }
    return type.getValue();
  }
}
