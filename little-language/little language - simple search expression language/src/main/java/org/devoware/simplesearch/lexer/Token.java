package org.devoware.simplesearch.lexer;

import static java.util.Objects.requireNonNull;

import java.util.Optional;

public class Token {
  
  public static enum Type {
    OR,
    AND,
    NOT,
    LEFT_PAREN,
    RIGHT_PAREN,
    WORD,
    QUOTED_STRING,
    EOF
  }
  
  static final Token EOF = create(Type.EOF);
  static final Token LEFT_PAREN = create(Type.LEFT_PAREN, "(");
  static final Token RIGHT_PAREN = create(Type.RIGHT_PAREN, ")");

  private final Type type;
  private final Optional<String> lexeme;
  
  private static Token create (Type type) {
    return create(type, null);
  }
  
  static Token create (Type type, String lexeme) {
    requireNonNull(type, "type cannot be null");
    Optional<String> lex = Optional.empty();
    if (lexeme != null) {
      lex = Optional.of(lexeme);
    }
    return new Token(type, lex);
  }

  private Token(Type type, Optional<String> lexeme) {
    this.type = type;
    this.lexeme = lexeme;
  }
  
  public Type getType () {
    return type;
  }
  
  public boolean hasLexeme() {
    return lexeme.isPresent();
  }
  
  public String getLexeme() {
    return lexeme.get();
  }

  @Override
  public String toString() {
    return "Token [type=" + type + ", lexeme=" + lexeme + "]";
  }

}
