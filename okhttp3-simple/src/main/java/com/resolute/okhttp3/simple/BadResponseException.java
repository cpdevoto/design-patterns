package com.resolute.okhttp3.simple;

import static java.util.Objects.requireNonNull;

import java.io.IOException;

public class BadResponseException extends IOException {

  private static final long serialVersionUID = 1L;

  private HttpResponse response;

  public BadResponseException(String message, HttpResponse response) {
    super(message);
    this.response = requireNonNull(response, "response cannot be null");
  }

  public HttpResponse getResponse() {
    return response;
  }

}
