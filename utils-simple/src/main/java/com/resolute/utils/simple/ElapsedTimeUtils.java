package com.resolute.utils.simple;

import static com.google.common.base.Preconditions.checkArgument;
import static com.resolute.utils.simple.StringUtils.padLeftWithZeroes;

public class ElapsedTimeUtils {

  public static String format(long elapsedTime) {
    checkArgument(elapsedTime >= 0);
    long milliseconds = elapsedTime % 1000;
    elapsedTime /= 1000;
    if (elapsedTime == 0) {
      return String.format("%d ms", milliseconds);
    }
    long seconds = elapsedTime % 60;
    elapsedTime /= 60;
    if (elapsedTime == 0) {
      return String.format("%d.%s seconds", seconds, padLeftWithZeroes(milliseconds, 3));
    }
    long minutes = elapsedTime % 60;
    elapsedTime /= 60;
    if (elapsedTime == 0) {
      return String.format("%d:%s.%s minutes", minutes, padLeftWithZeroes(seconds, 2),
          padLeftWithZeroes(milliseconds, 3));
    }
    long hours = elapsedTime % 24;
    elapsedTime /= 24;
    if (elapsedTime == 0) {
      return String.format("%d:%s:%s.%s hours", hours, padLeftWithZeroes(minutes, 2),
          padLeftWithZeroes(seconds, 2), padLeftWithZeroes(milliseconds, 3));
    }

    long days = elapsedTime;
    return String.format("%d:%s:%s:%s.%s days", days, padLeftWithZeroes(hours, 2),
        padLeftWithZeroes(minutes, 2),
        padLeftWithZeroes(seconds, 2), padLeftWithZeroes(milliseconds, 3));
  }

  private ElapsedTimeUtils() {}

}
