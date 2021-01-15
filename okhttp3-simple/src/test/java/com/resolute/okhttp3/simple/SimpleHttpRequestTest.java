package com.resolute.okhttp3.simple;

import static com.resolute.okhttp3.simple.HttpUtilsHelper.MAPPER;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.testcontainers.shaded.com.google.common.base.Preconditions.checkArgument;

import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.testcontainers.containers.MockServerContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import com.fasterxml.jackson.core.type.TypeReference;
import com.resolute.okhttp3.simple.utils.ResponseUtils;

@Testcontainers
public class SimpleHttpRequestTest {

  @Container
  private static MockServerContainer mockServer = new MockServerContainer(
      DockerImageName.parse("mockserver/mockserver:mockserver-5.11.2"));

  private static MockServerClient client;

  @BeforeAll
  public static void setup() {
    client =
        new MockServerClient(mockServer.getHost(), mockServer.getServerPort());
  }

  @Nested
  @DisplayName("httpGet")
  class HttpGet {

    @Test
    public void test_get_and_process_raw_response() throws Exception {

      HttpServerConfigFunction configFunction = client -> {
        client
            .when(request().withPath("/hello"))
            .respond(response().withBody("Hello World!"));
      };


      testWebServer(configFunction, (host, port) -> {

        SimpleHttpRequestFactory requestFactory = createHttpRequestFactory(host, port);

        String expectedResponse = "Hello World!";
        String actualResponse = requestFactory.newRequest()
            .withUrl("/hello")
            .execute(response -> response.body().string());

        assertThat(actualResponse).isEqualTo(expectedResponse);

      });
    }

    @Test
    public void test_get_and_convert_json_to_object() throws Exception {
      HttpServerConfigFunction configFunction = client -> {
        client
            .when(request().withPath("/user/invite"))
            .respond(response()
                .withBody(inviteStatusJson(
                    createInviteStatus("b558664d-fcf7-48e8-9807-e7a7614f22bc", "new"))));
      };

      testWebServer(configFunction, (host, port) -> {

        SimpleHttpRequestFactory requestFactory = createHttpRequestFactory(host, port);

        InviteStatus status = requestFactory.newRequest()
            .withUrl("/user/invite")
            .execute(InviteStatus.class);

        assertThat(status).isEqualTo(
            createInviteStatus("b558664d-fcf7-48e8-9807-e7a7614f22bc", "new"));

      });
    }

    @Test
    public void test_get_and_convert_json_to_list() throws Exception {
      HttpServerConfigFunction configFunction = client -> {
        client
            .when(request().withPath("/user/invite"))
            .respond(response()
                .withBody(inviteStatusJson(
                    createInviteStatus("b558664d-fcf7-48e8-9807-e7a7614f22bc", "new"),
                    createInviteStatus("b558664d-fcf7-48e8-9807-e7a7614f22bd", "old"))));
      };

      testWebServer(configFunction, (host, port) -> {

        SimpleHttpRequestFactory requestFactory = createHttpRequestFactory(host, port);

        List<InviteStatus> statuses = requestFactory.newRequest()
            .withUrl("/user/invite")
            .execute(new TypeReference<List<InviteStatus>>() {});

        assertThat(statuses).isNotNull().containsOnly(
            createInviteStatus("b558664d-fcf7-48e8-9807-e7a7614f22bc", "new"),
            createInviteStatus("b558664d-fcf7-48e8-9807-e7a7614f22bd", "old"));

      });
    }

    @Test
    public void test_get_invalid_url() throws Exception {
      HttpServerConfigFunction configFunction = client -> {
        client
            .when(request().withPath("/user/invite"))
            .respond(response()
                .withBody(inviteStatusJson(
                    createInviteStatus("b558664d-fcf7-48e8-9807-e7a7614f22bc", "new"))));
      };

      testWebServer(configFunction, (host, port) -> {

        SimpleHttpRequestFactory requestFactory = createHttpRequestFactory(host, port);

        ResponseUtils.assertErrorStatus(404, () -> {
          requestFactory.newRequest()
              .withUrl("/user/invitexx")
              .execute(InviteStatus.class);
        });

      });
    }

    @Test
    public void test_get_with_query_params() throws Exception {
      HttpServerConfigFunction configFunction = client -> {
        client
            .when(request().withPath("/user/invite").withQueryStringParameter("type", "admin"))
            .respond(response()
                .withBody(inviteStatusJson(
                    createInviteStatus("b558664d-fcf7-48e8-9807-e7a7614f22bc", "new"))));
      };

      testWebServer(configFunction, (host, port) -> {

        SimpleHttpRequestFactory requestFactory = createHttpRequestFactory(host, port);

        InviteStatus status = requestFactory.newRequest()
            .withUrl("/user/invite", urlBuilder -> {
              urlBuilder.addQueryParameter("type", "admin");
            })
            .execute(InviteStatus.class);

        assertThat(status).isEqualTo(
            createInviteStatus("b558664d-fcf7-48e8-9807-e7a7614f22bc", "new"));

      });
    }

    @Test
    public void test_get_with_header() throws Exception {
      HttpServerConfigFunction configFunction = client -> {
        client
            .when(request().withPath("/user/invite").withHeader("Accept", "application/json"))
            .respond(response()
                .withBody(inviteStatusJson(
                    createInviteStatus("b558664d-fcf7-48e8-9807-e7a7614f22bc", "new"))));
      };

      testWebServer(configFunction, (host, port) -> {

        SimpleHttpRequestFactory requestFactory = createHttpRequestFactory(host, port);

        InviteStatus status = requestFactory.newRequest()
            .withUrl("/user/invite")
            .addHeader("Accept", "application/json")
            .execute(InviteStatus.class);

        assertThat(status).isEqualTo(
            createInviteStatus("b558664d-fcf7-48e8-9807-e7a7614f22bc", "new"));

      });
    }

  }

  @Nested
  @DisplayName("httpsGet")
  class HttpsGet {

    @Test
    public void test_configuring_ssl_socket_factory_and_calling_get() throws Exception {

      HttpServerConfigFunction configFunction = client -> {
        client
            .when(request().withPath("/user/invite"))
            .respond(response()
                .withBody(inviteStatusJson(
                    createInviteStatus("b558664d-fcf7-48e8-9807-e7a7614f22bc", "new"))));
      };

      testWebServer(configFunction, (host, port) -> {

        SimpleHttpRequestFactory httpsRequestFactory = createHttpsRequestFactory(host, port);

        InviteStatus status = httpsRequestFactory.newRequest()
            .withUrl("/user/invite")
            .execute(InviteStatus.class);

        assertThat(status).isEqualTo(
            createInviteStatus("b558664d-fcf7-48e8-9807-e7a7614f22bc", "new"));

      });


    }

  }


  @Nested
  @DisplayName("httpPost")
  class HttpPost {

    @Test
    public void test_post_with_authorization() throws Exception {
      HttpServerConfigFunction configFunction = client -> {
        String validUserJson = userJson(createUser("cdevoto", "password"));
        client
            .when(request().withPath("/users").withMethod("POST")
                .withHeader("Authorization", "Bearer abcdefg0xy")
                .withBody(validUserJson))
            .respond(response().withStatusCode(200)
                .withBody(userJson(
                    createUser("cdevoto", "xxxxx"),
                    createUser("challendy", "xxxxx"))));
        client
            .when(request().withPath("/users").withMethod("POST")
                .withHeader("Authorization", "Bearer abcdefg0xy"))
            .respond(response().withStatusCode(400));
        client
            .when(request().withPath("/users").withMethod("POST"))
            .respond(response().withStatusCode(401).withBody("You are not authorized!"));
      };

      testWebServer(configFunction, (host, port) -> {

        SimpleHttpRequestFactory requestFactory = createHttpRequestFactory(host, port);

        List<User> users = requestFactory.newRequest()
            .withUrl("/users")
            .addHeader("Authorization", "Bearer abcdefg0xy")
            .post(new User("cdevoto", "password"))
            .execute(new TypeReference<List<User>>() {});

        assertThat(users).isNotNull()
            .containsExactly(
                createUser("cdevoto", "xxxxx"),
                createUser("challendy", "xxxxx"));

      });
    }

    @Test
    public void test_post_with_authorization_incorrect_password() throws Exception {
      HttpServerConfigFunction configFunction = client -> {
        String validUserJson = userJson(createUser("cdevoto", "password"));
        client
            .when(request().withPath("/users").withMethod("POST")
                .withHeader("Authorization", "Bearer abcdefg0xy")
                .withBody(validUserJson))
            .respond(response().withStatusCode(200)
                .withBody(userJson(
                    createUser("cdevoto", "xxxxx"),
                    createUser("challendy", "xxxxx"))));
        client
            .when(request().withPath("/users").withMethod("POST")
                .withHeader("Authorization", "Bearer abcdefg0xy"))
            .respond(response().withStatusCode(400));
        client
            .when(request().withPath("/users").withMethod("POST"))
            .respond(response().withStatusCode(401).withBody("You are not authorized!"));
      };

      testWebServer(configFunction, (host, port) -> {

        SimpleHttpRequestFactory requestFactory = createHttpRequestFactory(host, port);

        // Test invalid password
        ResponseUtils.assertErrorStatus(400, () -> {
          requestFactory.newRequest()
              .withUrl("/users")
              .addHeader("Authorization", "Bearer abcdefg0xy")
              .post(new User("cdevoto", "wrong!!@"))
              .execute();
        }, response -> {
          assertThat(response.body()).isEmpty();
        });

      });
    }

    @Test
    public void test_post_with_authorization_invalid_header() throws Exception {
      HttpServerConfigFunction configFunction = client -> {
        String validUserJson = userJson(createUser("cdevoto", "password"));
        client
            .when(request().withPath("/users").withMethod("POST")
                .withHeader("Authorization", "Bearer abcdefg0xy")
                .withBody(validUserJson))
            .respond(response().withStatusCode(200)
                .withBody(userJson(
                    createUser("cdevoto", "xxxxx"),
                    createUser("challendy", "xxxxx"))));
        client
            .when(request().withPath("/users").withMethod("POST")
                .withHeader("Authorization", "Bearer abcdefg0xy"))
            .respond(response().withStatusCode(400));
        client
            .when(request().withPath("/users").withMethod("POST"))
            .respond(response().withStatusCode(401).withBody("You are not authorized!"));
      };

      testWebServer(configFunction, (host, port) -> {

        SimpleHttpRequestFactory requestFactory = createHttpRequestFactory(host, port);

        // Test invalid auth
        ResponseUtils.assertErrorStatus(401, () -> {
          requestFactory.newRequest()
              .withUrl("/users")
              .addHeader("Authorization", "Bearer abcdefg0xyxxxx")
              .post(new User("cdevoto", "password"))
              .execute();
        }, response -> {
          assertThat(response.body()).isEqualTo("You are not authorized!");
        });
      });
    }
  }

  @Nested
  @DisplayName("httpDelete")
  class HttpDelete {

    @Test
    public void test_delete() throws Exception {
      HttpServerConfigFunction configFunction = client -> {
        client
            .when(request().withPath("/users").withMethod("DELETE"))
            .respond(response().withStatusCode(200));
      };

      testWebServer(configFunction, (host, port) -> {

        SimpleHttpRequestFactory requestFactory = createHttpRequestFactory(host, port);

        assertThatNoException().isThrownBy(() -> {
          requestFactory.newRequest()
              .withUrl("/users")
              .delete()
              .execute();

        });
      });
    }
  }

  @Nested
  @DisplayName("httpPut")
  class HttpPut {

    @Test
    public void test_put() throws Exception {
      HttpServerConfigFunction configFunction = client -> {
        client
            .when(request().withPath("/users").withMethod("PUT")
                .withBody(userJson(createUser("cdevoto", "newPassword"))))
            .respond(response().withStatusCode(200));
      };

      testWebServer(configFunction, (host, port) -> {

        SimpleHttpRequestFactory requestFactory = createHttpRequestFactory(host, port);

        assertThatNoException().isThrownBy(() -> {
          requestFactory.newRequest()
              .withUrl("/users")
              .put(new User("cdevoto", "newPassword"))
              .execute();

        });
      });
    }
  }

  @Nested
  @DisplayName("httpHead")
  class HttpHead {

    @Test
    public void test_put() throws Exception {
      HttpServerConfigFunction configFunction = client -> {
        client
            .when(request().withPath("/users").withMethod("HEAD"))
            .respond(response().withStatusCode(200)
                .withHeader("Content-Type", "text/zinc"));
      };

      testWebServer(configFunction, (host, port) -> {

        SimpleHttpRequestFactory requestFactory = createHttpRequestFactory(host, port);

        String contentType = requestFactory.newRequest()
            .withUrl("/users")
            .head()
            .execute(response -> response.header("Content-Type"));

        assertThat(contentType).isEqualTo("text/zinc");

      });
    }
  }

  // ---------------------
  // Helper Functions
  // ---------------------

  private static void testWebServer(HttpServerConfigFunction serverConfigFunction,
      HttpClientTestFunction testFunction) throws Exception {
    try {
      serverConfigFunction.apply(client);
      testFunction.run(mockServer.getHost(), mockServer.getServerPort());

    } finally {
      client.reset();
    }
  }

  private static SimpleHttpRequestFactory createHttpRequestFactory(String host, int port) {
    SimpleHttpRequestFactory requestFactory =
        SimpleHttpRequestFactory.builder("http://" + host + ":" + port)
            .withConnectTimeout(30, TimeUnit.SECONDS)
            .withReadTimeout(30, TimeUnit.SECONDS)
            .build();
    return requestFactory;
  }

  private SimpleHttpRequestFactory createHttpsRequestFactory(String host, int port)
      throws CertificateValidationException {
    X509Certificate cert =
        X509Certificates.getCertificate("https://" + host + ":" + port);

    X509TrustManager trustManager = TrustManagers.trustCert(cert);
    SSLSocketFactory socketFactory = SslSocketFactories.trustCertFactory(trustManager);

    SimpleHttpRequestFactory httpsRequestFactory =
        SimpleHttpRequestFactory.builder("https://localhost:" + port)
            .withConnectTimeout(30, TimeUnit.SECONDS)
            .withReadTimeout(30, TimeUnit.SECONDS)
            .withSslSocketFactory(socketFactory,
                trustManager)
            .withHostnameVerifier(HostnameVerifiers.trustAllHostnames())
            .build();
    return httpsRequestFactory;
  }

  private InviteStatus createInviteStatus(String userUuid, String status) {
    return new InviteStatus(userUuid, status);
  }

  private String inviteStatusJson(InviteStatus... inviteStatuses) throws IOException {
    checkArgument(inviteStatuses.length > 0, "expected at least one invite status");
    if (inviteStatuses.length == 1) {
      return MAPPER.writeValueAsString(inviteStatuses[0]);
    }
    List<InviteStatus> statusList = Arrays.stream(inviteStatuses).collect(toList());
    return MAPPER.writeValueAsString(statusList);
  }

  private User createUser(String userId, String password) {
    return new User(userId, password);
  }

  private String userJson(User... users) throws IOException {
    checkArgument(users.length > 0, "expected at least one user");
    if (users.length == 1) {
      return MAPPER.writeValueAsString(users[0]);
    }
    List<User> userList = Arrays.stream(users).collect(toList());
    return MAPPER.writeValueAsString(userList);
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

  private static class InviteStatus {
    private String userUuid;
    private String status;

    @SuppressWarnings("unused")
    public InviteStatus() {}

    public InviteStatus(String userUuid, String status) {
      super();
      this.userUuid = userUuid;
      this.status = status;
    }

    public String getUserUuid() {
      return userUuid;
    }

    public String getStatus() {
      return status;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((status == null) ? 0 : status.hashCode());
      result = prime * result + ((userUuid == null) ? 0 : userUuid.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      InviteStatus other = (InviteStatus) obj;
      if (status == null) {
        if (other.status != null)
          return false;
      } else if (!status.equals(other.status))
        return false;
      if (userUuid == null) {
        if (other.userUuid != null)
          return false;
      } else if (!userUuid.equals(other.userUuid))
        return false;
      return true;
    }

  }

  private static class User {
    private String userId;
    private String password;

    @SuppressWarnings("unused")
    public User() {}

    public User(String userId, String password) {
      super();
      this.userId = userId;
      this.password = password;
    }

    public String getUserId() {
      return userId;
    }

    public String getPassword() {
      return password;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((password == null) ? 0 : password.hashCode());
      result = prime * result + ((userId == null) ? 0 : userId.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      User other = (User) obj;
      if (password == null) {
        if (other.password != null)
          return false;
      } else if (!password.equals(other.password))
        return false;
      if (userId == null) {
        if (other.userId != null)
          return false;
      } else if (!userId.equals(other.userId))
        return false;
      return true;
    }


  }

}
