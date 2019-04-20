package com.resolute.jdbc.simple;

public class DataUpdateException extends DataAccessException {

  private static final long serialVersionUID = 1L;

  public DataUpdateException() {}

  public DataUpdateException(String message) {
    super(message);
  }

  public DataUpdateException(Throwable cause) {
    super(cause);
  }

  public DataUpdateException(String message, Throwable cause) {
    super(message, cause);
  }

  public DataUpdateException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
