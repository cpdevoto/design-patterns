package com.resolute.okhttp3.simple;

import static java.util.Objects.requireNonNull;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

public class SslSocketFactories {

  private static final class Initializer {
    private static final SSLSocketFactory trustAllCertsFactory;

    static {
      final TrustManager[] trustManagers = {TrustManagers.trustAllCerts()};
      try {
        final SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
        sslContext.init(null, trustManagers, new java.security.SecureRandom());
        // Create an ssl socket factory with our all-trusting manager
        trustAllCertsFactory = sslContext.getSocketFactory();
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }

  }

  public static SSLSocketFactory trustAllCertsFactory() {
    return Initializer.trustAllCertsFactory;
  }

  public static SSLSocketFactory trustCertFactory(TrustManager trustManager) {
    requireNonNull(trustManager, "trustManager cannot be null");
    final TrustManager[] trustManagers = {trustManager};
    try {
      final SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
      sslContext.init(null, trustManagers, null);
      // Create an ssl socket factory with our new trust manager
      return sslContext.getSocketFactory();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private SslSocketFactories() {}

}
