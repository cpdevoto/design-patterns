package com.resolute.utils.simple;

import static com.resolute.utils.simple.CalendarAligners.ceiling;
import static com.resolute.utils.simple.CalendarAligners.floor;
import static com.resolute.utils.simple.CalendarAligners.nearest;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;
import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.UnsupportedTemporalTypeException;

import org.junit.Test;

public class CalendarAlignerTest {
  private static TemporalAdjuster floor =
      floor(15, ChronoField.MINUTE_OF_HOUR);
  private static TemporalAdjuster ceiling =
      ceiling(15, ChronoField.MINUTE_OF_HOUR);
  private static TemporalAdjuster nearest =
      nearest(15, ChronoField.MINUTE_OF_HOUR);

  private static LocalDateTime getLocalDateTime(int year, int month, int day, int hour, int minute,
      int second, int nanos) {
    LocalDate date = LocalDate.of(year, month, day);
    LocalTime time = LocalTime.of(hour, minute, second, nanos);
    return date.atTime(time);

  }

  @Test(expected = IllegalArgumentException.class)
  public void test_new_invalid_interval() {
    CalendarAligners.floor(1_000_000_000, ChronoField.NANO_OF_SECOND);
  }

  @Test(expected = IllegalArgumentException.class)
  public void test_new_invalid_interval_2() {
    CalendarAligners.floor(5, ChronoField.MONTH_OF_YEAR);
  }

  @Test(expected = IllegalArgumentException.class)
  public void test_new_invalid_field() {
    CalendarAligners.floor(2, ChronoField.DAY_OF_WEEK);
  }


  @Test(expected = UnsupportedTemporalTypeException.class)
  public void test_local_date() {
    LocalDate.of(2019, 4, 3)
        .with(floor(2, ChronoField.MONTH_OF_YEAR));
  }

  @Test
  public void test() {
    LocalDateTime adjusted = getLocalDateTime(2019, 3, 3, 19, 57, 43, 657_423_821)
        .with(floor(15, ChronoField.MINUTE_OF_HOUR));
    assertThat(adjusted.format(ISO_LOCAL_DATE_TIME), equalTo("2019-03-03T19:45:00"));

    adjusted = getLocalDateTime(2019, 3, 3, 19, 47, 43, 657_423_821)
        .with(ceiling(15, ChronoField.MINUTE_OF_HOUR));
    assertThat(adjusted.format(ISO_LOCAL_DATE_TIME), equalTo("2019-03-03T20:00:00"));

    adjusted = getLocalDateTime(2019, 3, 3, 19, 52, 30, 0)
        .with(nearest(15, ChronoField.MINUTE_OF_HOUR));
    assertThat(adjusted.format(ISO_LOCAL_DATE_TIME), equalTo("2019-03-03T20:00:00"));

    adjusted = getLocalDateTime(2019, 3, 3, 19, 52, 29, 999_999_999)
        .with(nearest(15, ChronoField.MINUTE_OF_HOUR));
    assertThat(adjusted.format(ISO_LOCAL_DATE_TIME), equalTo("2019-03-03T19:45:00"));

    adjusted = getLocalDateTime(2019, 2, 3, 19, 52, 29, 999_999_999)
        .with(floor(2, ChronoField.MONTH_OF_YEAR));
    assertThat(adjusted.format(ISO_LOCAL_DATE_TIME), equalTo("2019-01-01T00:00:00"));

    adjusted = getLocalDateTime(2019, 2, 3, 19, 52, 29, 999_999_999)
        .with(floor(2, ChronoField.YEAR));
    assertThat(adjusted.format(ISO_LOCAL_DATE_TIME), equalTo("2018-01-01T00:00:00"));

    ZonedDateTime adjustedZDT =
        getLocalDateTime(2019, 2, 3, 19, 52, 29, 999_999_999).atZone(ZoneId.of("America/New_York"))
            .with(floor(2, ChronoField.YEAR));
    assertThat(adjustedZDT.format(ISO_OFFSET_DATE_TIME), equalTo("2018-01-01T00:00:00-05:00"));

    adjustedZDT =
        getLocalDateTime(2019, 2, 3, 19, 52, 29, 999_999_999).atZone(ZoneId.of("America/New_York"))
            .with(floor(1, ChronoField.DAY_OF_MONTH));
    assertThat(adjustedZDT.format(ISO_OFFSET_DATE_TIME), equalTo("2019-02-03T00:00:00-05:00"));

    adjustedZDT =
        getLocalDateTime(2019, 2, 3, 19, 52, 29, 999_999_999).atZone(ZoneId.of("America/New_York"))
            .with(ceiling(1, ChronoField.DAY_OF_MONTH));
    assertThat(adjustedZDT.format(ISO_OFFSET_DATE_TIME), equalTo("2019-02-04T00:00:00-05:00"));

  }


}
