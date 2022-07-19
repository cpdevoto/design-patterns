package org.devoware.dicegolem.dice;

import static java.util.Objects.requireNonNull;

public class InvalidDieTypeException extends SyntaxException {

  private static final long serialVersionUID = 1L;
  private final Token token;
  private final int dieType;

  public InvalidDieTypeException(Token token, int dieType) {
    super("Syntax error at " + token.getPosition() + ": invalid "
        + " die type " + dieType);
    this.token = requireNonNull(token, "token cannot be null");
    this.dieType = dieType;
  }

  public Token getToken() {
    return token;
  }

  public int getDieType() {
    return dieType;
  }

}
