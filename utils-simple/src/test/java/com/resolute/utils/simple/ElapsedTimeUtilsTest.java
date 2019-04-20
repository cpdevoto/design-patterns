package com.resolute.utils.simple;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.hamcrest.CoreMatchers;
import org.junit.Test;

import com.resolute.utils.simple.ElapsedTimeUtils;

public class ElapsedTimeUtilsTest {

  /*
   * System.out.println(format(582)); System.out.println(format(25020));
   * System.out.println(format(65324)); System.out.println(format(3600000));
   */
  @Test
  public void testNegativeElapsedTime() {
    try {
      ElapsedTimeUtils.format(-582);
      fail("Expected an IllegalArgumentException");
    } catch (IllegalArgumentException ex) {
    }

  }

  @Test
  public void testFormatMilliseconds() {
    String s = ElapsedTimeUtils.format(582);
    assertThat(s, CoreMatchers.equalTo("582 ms"));

    s = ElapsedTimeUtils.format(4);
    assertThat(s, CoreMatchers.equalTo("4 ms"));

  }

  @Test
  public void testSeconds() {
    String s = ElapsedTimeUtils.format(1000);
    assertThat(s, CoreMatchers.equalTo("1.000 seconds"));

    s = ElapsedTimeUtils.format(59999);
    assertThat(s, CoreMatchers.equalTo("59.999 seconds"));
  }

  @Test
  public void testMinutes() {
    String s = ElapsedTimeUtils.format(60000);
    assertThat(s, CoreMatchers.equalTo("1:00.000 minutes"));

    s = ElapsedTimeUtils.format(3599999);
    assertThat(s, CoreMatchers.equalTo("59:59.999 minutes"));
  }

  @Test
  public void testHours() {
    String s = ElapsedTimeUtils.format(3600000);
    assertThat(s, CoreMatchers.equalTo("1:00:00.000 hours"));

    s = ElapsedTimeUtils.format(86399999);
    assertThat(s, CoreMatchers.equalTo("23:59:59.999 hours"));
  }

  @Test
  public void testDays() {
    String s = ElapsedTimeUtils.format(86400000);
    assertThat(s, CoreMatchers.equalTo("1:00:00:00.000 days"));

    s = ElapsedTimeUtils.format(2592000000L);
    assertThat(s, CoreMatchers.equalTo("30:00:00:00.000 days"));
  }
}
