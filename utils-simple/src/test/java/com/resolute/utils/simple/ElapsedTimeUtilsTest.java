package com.resolute.utils.simple;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

public class ElapsedTimeUtilsTest {

  @Test
  public void testNegativeElapsedTime() {
    assertThatThrownBy(() -> {
      ElapsedTimeUtils.format(-582);
    }).isInstanceOf(IllegalArgumentException.class);

  }

  @Test
  public void testFormatMilliseconds() {
    String s = ElapsedTimeUtils.format(582);
    assertThat(s).isEqualTo("582 ms");

    s = ElapsedTimeUtils.format(4);
    assertThat(s).isEqualTo("4 ms");

  }

  @Test
  public void testSeconds() {
    String s = ElapsedTimeUtils.format(1000);
    assertThat(s).isEqualTo("1.000 seconds");

    s = ElapsedTimeUtils.format(59999);
    assertThat(s).isEqualTo("59.999 seconds");
  }

  @Test
  public void testMinutes() {
    String s = ElapsedTimeUtils.format(60000);
    assertThat(s).isEqualTo("1:00.000 minutes");

    s = ElapsedTimeUtils.format(3599999);
    assertThat(s).isEqualTo("59:59.999 minutes");
  }

  @Test
  public void testHours() {
    String s = ElapsedTimeUtils.format(3600000);
    assertThat(s).isEqualTo("1:00:00.000 hours");

    s = ElapsedTimeUtils.format(86399999);
    assertThat(s).isEqualTo("23:59:59.999 hours");
  }

  @Test
  public void testDays() {
    String s = ElapsedTimeUtils.format(86400000);
    assertThat(s).isEqualTo("1:00:00:00.000 days");

    s = ElapsedTimeUtils.format(2592000000L);
    assertThat(s).isEqualTo("30:00:00:00.000 days");
  }
}
