package com.resolute.okhttp3.simple;

public class SimpleHttpRequestFactory extends AbstractHttpRequestFactory {

  public static Builder builder(String baseUrl) {
    return new Builder(baseUrl);
  }

  private SimpleHttpRequestFactory(Builder builder) {
    super(builder);
  }

  public HttpRequestUrlBuilder<SimpleHttpRequest> newRequest() {
    return new SimpleHttpRequest(this);
  }

  public static class Builder
      extends AbstractHttpRequestFactory.Builder<SimpleHttpRequestFactory, Builder> {

    private Builder(String baseUrl) {
      super(baseUrl);
    }

    @Override
    protected Builder getThis() {
      return this;
    }

    @Override
    protected SimpleHttpRequestFactory newInstance() {
      return new SimpleHttpRequestFactory(this);
    }

  }

}
