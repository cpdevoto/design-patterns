package com.resolute.okhttp3.simple;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.Proxy;
import java.net.URL;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class X509Certificates {
  private static final Logger log = LoggerFactory.getLogger(X509Certificates.class);

  private static final Base64.Encoder encoder = Base64.getEncoder();
  private static final Base64.Decoder decoder = Base64.getDecoder();
  private static final CertificateFactory factory;

  static {
    try {
      factory = CertificateFactory.getInstance("X.509");
    } catch (CertificateException e) {
      throw new CertificateRuntimeException(e);
    }
  }


  public static X509Certificate getCertificate(String url)
      throws CertificateValidationException {
    return getCertificate(url, null);
  }

  public static X509Certificate getCertificate(String url, Proxy proxy)
      throws CertificateValidationException {
    requireNonNull(url, "url cannot be null");
    checkArgument(url.toLowerCase().startsWith("https://"), "url must use the HTTPS protocol");
    SSLSocketFactory socketFactory = SslSocketFactories.trustAllCertsFactory();
    HostnameVerifier hostnameVerifier = HostnameVerifiers.trustAllHostnames();

    X509Certificate cert = null;
    try {
      URL destinationURL = new URL(url);

      HttpsURLConnection conn =
          (HttpsURLConnection) (proxy == null ? destinationURL.openConnection()
              : destinationURL.openConnection(proxy));
      conn.setSSLSocketFactory(socketFactory);
      conn.setHostnameVerifier(hostnameVerifier);
      conn.setConnectTimeout(5000);

      conn.connect();
      Certificate[] certs = conn.getServerCertificates();
      if (certs.length < 1) {
        throw new CertificateValidationException("Expected at least one certificate");
      } else if (!X509Certificate.class.isInstance(certs[0])) {
        throw new CertificateValidationException("Unknown certificate type: " + certs[0]);
      }
      cert = (X509Certificate) certs[0];
      try {
        cert.checkValidity();
      } catch (CertificateExpiredException cee) {
        // RP-6482 The NHaystack connector should accept expired certificates
        // throw new CertificateValidationException("Certificate is expired", cee);
        log.warn("Certificate is expired", cee);
      }
    } catch (Exception e) {
      throw new CertificateValidationException(e);
    }
    return cert;
  }

  public static String serialize(X509Certificate cert) {
    requireNonNull(cert, "cert cannot be null");
    try {
      return encoder.encodeToString(cert.getEncoded());
    } catch (CertificateEncodingException e) {
      throw new CertificateRuntimeException(e);
    }
  }

  public static X509Certificate deserialize(String cert) {
    requireNonNull(cert, "cert cannot be null");
    try (ByteArrayInputStream in = new ByteArrayInputStream(decoder.decode(cert))) {
      return (X509Certificate) factory.generateCertificate(in);
    } catch (CertificateException | IOException e) {
      throw new CertificateRuntimeException(e);
    }

  }

  private X509Certificates() {}

}
