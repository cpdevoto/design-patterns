package com.resolutebi.testutils.docker.flume;

public class DockerDependencyLockReadException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public DockerDependencyLockReadException(Throwable cause) {
    super(cause);
  }

  public DockerDependencyLockReadException(String message) {
    super(message);
  }

}
