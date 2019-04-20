package com.resolute.okhttp3.simple;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.resolute.okhttp3.simple.utils.ResponseUtils;

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

public class SimpletHttpRequestTest {

  private static final ObjectMapper mapper = HttpUtilsHelper.MAPPER;

  private static MockWebServer server;
  private static SimpleHttpRequestFactory requestFactory;


  @BeforeClass
  public static void setup() {
    server = new MockWebServer();

    final Dispatcher dispatcher = new Dispatcher() {

      @Override
      public MockResponse dispatch(RecordedRequest request) throws InterruptedException {

        try {
          if (request.getPath().equals("/user/invite") && request.getMethod().equals("GET")) {
            String responseBody = mapper.writeValueAsString(
                new InviteStatus("b558664d-fcf7-48e8-9807-e7a7614f22bc", "new"));
            return new MockResponse().setResponseCode(200).setBody(responseBody);

          } else if (request.getPath().equals("/users") && request.getMethod().equals("POST")) {
            String auth = request.getHeader("Authorization");
            if (auth != null && "Bearer abcdefg0xy".equals(auth)) {
              String requestBody = request.getBody().readUtf8();
              User user = mapper.readValue(requestBody, User.class);
              if (!"cdevoto".equals(user.getUserId()) || !"password".equals(user.getPassword())) {
                return new MockResponse().setResponseCode(400);
              }
              return new MockResponse().setResponseCode(200);
            } else {
              String responseBody = "You are not authorized!";
              return new MockResponse().setResponseCode(401).setBody(responseBody);
            }

          } else {
            return new MockResponse().setResponseCode(404);
          }
        } catch (Exception e) {
          return new MockResponse().setResponseCode(500);
        }
      }
    };

    server.setDispatcher(dispatcher);

    requestFactory =
        SimpleHttpRequestFactory.builder("http://localhost:" + server.getPort())
            .withConnectTimeout(30, TimeUnit.SECONDS)
            .withReadTimeout(30, TimeUnit.SECONDS)
            .build();
  }

  @AfterClass
  public static void teardown() throws IOException {
    server.shutdown();
  }

  @Test
  public void test_http_get() throws IOException {

    // Test happy path
    InviteStatus status = requestFactory.newRequest()
        .withUrl("/user/invite")
        .execute(InviteStatus.class);

    assertThat(status, notNullValue());
    assertThat(status.getUserUuid(), equalTo("b558664d-fcf7-48e8-9807-e7a7614f22bc"));
    assertThat(status.getStatus(), equalTo("new"));

    // Test incorrect Url
    ResponseUtils.assertErrorStatus(404, () -> {
      requestFactory.newRequest()
          .withUrl("/user/invitexx")
          .execute(InviteStatus.class);
    });

  }

  @Test
  public void test_http_post() throws IOException {

    // Test happy path
    requestFactory.newRequest()
        .withUrl("/users")
        .addHeader("Authorization", "Bearer abcdefg0xy")
        .post(new User("cdevoto", "password"))
        .execute();


    // Test invalid auth
    ResponseUtils.assertErrorStatus(401, () -> {
      requestFactory.newRequest()
          .withUrl("/users")
          .addHeader("Authorization", "Bearer abcdefg0xyxxxx")
          .post(new User("cdevoto", "password"))
          .execute();
    }, response -> {
      assertThat(response.body(), equalTo("You are not authorized!"));
    });

    // Same test but without ResponseUtils (included for educational purposes)
    try {
      requestFactory.newRequest()
          .withUrl("/users")
          .addHeader("Authorization", "Bearer abcdefg0xyxxxx")
          .post(new User("cdevoto", "password"))
          .execute();
      fail("Expected a BadResponseException");
    } catch (BadResponseException e) {
      assertThat(e.getResponse().code(), equalTo(401));
      assertThat(e.getResponse().body(), equalTo("You are not authorized!"));
    }


    // Test bad request
    ResponseUtils.assertErrorStatus(400, () -> {
      requestFactory.newRequest()
          .withUrl("/users")
          .addHeader("Authorization", "Bearer abcdefg0xy")
          .post(new User("cdevoto", "passwordxxx"))
          .execute();
    });


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
  }

}
