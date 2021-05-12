package com.resolute.dataset.cloner.log;

public class LogFileParseException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public LogFileParseException() {}

  public LogFileParseException(String message) {
    super(message);
  }

  public LogFileParseException(Throwable cause) {
    super(cause);
  }

  public LogFileParseException(String message, Throwable cause) {
    super(message, cause);
  }

  public LogFileParseException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
