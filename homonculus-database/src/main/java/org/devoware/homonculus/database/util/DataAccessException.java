package org.devoware.homonculus.database.util;

public class DataAccessException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public DataAccessException() {}

  public DataAccessException(String message) {
    super(message);
  }

  public DataAccessException(Throwable cause) {
    super(cause);
  }

  public DataAccessException(String message, Throwable cause) {
    super(message, cause);
  }

  public DataAccessException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
