package com.resolute.utils.simple;

public class ParallelExecutionException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public ParallelExecutionException() {
    super();
  }

  public ParallelExecutionException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public ParallelExecutionException(String message, Throwable cause) {
    super(message, cause);
  }

  public ParallelExecutionException(String message) {
    super(message);
  }

  public ParallelExecutionException(Throwable cause) {
    super(cause);
  }
}
