package org.devoware.dao;

public class DatabaseAccessException extends RuntimeException {

  public DatabaseAccessException() {}

  public DatabaseAccessException(String message) {
    super(message);
  }

  public DatabaseAccessException(Throwable cause) {
    super(cause);
  }

  public DatabaseAccessException(String message, Throwable cause) {
    super(message, cause);
  }

  public DatabaseAccessException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
