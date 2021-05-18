package org.devoware.table;

public class TableLoadException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public TableLoadException() {}

  public TableLoadException(String message) {
    super(message);
  }

  public TableLoadException(Throwable cause) {
    super(cause);
  }

  public TableLoadException(String message, Throwable cause) {
    super(message, cause);
  }

  public TableLoadException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
