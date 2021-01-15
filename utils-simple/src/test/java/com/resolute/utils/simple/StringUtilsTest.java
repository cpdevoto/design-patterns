package com.resolute.utils.simple;

import static com.resolute.utils.simple.StringUtils.hr;
import static com.resolute.utils.simple.StringUtils.padLeft;
import static com.resolute.utils.simple.StringUtils.padLeftWithZeroes;
import static com.resolute.utils.simple.StringUtils.padRight;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

public class StringUtilsTest {

  @Nested
  @DisplayName("padLeft")
  class PadLeft {
    @ParameterizedTest
    @CsvSource({
        "'',    5, '     '",
        "'abc', 5, '  abc'",
        "'abc', 2, 'abc'"})
    public void test_pad_left(String input, int padLength, String expected) {
      String s = padLeft(input, padLength);
      assertThat(s).isEqualTo(expected);
    }

    @Test
    public void test_string_is_null() {
      assertThatThrownBy(() -> {
        padLeft(null, 5);
      }).isInstanceOf(NullPointerException.class);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    public void test_invalid_pad_lengths(int padLength) {
      assertThatThrownBy(() -> {
        padLeft("abc", padLength);
      }).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void test_override_default_pad_char() {
      String s = padLeft("abc", 5, '*');
      assertThat(s).isEqualTo("**abc");
    }


  }

  @Nested
  @DisplayName("padRight")
  class PadRight {

    @ParameterizedTest
    @CsvSource({
        "'',    5, '     '",
        "'abc', 5, 'abc  '",
        "'abc', 2, 'abc'"})
    public void test_pad_right(String input, int padLength, String expected) {
      String s = padRight(input, padLength);
      assertThat(s).isEqualTo(expected);
    }

    @Test
    public void test_override_default_pad_char() {
      String s = padRight("abc", 5, '*');
      assertThat(s).isEqualTo("abc**");
    }

    @Test
    public void test_string_is_null() {
      assertThatThrownBy(() -> {
        padRight(null, 5);
      }).isInstanceOf(NullPointerException.class);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    public void test_invalid_pad_lengths(int padLength) {
      assertThatThrownBy(() -> {
        padRight("abc", padLength);
      }).isInstanceOf(IllegalArgumentException.class);
    }
  }

  @Nested
  @DisplayName("padWithZeroes")
  class PadWithZeroes {

    @ParameterizedTest
    @CsvSource({
        "1,    5, '00001'",
        "41,   5, '00041'",
        "-1,   5, '-0001'",
        "555,  2, '555'",
        "1,    0, '1'",
        "1,   -1, '1'"

    })
    public void test_pad_with_zeroes(int input, int padLength, String expected) {
      String s = padLeftWithZeroes(input, padLength);
      assertThat(s).isEqualTo(expected);
    }

  }


  @Nested
  @DisplayName("hr")
  class HorizontalRule {

    @Test
    public void test_positive_length() {
      String s = hr(5);
      assertThat(s).isEqualTo("=====");
    }

    @Test
    public void test_nonpositive_length() {
      assertThatThrownBy(() -> {
        hr(0);
      }).isInstanceOf(IllegalArgumentException.class);

    }

    @Test
    public void test_override_default_char() {
      String s = hr('*', 5);
      assertThat(s).isEqualTo("*****");
    }

  }
}
