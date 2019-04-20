package com.resolute.utils.simple;

import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

public class TimeUnitUtils {

  public static ChronoUnit convert(TimeUnit tu) {
    if (tu == null) {
      return null;
    }
    switch (tu) {
      case DAYS:
        return ChronoUnit.DAYS;
      case HOURS:
        return ChronoUnit.HOURS;
      case MINUTES:
        return ChronoUnit.MINUTES;
      case SECONDS:
        return ChronoUnit.SECONDS;
      case MICROSECONDS:
        return ChronoUnit.MICROS;
      case MILLISECONDS:
        return ChronoUnit.MILLIS;
      case NANOSECONDS:
        return ChronoUnit.NANOS;
      default:
        assert false : "there are no other TimeUnit ordinal values";
        return null;
    }
  }

  private TimeUnitUtils() {}

}
