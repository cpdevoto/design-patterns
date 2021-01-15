package com.resolute.utils.simple;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtils {

  public static final String DATE_TIME_FORMATTER_PATTERN = "M/d/yyyy, h:mm:ss a";
  
  private static final ThreadLocal<DateTimeFormatter> DATE_TIME_FORMATTER =
      new ThreadLocal<DateTimeFormatter>() {
        @Override
        protected DateTimeFormatter initialValue() {
          return DateTimeFormatter.ofPattern(DATE_TIME_FORMATTER_PATTERN);
        }
      };
      
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
  
  /**
   * 
   * @param epochMillis Number of milliseconds since the UNIX epoch (Thu Jan 01 1970 00:00:00)
   * @param timezone A Ruby timezone (which is what is stored in the DB), e.g. "Eastern Time (US & Canada)"
   * @return Returns time formatted as: "M/d/yyyy, h:mm:ss a"
   */
  public static String toFormattedZonedTime(long epochMillis, String timezone) {
    
    return DATE_TIME_FORMATTER
        .get()
        .format(Instant
            .ofEpochMilli(epochMillis)
            .atZone(ZoneId
                .of(TimezoneUtils
                    .getTimezone(timezone)
                    .getID())));
  }

  private DateTimeUtils() {}

}
