package com.resolute.okhttp3.simple;

public class SimpleHttpRequest
    extends AbstractHttpRequest<SimpleHttpRequest> {

  SimpleHttpRequest(SimpleHttpRequestFactory factory) {
    super(factory);
  }

  @Override
  protected SimpleHttpRequest getThis() {
    return this;
  }

}
