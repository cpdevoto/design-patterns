package com.resolutebi.testutils.dockerdb;

public class ApplicationConfigLoadException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public ApplicationConfigLoadException(Throwable cause) {
    super(cause);
  }

  public ApplicationConfigLoadException(String message) {
    super(message);
  }

}
