package org.devoware.dicegolem;

import static java.util.Objects.requireNonNull;

import org.devoware.dicegolem.Token.Type;

public class UnexpectedTokenException extends SyntaxException {

  private static final long serialVersionUID = 1L;
  private final Token token;
  private final Type expectedType;

  public UnexpectedTokenException(Token token, Token.Type expectedType) {
    super("Syntax error at " + token.getPosition() + ": found "
        + token.getType() + " when expecting " + expectedType);
    this.token = requireNonNull(token, "token cannot be null");
    this.expectedType = requireNonNull(expectedType, "expectedType cannot be null");
  }

  public Token getToken() {
    return token;
  }

  public Type getExpectedType() {
    return expectedType;
  }

}
