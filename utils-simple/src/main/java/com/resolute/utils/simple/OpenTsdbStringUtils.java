package com.resolute.utils.simple;

import static java.util.Objects.requireNonNull;

public class OpenTsdbStringUtils {

  public static String toValidMetricId(String s) {
    requireNonNull(s, "s cannot be null");
    return s.replaceAll("[^a-zA-Z0-9-_\\./]", "_");
  }


  private OpenTsdbStringUtils() {}

}
