package com.resolute.utils.simple;

import static com.resolute.utils.simple.StringUtils.hr;
import static com.resolute.utils.simple.StringUtils.padLeft;
import static com.resolute.utils.simple.StringUtils.padLeftWithZeroes;
import static com.resolute.utils.simple.StringUtils.padRight;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.junit.Test;

public class StringUtilsTest {

  @Test
  public void testPadLeft() {
    String s = padLeft("", 5);
    assertThat(s, equalTo("     "));

    s = padLeft("abc", 5);
    assertThat(s, equalTo("  abc"));

    s = padLeft("abc", 5, '*');
    assertThat(s, equalTo("**abc"));

    s = padLeft("abc", 2);
    assertThat(s, equalTo("abc"));

    try {
      padLeft(null, 5);
      fail("Expected a NullPointerException");
    } catch (NullPointerException ex) {
    }

    try {
      padLeft("abc", 0);
      fail("Expected an IllegalArgumentException");
    } catch (IllegalArgumentException ex) {
    }

    try {
      padLeft("abc", -1);
      fail("Expected an IllegalArgumentException");
    } catch (IllegalArgumentException ex) {
    }
  }

  @Test
  public void testPadRight() {
    String s = padRight("", 5);
    assertThat(s, equalTo("     "));

    s = padRight("abc", 5);
    assertThat(s, equalTo("abc  "));

    s = padRight("abc", 5, '*');
    assertThat(s, equalTo("abc**"));

    s = padRight("abc", 2);
    assertThat(s, equalTo("abc"));

    try {
      padRight(null, 5);
      fail("Expected a NullPointerException");
    } catch (NullPointerException ex) {
    }

    try {
      padRight("abc", 0);
      fail("Expected an IllegalArgumentException");
    } catch (IllegalArgumentException ex) {
    }

    try {
      padRight("abc", -1);
      fail("Expected an IllegalArgumentException");
    } catch (IllegalArgumentException ex) {
    }
  }

  @Test
  public void testPadWithZeroes() {
    String s = padLeftWithZeroes(1, 5);
    assertThat(s, equalTo("00001"));

    s = padLeftWithZeroes(41, 5);
    assertThat(s, equalTo("00041"));

    s = padLeftWithZeroes(-1, 5);
    assertThat(s, equalTo("-0001"));

    s = padLeftWithZeroes(555, 2);
    assertThat(s, equalTo("555"));

    s = padLeftWithZeroes(1, 0);
    assertThat(s, equalTo("1"));

    s = padLeftWithZeroes(1, -1);
    assertThat(s, equalTo("1"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void test_hr_with_nonpositive_length() {
    hr(0);
  }

  @Test
  public void test_hr() {
    assertThat(hr(5), equalTo("====="));
    assertThat(hr('*', 5), equalTo("*****"));
  }
}
