package org.devoware.table;

public class TableFileParseException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public TableFileParseException() {}

  public TableFileParseException(String message) {
    super(message);
  }

  public TableFileParseException(String format, Object... args) {
    super(String.format(format, args));
  }

  public TableFileParseException(Throwable cause) {
    super(cause);
  }

}
