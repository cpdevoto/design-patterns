package com.resolute.okhttp3.simple.utils;

import static java.util.Objects.requireNonNull;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Optional;

import com.resolute.okhttp3.simple.BadResponseException;
import com.resolute.okhttp3.simple.HttpCall;

public class ResponseUtils {

  public static void assertErrorStatus(int expectedStatus, HttpCall call)
      throws IOException {
    assertErrorStatus(expectedStatus, call, Optional.empty());
  }

  public static void assertErrorStatus(int expectedStatus, HttpCall call,
      ErrorResponseHandler errorResponseHandler)
      throws IOException {
    requireNonNull(errorResponseHandler, "errorResponseHandler cannot be null");
    assertErrorStatus(expectedStatus, call, Optional.of(errorResponseHandler));
  }

  private static void assertErrorStatus(int expectedStatus, HttpCall call,
      Optional<ErrorResponseHandler> errorResponseHandler)
      throws IOException {
    requireNonNull(call, "call cannot be null");
    try {
      call.execute();
      fail("Expected an error status of " + expectedStatus + " but got a successful response");
    } catch (BadResponseException ex) {
      assertThat(ex.getResponse().code(), equalTo(expectedStatus));
      if (errorResponseHandler.isPresent()) {
        errorResponseHandler.get().handle(ex.getResponse());
      }
    }
  }


}
