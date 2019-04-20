package org.devoware.dice;

public class LexicalAnalysisException extends ParseException {

  private static final long serialVersionUID = 1L;

  public LexicalAnalysisException() {}

  public LexicalAnalysisException(String message) {
    super(message);
  }

  public LexicalAnalysisException(Throwable cause) {
    super(cause);
  }

  public LexicalAnalysisException(String message, Throwable cause) {
    super(message, cause);
  }

  public LexicalAnalysisException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
