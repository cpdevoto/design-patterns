package com.resolute.okhttp3.simple;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

import java.security.cert.X509Certificate;

import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.testcontainers.containers.MockServerContainer;
import org.testcontainers.utility.DockerImageName;

public class X509CertificatesTest {

  private static final String serializedCert =
      "MIIDtzCCAp+gAwIBAgIEO+cY2zANBgkqhkiG9w0BAQsFADCBizELMAkGA1UEBhMCVVMxETAPBgNVBAgTCE1pY2hpZ2FuMRMwEQYDVQQHEwpCaXJtaW5naGFtMScwJQYDVQQKEx5SZXNvbHV0ZSBCdWlsZGluZyBJbnRlbGxpZ2VuY2UxEzARBgNVBAsTClRlY2hub2xvZ3kxFjAUBgNVBAMTDUNhcmxvcyBEZXZvdG8wHhcNMTkwNjE3MTQ0OTEwWhcNNDYxMTAyMTQ0OTEwWjCBizELMAkGA1UEBhMCVVMxETAPBgNVBAgTCE1pY2hpZ2FuMRMwEQYDVQQHEwpCaXJtaW5naGFtMScwJQYDVQQKEx5SZXNvbHV0ZSBCdWlsZGluZyBJbnRlbGxpZ2VuY2UxEzARBgNVBAsTClRlY2hub2xvZ3kxFjAUBgNVBAMTDUNhcmxvcyBEZXZvdG8wggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCHVD2Dbo1xyOFMyACwJy6HofI6+u0dCGqxbzpsfKm/ejnmeB1BUjCFPTrjhtAREz2L4rbneBL3BfNLKbqdlialAQPe/P+FyLso2abVYCiR/sSolQvuG7rpNTzl/HrErrpfIgOfqdkiAssg85Iq4UiErwt5ql5VeLaukpsmiVOxeqDjHpHpVkyMTOq+AFXEMYhI7lni32gTM59sE3N8U70LXNPtDtYGWyPxqeQRDZlAybwH/zbHXfXTUAJx85VmcujflzUo2VoGjlrkPbDSGixJiinP7V5f8LRaM+MFpuvDTGVtFJPmsI2SQUNghFJaDsBgvOFCxjD8tZmwELlOtzjLAgMBAAGjITAfMB0GA1UdDgQWBBS8v00M05gOoraPGAKn+49s9g1sjzANBgkqhkiG9w0BAQsFAAOCAQEADIwHvpAfy9p2jMyiRtmzRhUVGJF5u7bsdTVZJRSG2IgBKZU1ktFwaHh6sZHfT/shEAU8TbrC9jJKnVIoLpnYFaUPVQEwAnjrYC465Xcr/Qkwkm8z370Qwv2to0/GyIK0wJES/IvhodJMttPb8jDYHlhcgefk/I3SSuifhhnFYgEIeHbuuEPGgD2npQ6Mp3yOl2vIuxxy1OeQbwJs/fxy4OlsKcMWzoata7UMcCuikcEHMjga1S1Qu3Hywj1FBLdmOQM8KOSWZsYv4xqzB8EgXAWBS4S7lG9YwmdB/tPjpm32X4OvZSeaNscYaEftqkp5Xm0fkIjuZFr9+8FffElKcw==";

  @Test
  public void test_get_certificate() throws Exception {
    launchWebServer(X509CertificatesTest::configureServer, (host, port) -> {

      // The MockServer generates a random cert dynamically every time and one cannot easily
      // configure it to use a fixed certificate. The best we can do therefore is validate that no
      // exception is thrown.
      assertThatNoException().isThrownBy(() -> {
        X509Certificate actualCert =
            X509Certificates.getCertificate("https://" + host + ":" + port);
        assertThat(actualCert).isNotNull();
      });
    });
  }

  @Test
  public void test_get_serialize_and_deserialize() throws Exception {
    launchWebServer(X509CertificatesTest::configureServer, (host, port) -> {

      X509Certificate actualCert = X509Certificates.deserialize(serializedCert);
      assertThat(actualCert).isNotNull();

      String actualSerializedCert = X509Certificates.serialize(actualCert);
      assertThat(actualSerializedCert).isNotNull().isEqualTo(serializedCert);


    });
  }

  // ---------------------
  // Helper Functions
  // ---------------------

  private static void launchWebServer(HttpServerConfigFunction serverConfigFunction,
      HttpClientTestFunction testFunction) throws Exception {
    try (MockServerContainer mockServer = new MockServerContainer(
        DockerImageName.parse("mockserver/mockserver:mockserver-5.11.2"))) {
      mockServer.start();
      MockServerClient client =
          new MockServerClient(mockServer.getHost(), mockServer.getServerPort());
      serverConfigFunction.apply(client);
      testFunction.run(mockServer.getHost(), mockServer.getServerPort());

    }
  }

  private static void configureServer(MockServerClient client) {
    // There is not configuration required
  }

  // ---------------------
  // Helper Classes
  // ---------------------

  @FunctionalInterface
  public static interface HttpServerConfigFunction {
    public void apply(MockServerClient client) throws Exception;
  }

  @FunctionalInterface
  public static interface HttpClientTestFunction {
    public void run(String host, int port) throws Exception;
  }

}
