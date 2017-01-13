package org.devoware.simplesearch.lexer;

public class LexicalAnalysisException extends RuntimeException {

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
