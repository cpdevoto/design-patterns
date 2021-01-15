package com.resolute.utils.simple;

import static com.resolute.utils.simple.TimeUnitUtils.convert;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

public class TimeUnitUtilsTest {

  @Test
  public void test_convert() {

    assertThat(convert(TimeUnit.DAYS)).isEqualTo(ChronoUnit.DAYS);
    assertThat(convert(TimeUnit.HOURS)).isEqualTo(ChronoUnit.HOURS);
    assertThat(convert(TimeUnit.MINUTES)).isEqualTo(ChronoUnit.MINUTES);
    assertThat(convert(TimeUnit.SECONDS)).isEqualTo(ChronoUnit.SECONDS);
    assertThat(convert(TimeUnit.MICROSECONDS)).isEqualTo(ChronoUnit.MICROS);
    assertThat(convert(TimeUnit.MILLISECONDS)).isEqualTo(ChronoUnit.MILLIS);
    assertThat(convert(TimeUnit.NANOSECONDS)).isEqualTo(ChronoUnit.NANOS);
  }

}
