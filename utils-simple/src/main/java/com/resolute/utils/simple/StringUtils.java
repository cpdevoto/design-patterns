package com.resolute.utils.simple;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

public class StringUtils {

  public static String padRight(String s, int len) {
    return padRight(s, len, ' ');
  }

  public static String padRight(String s, int len, char pad) {
    requireNonNull(s);
    checkArgument(len > 0);
    StringBuilder buf = new StringBuilder(s);
    for (int i = s.length(); i < len; i++) {
      buf.append(pad);
    }
    return buf.toString();
  }

  public static String padLeft(String s, int len) {
    return padLeft(s, len, ' ');
  }

  public static String padLeft(String s, int len, char pad) {
    requireNonNull(s);
    checkArgument(len > 0);
    StringBuilder buf = new StringBuilder();
    for (int i = s.length(); i < len; i++) {
      buf.append(pad);
    }
    buf.append(s);
    return buf.toString();
  }

  public static String padLeftWithZeroes(long n, int len) {
    requireNonNull(len > 0);
    StringBuilder buf = new StringBuilder();
    if (n < 0) {
      buf.append("-");
      len = Math.max(0, len - 1);
      n = Math.abs(n);
    }
    String s = String.valueOf(n);
    for (int i = s.length(); i < len; i++) {
      buf.append("0");
    }
    buf.append(s);
    return buf.toString();
  }

  public static String hr(int len) {
    return hr('=', len);
  }

  public static String hr(char c, int len) {
    checkArgument(len > 0);
    StringBuilder buf = new StringBuilder();
    for (int i = 0; i < len; i++) {
      buf.append(c);
    }
    return buf.toString();
  }

  public static String padWithZeroes(int size, long num) {
    long abs = Math.abs(num);
    String s = String.valueOf(abs);
    StringBuilder buf = new StringBuilder();
    for (int i = s.length(); i < size; i++) {
      buf.append(0);
    }
    buf.append(abs);
    return buf.toString();
  }

  private StringUtils() {}

}
