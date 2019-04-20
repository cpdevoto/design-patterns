package com.resolute.coord.utils;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ConcurrencyUtils {

  public static void await(CountDownLatch latch) {
    try {
      latch.await();
    } catch (InterruptedException e) {
      // If the thread is interrupted, just stop blocking!
    }
  }

  public static boolean await(CountDownLatch latch, long timeout, TimeUnit unit) {
    try {
      return latch.await(timeout, unit);
    } catch (InterruptedException e) {
      // If the thread is interrupted, just stop blocking!
      return false;
    }
  }

  private ConcurrencyUtils() {}

}
