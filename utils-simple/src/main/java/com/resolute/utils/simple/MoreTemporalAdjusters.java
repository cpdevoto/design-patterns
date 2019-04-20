package com.resolute.utils.simple;

import java.time.DayOfWeek;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;

public class MoreTemporalAdjusters {

  // Assumes that week starts on Sunday; like Outlook
  public static TemporalAdjuster firstDayOfWeek() {
    return MoreTemporalAdjusters::getFirstDayOfWeek;
  }

  // Assumes that week starts on Sunday; like Outlook
  public static TemporalAdjuster lastDayOfWeek() {
    return MoreTemporalAdjusters::getLastDayOfWeek;
  }

  public static TemporalAdjuster firstWeekdayOfMonth() {
    return MoreTemporalAdjusters::getFirstWeekdayOfMonth;
  }

  public static TemporalAdjuster nextWeekday() {
    return MoreTemporalAdjusters::getNextWeekday;
  }

  public static TemporalAdjuster lastWeekdayOfMonth() {
    return MoreTemporalAdjusters::getLastWeekdayOfMonth;
  }

  public static TemporalAdjuster previousWeekday() {
    return MoreTemporalAdjusters::getPreviousWeekday;
  }

  public static TemporalAdjuster firstWeekendDayOfMonth() {
    return MoreTemporalAdjusters::getFirstWeekendDayOfMonth;
  }

  public static TemporalAdjuster nextWeekendDay() {
    return MoreTemporalAdjusters::getNextWeekendDay;
  }

  public static TemporalAdjuster lastWeekendDayOfMonth() {
    return MoreTemporalAdjusters::getLastWeekendDayOfMonth;
  }

  public static TemporalAdjuster previousWeekendDay() {
    return MoreTemporalAdjusters::getPreviousWeekendDay;
  }

  private static Temporal getFirstDayOfWeek(Temporal temporal) {
    int dayOfWeek = (temporal.get(ChronoField.DAY_OF_WEEK) + 1) % 7; // 1 = Sunday; 7 = Saturday
    dayOfWeek = dayOfWeek == 0 ? 7 : dayOfWeek;
    return temporal.minus(dayOfWeek - 1,
        ChronoUnit.DAYS);
  }

  private static Temporal getLastDayOfWeek(Temporal temporal) {
    return getFirstDayOfWeek(temporal).plus(6, ChronoUnit.DAYS);
  }

  private static Temporal getFirstWeekdayOfMonth(Temporal temporal) {
    Temporal firstDayOfMonth = temporal.with(TemporalAdjusters.firstDayOfMonth());
    DayOfWeek dow = DayOfWeek.of(firstDayOfMonth.get(ChronoField.DAY_OF_WEEK));
    if (dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY) {
      return getNextWeekday(firstDayOfMonth);
    }
    return firstDayOfMonth;
  }

  private static Temporal getNextWeekday(Temporal temporal) {
    DayOfWeek dow = DayOfWeek.of(temporal.get(ChronoField.DAY_OF_WEEK));
    int dayToAdd = 1;
    if (dow == DayOfWeek.FRIDAY) {
      dayToAdd = 3;
    } else if (dow == DayOfWeek.SATURDAY) {
      dayToAdd = 2;
    }
    return temporal.plus(dayToAdd, ChronoUnit.DAYS);
  }

  private static Temporal getLastWeekdayOfMonth(Temporal temporal) {
    Temporal lastDayOfMonth = temporal.with(TemporalAdjusters.lastDayOfMonth());
    DayOfWeek dow = DayOfWeek.of(lastDayOfMonth.get(ChronoField.DAY_OF_WEEK));
    if (dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY) {
      return getPreviousWeekday(lastDayOfMonth);
    }
    return lastDayOfMonth;
  }

  private static Temporal getPreviousWeekday(Temporal temporal) {
    DayOfWeek dow = DayOfWeek.of(temporal.get(ChronoField.DAY_OF_WEEK));
    int dayToSubtract = 1;
    if (dow == DayOfWeek.MONDAY) {
      dayToSubtract = 3;
    } else if (dow == DayOfWeek.SUNDAY) {
      dayToSubtract = 2;
    }
    return temporal.minus(dayToSubtract, ChronoUnit.DAYS);
  }

  private static Temporal getFirstWeekendDayOfMonth(Temporal temporal) {
    Temporal firstDayOfMonth = temporal.with(TemporalAdjusters.firstDayOfMonth());
    DayOfWeek dow = DayOfWeek.of(firstDayOfMonth.get(ChronoField.DAY_OF_WEEK));
    if (dow != DayOfWeek.SATURDAY && dow != DayOfWeek.SUNDAY) {
      return getNextWeekendDay(firstDayOfMonth);
    }
    return firstDayOfMonth;
  }

  private static Temporal getNextWeekendDay(Temporal temporal) {
    DayOfWeek dow = DayOfWeek.of(temporal.get(ChronoField.DAY_OF_WEEK));
    int dayToAdd = 1;
    if (dow == DayOfWeek.SUNDAY) {
      dayToAdd = 6;
    } else if (dow == DayOfWeek.MONDAY) {
      dayToAdd = 5;
    } else if (dow == DayOfWeek.TUESDAY) {
      dayToAdd = 4;
    } else if (dow == DayOfWeek.WEDNESDAY) {
      dayToAdd = 3;
    } else if (dow == DayOfWeek.THURSDAY) {
      dayToAdd = 2;
    }
    return temporal.plus(dayToAdd, ChronoUnit.DAYS);
  }

  private static Temporal getLastWeekendDayOfMonth(Temporal temporal) {
    Temporal lastDayOfMonth = temporal.with(TemporalAdjusters.lastDayOfMonth());
    DayOfWeek dow = DayOfWeek.of(lastDayOfMonth.get(ChronoField.DAY_OF_WEEK));
    if (dow != DayOfWeek.SATURDAY && dow != DayOfWeek.SUNDAY) {
      return getPreviousWeekendDay(lastDayOfMonth);
    }
    return lastDayOfMonth;
  }

  private static Temporal getPreviousWeekendDay(Temporal temporal) {
    DayOfWeek dow = DayOfWeek.of(temporal.get(ChronoField.DAY_OF_WEEK));
    int dayToSubtract = 1;
    if (dow == DayOfWeek.SATURDAY) {
      dayToSubtract = 6;
    } else if (dow == DayOfWeek.FRIDAY) {
      dayToSubtract = 5;
    } else if (dow == DayOfWeek.THURSDAY) {
      dayToSubtract = 4;
    } else if (dow == DayOfWeek.WEDNESDAY) {
      dayToSubtract = 3;
    } else if (dow == DayOfWeek.TUESDAY) {
      dayToSubtract = 2;
    }
    return temporal.minus(dayToSubtract, ChronoUnit.DAYS);
  }

  private MoreTemporalAdjusters() {}
}
