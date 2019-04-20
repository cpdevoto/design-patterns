package com.resolute.utils.simple;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtils {

  public static long iso8601ToEpochMillis(String iso8601) {
    requireNonNull(iso8601, "iso8601 cannot be null");
    ZonedDateTime zoned = ZonedDateTime.parse(iso8601, DateTimeFormatter.ISO_DATE_TIME);
    return zoned.toEpochSecond() * 1000;
  }

  public static String epochMillisToIso8601(long epochMillis) {
    return epochMillisToIso8601(epochMillis, ZoneId.of("UTC"));
  }

  public static String epochMillisToIso8601(long epochMillis, ZoneId zoneId) {
    checkArgument(epochMillis >= 0, "epochMillis cannot be negative");
    requireNonNull(zoneId, "zoneId cannot be null");
    Instant instant = Instant.ofEpochSecond(epochMillis > 0 ? epochMillis / 1000 : epochMillis);
    ZonedDateTime zoned = ZonedDateTime.ofInstant(instant, zoneId);
    String iso8601 = DateTimeFormatter.ISO_DATE_TIME.format(zoned);
    int idx = iso8601.indexOf('[');
    if (idx != -1) {
      iso8601 = iso8601.substring(0, idx);
    }
    return iso8601;
  }

  private DateTimeUtils() {}

}
