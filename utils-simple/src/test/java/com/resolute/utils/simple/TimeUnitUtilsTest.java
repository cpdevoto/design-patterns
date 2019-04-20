package com.resolute.utils.simple;

import static com.resolute.utils.simple.TimeUnitUtils.convert;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

public class TimeUnitUtilsTest {

  @Test
  public void test_convert() {

    assertThat(convert(TimeUnit.DAYS), equalTo(ChronoUnit.DAYS));
    assertThat(convert(TimeUnit.HOURS), equalTo(ChronoUnit.HOURS));
    assertThat(convert(TimeUnit.MINUTES), equalTo(ChronoUnit.MINUTES));
    assertThat(convert(TimeUnit.SECONDS), equalTo(ChronoUnit.SECONDS));
    assertThat(convert(TimeUnit.MICROSECONDS), equalTo(ChronoUnit.MICROS));
    assertThat(convert(TimeUnit.MILLISECONDS), equalTo(ChronoUnit.MILLIS));
    assertThat(convert(TimeUnit.NANOSECONDS), equalTo(ChronoUnit.NANOS));
  }

}
