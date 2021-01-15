package com.resolute.utils.simple;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class ExceptionUtilsTest {

  @SuppressWarnings("null")
  @Test
  public void extractReason() {

    // STEP 1: ARRANGE
    String reason = "";
    String expectedReason =
        "null  STACKTRACE:  com.resolute.utils.simple.ExceptionUtilsTest.extractReason(ExceptionUtilsTest.java:23)";


    // STEP 2: ACT
    try {
      String s = null;
      s = s.replaceAll("X", "Y");
    } catch (Throwable t) {
      reason = ExceptionUtils.extractReason(t);
    }


    // STEP 3: ASSERT
    assertThat(reason).isEqualTo(expectedReason);
  }

  @SuppressWarnings("null")
  @Test
  public void extractReason_withErrorMessagePrefix() {

    // STEP 1: ARRANGE
    String errorMessagePrefix = "Error Message Prefix. ";
    String reason = "";
    String expectedReason = errorMessagePrefix
        + " null  STACKTRACE:  com.resolute.utils.simple.ExceptionUtilsTest.extractReason_withErrorMessagePrefix(ExceptionUtilsTest.java:47)";


    // STEP 2: ACT
    try {
      String s = null;
      s = s.replaceAll("X", "Y");
    } catch (Throwable t) {
      reason = ExceptionUtils.extractReason(errorMessagePrefix, t);
    }


    // STEP 3: ASSERT
    assertThat(reason).isEqualTo(expectedReason);
  }

  @Test
  public void extractReason_nullArgument() {
    Assertions.assertThatThrownBy(() -> {
      ExceptionUtils.extractReason(null);
    }).isInstanceOf(IllegalArgumentException.class);
  }
}
