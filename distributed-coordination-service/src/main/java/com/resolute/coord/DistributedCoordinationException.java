package com.resolute.coord;

public class DistributedCoordinationException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public DistributedCoordinationException(String message) {
    super(message);
  }

  public DistributedCoordinationException(Throwable cause) {
    super(cause);
  }

  public DistributedCoordinationException(String message, Throwable cause) {
    super(message, cause);
  }

  public DistributedCoordinationException(String message, Throwable cause,
      boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
