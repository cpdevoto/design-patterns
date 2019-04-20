package com.resolute.utils.simple;

import java.time.temporal.TemporalField;

public class CalendarAligners {

  public static CalendarAligner floor(long interval, TemporalField field) {
    return new CalendarAligner(interval, field, CalendarAligner::computeFloor);
  }

  public static CalendarAligner ceiling(long interval, TemporalField field) {
    return new CalendarAligner(interval, field,
        CalendarAligner::computeCeiling);
  }

  public static CalendarAligner nearest(long interval, TemporalField field) {
    return new CalendarAligner(interval, field,
        CalendarAligner::computeNearest);
  }
}
