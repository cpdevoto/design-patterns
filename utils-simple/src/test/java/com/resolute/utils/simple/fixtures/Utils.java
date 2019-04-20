package com.resolute.utils.simple.fixtures;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Random;

public class Utils {

  @SuppressWarnings("unused")
  private static final Random RANDOM = new Random(0);
  private static final DecimalFormat formatter =
      new DecimalFormat("#.##", new DecimalFormatSymbols(Locale.US));

  public static void fixedDelay() {
    int delay = 1000;
    try {
      Thread.sleep(delay);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  public static void fixedDelay(int millis) {
    int delay = millis;
    try {
      Thread.sleep(delay);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  public static void randomDelay() {
    int delay = 500 + RANDOM.nextInt(2000);
    try {
      Thread.sleep(delay);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  public static double format(double number) {
    synchronized (formatter) {
      return new Double(formatter.format(number));
    }
  }


  private Utils() {}
}
