package com.resolute.okhttp3.simple;

import static java.util.Objects.requireNonNull;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;

public abstract class AbstractHttpRequestFactory {
  private final String baseUrl;
  private final OkHttpClient client;

  protected <F extends AbstractHttpRequestFactory, B extends Builder<F, B>> AbstractHttpRequestFactory(
      Builder<F, B> builder) {
    this.baseUrl = builder.baseUrl;
    OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
        .proxy(builder.proxy)
        .connectTimeout(builder.connectTimeout.timeout, builder.connectTimeout.unit)
        .readTimeout(builder.readTimeout.timeout, builder.readTimeout.unit);
    if (builder.sslSocketFactory != null) {
      clientBuilder.sslSocketFactory(builder.sslSocketFactory, builder.trustManager);
    }
    if (builder.hostnameVerifier != null) {
      clientBuilder.hostnameVerifier(builder.hostnameVerifier);
    }
    this.client = clientBuilder.build();
    // dispatcher setting only after client is built
    if (builder.maxRequests != null)
      this.client.dispatcher().setMaxRequests(builder.maxRequests);
    if (builder.maxRequestsPerHost != null)
      this.client.dispatcher().setMaxRequestsPerHost(builder.maxRequestsPerHost);
  }

  String getBaseUrl() {
    return baseUrl;
  }

  OkHttpClient getClient() {
    return client;
  }

  public abstract static class Builder<F extends AbstractHttpRequestFactory, B extends Builder<F, B>> {
    private String baseUrl;
    private Timeout connectTimeout = new Timeout(30, TimeUnit.SECONDS);
    private Timeout readTimeout = new Timeout(30, TimeUnit.SECONDS);
    private Proxy proxy = Proxy.NO_PROXY;
    private SSLSocketFactory sslSocketFactory;
    private X509TrustManager trustManager;
    private HostnameVerifier hostnameVerifier;
    private Integer maxRequests = null, maxRequestsPerHost = null;

    protected Builder(String baseUrl) {
      this.baseUrl = requireNonNull(baseUrl, "baseUrl cannot be null");
    }

    public B withConnectTimeout(int timeout, TimeUnit unit) {
      requireNonNull(unit, "unit cannot be null");
      this.connectTimeout = new Timeout(timeout, unit);
      return getThis();
    }

    public B withReadTimeout(int timeout, TimeUnit unit) {
      requireNonNull(unit, "unit cannot be null");
      this.readTimeout = new Timeout(timeout, unit);
      return getThis();
    }

    public B withProxy(String proxyHost, int proxyPort) {
      requireNonNull(proxyHost, "proxyHost cannot be null");
      this.proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
      return getThis();
    }

    public B withSslSocketFactory(SSLSocketFactory sslSocketFactory,
        X509TrustManager trustManager) {
      requireNonNull(sslSocketFactory, "sslSocketFactory cannot be null");
      requireNonNull(trustManager, "trustManager cannot be null");
      this.sslSocketFactory = sslSocketFactory;
      this.trustManager = trustManager;
      return getThis();
    }

    public B withHostnameVerifier(HostnameVerifier hostnameVerifier) {
      requireNonNull(hostnameVerifier, "hostnameVerifier cannot be null");
      this.hostnameVerifier = hostnameVerifier;
      return getThis();
    }

    public B withMaxRequests(int maxRequests) {
      this.maxRequests = maxRequests;
      return getThis();
    }

    public B withMaxRequestsPerHost(int maxRequestsPerHost) {
      this.maxRequestsPerHost = maxRequestsPerHost;
      return getThis();
    }

    public F build() {
      return newInstance();
    }

    protected abstract B getThis();

    protected abstract F newInstance();

  }

  private static class Timeout {
    private int timeout;
    private TimeUnit unit;

    private Timeout(int timeout, TimeUnit unit) {
      this.timeout = timeout;
      this.unit = unit;
    }
  }

}
