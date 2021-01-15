package com.resolute.okhttp3.simple;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

public class HostnameVerifiers {

  private static final class Initializer {
    private static final HostnameVerifier trustAllHostnames;

    static {
      trustAllHostnames = new HostnameVerifier() {
        @Override
        public boolean verify(String hostname, SSLSession session) {
          return true;
        }
      };
    }
  }

  public static HostnameVerifier trustAllHostnames() {
    return Initializer.trustAllHostnames;
  }

  private HostnameVerifiers() {}

}
