package org.devoware.dice;

public class SyntaxException extends ParseException {

  private static final long serialVersionUID = 1L;

  public SyntaxException() {}

  public SyntaxException(String message) {
    super(message);
  }

  public SyntaxException(Throwable cause) {
    super(cause);
  }

  public SyntaxException(String message, Throwable cause) {
    super(message, cause);
  }

  public SyntaxException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
