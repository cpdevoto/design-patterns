package com.resolute.utils.simple;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.time.ZoneId;

import org.junit.Test;

public class DateTimeUtilsTest {

  @Test
  public void test() {
    long epochMillis = DateTimeUtils.iso8601ToEpochMillis("2017-06-20T16:55:00-07:00");
    assertThat(epochMillis, equalTo(1498002900000L));
    String iso8601 =
        DateTimeUtils.epochMillisToIso8601(epochMillis, ZoneId.of("America/Los_Angeles"));
    assertThat(iso8601, equalTo("2017-06-20T16:55:00-07:00"));
    iso8601 =
        DateTimeUtils.epochMillisToIso8601(epochMillis);
    assertThat(iso8601, equalTo("2017-06-20T23:55:00Z"));
  }
}
