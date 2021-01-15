package com.resolute.okhttp3.simple;

import static java.util.Objects.requireNonNull;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;

import javax.net.ssl.X509TrustManager;

public class TrustManagers {

  private static final class Initializer {
    private static final X509TrustManager trustAllCerts;

    static {
      trustAllCerts = new X509TrustManager() {
        @Override
        public void checkClientTrusted(java.security.cert.X509Certificate[] chain,
            String authType) throws CertificateException {}

        @Override
        public void checkServerTrusted(java.security.cert.X509Certificate[] chain,
            String authType) throws CertificateException {}

        @Override
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
          return new java.security.cert.X509Certificate[] {};
        }
      };
    }

  }

  public static X509TrustManager trustAllCerts() {
    return Initializer.trustAllCerts;
  }

  public static X509TrustManager trustCert(X509Certificate cert) {
    requireNonNull(cert, "cert cannot be null");
    return new X509TrustManager() {
      @Override
      public void checkClientTrusted(X509Certificate[] chain,
          String authType) throws CertificateException {}

      @Override
      public void checkServerTrusted(X509Certificate[] chain,
          String authType) throws CertificateException {
        Arrays.stream(chain)
            .filter(c -> cert.equals(c))
            .findFirst()
            .orElseThrow(() -> {
              return new CertificateException("No trusted certificate found.");
            });

      }

      @Override
      public X509Certificate[] getAcceptedIssuers() {
        return new java.security.cert.X509Certificate[] {cert};
      }
    };
  }

  private TrustManagers() {}

}
