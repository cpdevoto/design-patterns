package com.resolute.utils.simple;

import static com.resolute.utils.simple.CalendarAligners.ceiling;
import static com.resolute.utils.simple.CalendarAligners.floor;
import static com.resolute.utils.simple.CalendarAligners.nearest;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;
import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.UnsupportedTemporalTypeException;

import org.junit.jupiter.api.Test;


public class CalendarAlignerTest {

  private static LocalDateTime getLocalDateTime(int year, int month, int day, int hour, int minute,
      int second, int nanos) {
    LocalDate date = LocalDate.of(year, month, day);
    LocalTime time = LocalTime.of(hour, minute, second, nanos);
    return date.atTime(time);

  }

  @Test
  public void test_new_invalid_interval() {
    assertThatThrownBy(() -> {
      CalendarAligners.floor(1_000_000_000, ChronoField.NANO_OF_SECOND);
    })
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void test_new_invalid_interval_2() {
    assertThatThrownBy(() -> {
      CalendarAligners.floor(5, ChronoField.MONTH_OF_YEAR);
    })
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void test_new_invalid_field() {
    assertThatThrownBy(() -> {
      CalendarAligners.floor(2, ChronoField.DAY_OF_WEEK);
    })
        .isInstanceOf(IllegalArgumentException.class);
  }


  @Test
  public void test_local_date() {
    assertThatThrownBy(() -> {
      LocalDate.of(2019, 4, 3)
          .with(floor(2, ChronoField.MONTH_OF_YEAR));
    })
        .isInstanceOf(UnsupportedTemporalTypeException.class);
  }

  @Test
  public void test() {
    LocalDateTime adjusted = getLocalDateTime(2019, 3, 3, 19, 57, 43, 657_423_821)
        .with(floor(15, ChronoField.MINUTE_OF_HOUR));
    assertThat(adjusted.format(ISO_LOCAL_DATE_TIME)).isEqualTo("2019-03-03T19:45:00");

    adjusted = getLocalDateTime(2019, 3, 3, 19, 47, 43, 657_423_821)
        .with(ceiling(15, ChronoField.MINUTE_OF_HOUR));
    assertThat(adjusted.format(ISO_LOCAL_DATE_TIME)).isEqualTo("2019-03-03T20:00:00");

    adjusted = getLocalDateTime(2019, 3, 3, 19, 52, 30, 0)
        .with(nearest(15, ChronoField.MINUTE_OF_HOUR));
    assertThat(adjusted.format(ISO_LOCAL_DATE_TIME)).isEqualTo("2019-03-03T20:00:00");

    adjusted = getLocalDateTime(2019, 3, 3, 19, 52, 29, 999_999_999)
        .with(nearest(15, ChronoField.MINUTE_OF_HOUR));
    assertThat(adjusted.format(ISO_LOCAL_DATE_TIME)).isEqualTo("2019-03-03T19:45:00");

    adjusted = getLocalDateTime(2019, 2, 3, 19, 52, 29, 999_999_999)
        .with(floor(2, ChronoField.MONTH_OF_YEAR));
    assertThat(adjusted.format(ISO_LOCAL_DATE_TIME)).isEqualTo("2019-01-01T00:00:00");

    adjusted = getLocalDateTime(2019, 2, 3, 19, 52, 29, 999_999_999)
        .with(floor(2, ChronoField.YEAR));
    assertThat(adjusted.format(ISO_LOCAL_DATE_TIME)).isEqualTo("2018-01-01T00:00:00");

    ZonedDateTime adjustedZDT =
        getLocalDateTime(2019, 2, 3, 19, 52, 29, 999_999_999).atZone(ZoneId.of("America/New_York"))
            .with(floor(2, ChronoField.YEAR));
    assertThat(adjustedZDT.format(ISO_OFFSET_DATE_TIME)).isEqualTo("2018-01-01T00:00:00-05:00");

    adjustedZDT =
        getLocalDateTime(2019, 2, 3, 19, 52, 29, 999_999_999).atZone(ZoneId.of("America/New_York"))
            .with(floor(1, ChronoField.DAY_OF_MONTH));
    assertThat(adjustedZDT.format(ISO_OFFSET_DATE_TIME)).isEqualTo("2019-02-03T00:00:00-05:00");

    adjustedZDT =
        getLocalDateTime(2019, 2, 3, 19, 52, 29, 999_999_999).atZone(ZoneId.of("America/New_York"))
            .with(ceiling(1, ChronoField.DAY_OF_MONTH));
    assertThat(adjustedZDT.format(ISO_OFFSET_DATE_TIME)).isEqualTo("2019-02-04T00:00:00-05:00");

  }

  @Test
  public void test_aligners_at_boundary() {
    ZoneId zoneId = ZoneId.of("America/New_York");
    TemporalAdjuster adjuster = CalendarAligners.floor(15, ChronoField.MINUTE_OF_HOUR);
    long timestamp = 1572581700000L; // Timestamp falls exactly ath the 15 minute boundary
                                     // (11/1/2019 00:15:00)!
    long floor = Instant.ofEpochMilli(timestamp).atZone(zoneId)
        .with(adjuster).toInstant().toEpochMilli();
    assertThat(floor).isEqualTo(timestamp); // Expecting 11/1/2019 00:15:00
    long timestamp2 = timestamp - 1;
    floor = Instant.ofEpochMilli(timestamp2).atZone(zoneId)
        .with(adjuster).toInstant().toEpochMilli();
    assertThat(floor).isEqualTo(1572580800000L); // Expecting 11/1/2019 00:00:00
    timestamp2 = timestamp + 1;
    floor = Instant.ofEpochMilli(timestamp2).atZone(zoneId)
        .with(adjuster).toInstant().toEpochMilli();
    assertThat(floor).isEqualTo(timestamp); // Expecting 11/1/2019 00:15:00

    adjuster = CalendarAligners.ceiling(15, ChronoField.MINUTE_OF_HOUR);
    long ceiling = Instant.ofEpochMilli(timestamp).atZone(zoneId)
        .with(adjuster).toInstant().toEpochMilli();
    assertThat(ceiling).isEqualTo(timestamp); // Expecting 11/1/2019 00:15:00
    timestamp2 = timestamp - 1;
    ceiling = Instant.ofEpochMilli(timestamp2).atZone(zoneId)
        .with(adjuster).toInstant().toEpochMilli();
    assertThat(ceiling).isEqualTo(timestamp); // Expecting 11/1/2019 00:15:00
    timestamp2 = timestamp + 1;
    ceiling = Instant.ofEpochMilli(timestamp2).atZone(zoneId)
        .with(adjuster).toInstant().toEpochMilli();
    assertThat(ceiling).isEqualTo(1572582600000L); // Expecting 11/1/2019 00:30:00

    adjuster = CalendarAligners.nearest(15, ChronoField.MINUTE_OF_HOUR);
    long nearest = Instant.ofEpochMilli(timestamp).atZone(zoneId)
        .with(adjuster).toInstant().toEpochMilli();
    assertThat(nearest).isEqualTo(timestamp); // Expecting 11/1/2019 00:15:00
    timestamp2 = timestamp - 1;
    nearest = Instant.ofEpochMilli(timestamp2).atZone(zoneId)
        .with(adjuster).toInstant().toEpochMilli();
    assertThat(nearest).isEqualTo(timestamp); // Expecting 11/1/2019 00:15:00
    timestamp2 = timestamp + 1;
    nearest = Instant.ofEpochMilli(timestamp2).atZone(zoneId)
        .with(adjuster).toInstant().toEpochMilli();
    assertThat(nearest).isEqualTo(timestamp); // Expecting 11/1/2019 00:15:00
    // 7 minutes, 30 seconds
    Duration midwayDuration = Duration.ofMillis(Duration.ofMinutes(15).toMillis() / 2);

    timestamp2 = Instant.ofEpochMilli(timestamp)
        .minus(midwayDuration).toEpochMilli();
    nearest = Instant.ofEpochMilli(timestamp2).atZone(zoneId)
        .with(adjuster).toInstant().toEpochMilli();
    assertThat(nearest).isEqualTo(timestamp); // Expecting 11/1/2019 00:15:00
    timestamp2 = timestamp2 - 1;
    nearest = Instant.ofEpochMilli(timestamp2).atZone(zoneId)
        .with(adjuster).toInstant().toEpochMilli();
    assertThat(nearest).isEqualTo(1572580800000L); // Expecting 11/1/2019 00:00:00

    timestamp2 = Instant.ofEpochMilli(timestamp)
        .plus(midwayDuration).toEpochMilli();
    nearest = Instant.ofEpochMilli(timestamp2).atZone(zoneId)
        .with(adjuster).toInstant().toEpochMilli();
    assertThat(nearest).isEqualTo(1572582600000L); // Expecting 11/1/2019 00:30:00
    timestamp2 = timestamp2 - 1;
    nearest = Instant.ofEpochMilli(timestamp2).atZone(zoneId)
        .with(adjuster).toInstant().toEpochMilli();
    assertThat(nearest).isEqualTo(timestamp); // Expecting 11/1/2019 00:15:00
  }

}
