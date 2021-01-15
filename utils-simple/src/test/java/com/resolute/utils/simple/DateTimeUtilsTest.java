package com.resolute.utils.simple;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.ZoneId;

import org.junit.jupiter.api.Test;

public class DateTimeUtilsTest {

  @Test
  public void test() {

    long epochMillis = DateTimeUtils.iso8601ToEpochMillis("2017-06-20T16:55:00-07:00");
    assertThat(epochMillis).isEqualTo(1498002900000L);

    String iso8601 =
        DateTimeUtils.epochMillisToIso8601(epochMillis, ZoneId.of("America/Los_Angeles"));
    assertThat(iso8601).isEqualTo("2017-06-20T16:55:00-07:00");

    iso8601 = DateTimeUtils.epochMillisToIso8601(epochMillis);
    assertThat(iso8601).isEqualTo("2017-06-20T23:55:00Z");

    String zonedTime =
        DateTimeUtils.toFormattedZonedTime(1558530708330L, "Eastern Time (US & Canada)");
    assertThat(zonedTime).isEqualTo("5/22/2019, 9:11:48 AM");
  }
}
