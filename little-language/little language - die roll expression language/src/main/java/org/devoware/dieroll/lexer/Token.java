package org.devoware.dieroll.lexer;

import static java.util.Objects.requireNonNull;

public class Token {
  public static enum Type {
    DIE,
    PLUS,
    MINUS,
    KEEP_HIGHEST,
    KEEP_LOWEST,
    DROP_HIGHEST,
    DROP_LOWEST,
    NUMBER,
    LEFT_PAREN,
    RIGHT_PAREN,
    EOF
  }

  private final Type type;

  public Token(Type type) {
    this.type = requireNonNull(type, "type cannot be null");
  }
  
  public final Type getType() {
    return type;
  }

}
