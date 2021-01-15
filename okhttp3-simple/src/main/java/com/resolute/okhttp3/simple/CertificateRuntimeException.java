package com.resolute.okhttp3.simple;

public class CertificateRuntimeException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public CertificateRuntimeException(Throwable cause) {
    super(cause);
  }

  public CertificateRuntimeException(String message) {
    super(message);
  }
}
