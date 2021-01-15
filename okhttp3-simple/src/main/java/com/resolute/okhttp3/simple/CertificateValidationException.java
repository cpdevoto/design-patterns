package com.resolute.okhttp3.simple;

public class CertificateValidationException extends Exception {

  private static final long serialVersionUID = 1L;

  public CertificateValidationException() {}

  public CertificateValidationException(String message) {
    super(message);
  }

  public CertificateValidationException(Throwable cause) {
    super(cause);
  }

  public CertificateValidationException(String message, Throwable cause) {
    super(message, cause);
  }

  public CertificateValidationException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
