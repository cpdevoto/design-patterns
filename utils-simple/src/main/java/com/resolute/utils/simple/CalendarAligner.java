package com.resolute.utils.simple;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.time.DateTimeException;
import java.time.Duration;
import java.time.temporal.ChronoField;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalField;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class CalendarAligner implements TemporalAdjuster {
  private static final List<TemporalField> orderedFields = Arrays.asList(
      ChronoField.NANO_OF_SECOND,
      ChronoField.MICRO_OF_SECOND,
      ChronoField.MILLI_OF_SECOND,
      ChronoField.SECOND_OF_MINUTE,
      ChronoField.MINUTE_OF_HOUR,
      ChronoField.HOUR_OF_DAY,
      ChronoField.DAY_OF_MONTH,
      ChronoField.MONTH_OF_YEAR,
      ChronoField.YEAR);

  private final int fieldIdx;
  private final long interval;
  private final TemporalField field;
  private final CalendarAlignmentStrategy strategy;

  CalendarAligner(long interval, TemporalField field,
      CalendarAlignmentStrategy strategy) {
    this.fieldIdx = validateConstructorParams(interval, field, strategy);
    this.interval = interval;
    this.field = field;
    this.strategy = strategy;
  }

  public long getInterval() {
    return interval;
  }

  public TemporalField getTemporalField() {
    return field;
  }

  @Override
  public Temporal adjustInto(Temporal temporal) {
    validateAdjustIntoParams(temporal, field);
    // Set the floor
    return strategy.compute(temporal, interval, field, fieldIdx);
  }

  @FunctionalInterface
  interface CalendarAlignmentStrategy {
    Temporal compute(Temporal temporal, long interval, TemporalField field, int fieldIdx);
  }

  static Temporal computeNearest(Temporal temporal, long interval, TemporalField field,
      int fieldIdx) {
    Temporal floor = computeFloor(temporal, interval, field, fieldIdx);
    Temporal ceiling = computeCeiling(temporal, interval, field, fieldIdx);
    Duration durationFromFloor = Duration.between(floor, temporal);
    Duration durationToCeiling = Duration.between(temporal, ceiling);
    return durationFromFloor.compareTo(durationToCeiling) < 0 ? floor : ceiling;
  }

  static Temporal computeFloor(Temporal temporal, long interval, TemporalField field,
      int fieldIdx) {
    Temporal floor = alignToBase(temporal, fieldIdx);
    Duration durationMin =
        Duration.between(temporal.minus(interval, field.getBaseUnit()), temporal);
    Duration durationFromFloor = Duration.between(floor, temporal);
    while (durationFromFloor.compareTo(durationMin) >= 0) {
      floor = floor.plus(interval, field.getBaseUnit());
      durationFromFloor = Duration.between(floor, temporal);
    }
    return floor;
  }

  static Temporal computeCeiling(Temporal temporal, long interval, TemporalField field,
      int fieldIdx) {
    Temporal ceiling = alignToBase(temporal, fieldIdx);
    Duration durationFromCeiling = Duration.between(temporal, ceiling);
    while (durationFromCeiling.isNegative()) {
      ceiling = ceiling.plus(interval, field.getBaseUnit());
      durationFromCeiling = Duration.between(temporal, ceiling);
    }
    return ceiling;
  }

  private static Temporal alignToBase(Temporal temporal, int fieldIdx) {
    Temporal floor = temporal;
    for (int i = 0; i <= fieldIdx; i++) {
      TemporalField f = orderedFields.get(i);
      if (temporal.isSupported(f)) {
        if (f == ChronoField.YEAR) {
          floor = floor.with(f, 1970);
        } else {
          floor = floor.with(f, f.range().getMinimum());
        }
      }
    }
    return floor;
  }

  private static void validateAdjustIntoParams(Temporal temporal, TemporalField field) {
    requireNonNull(temporal, "temporal cannot be null");
    checkArgument(field.getBaseUnit().isSupportedBy(temporal), "temporal is not valid");
  }

  private static int validateConstructorParams(long interval, TemporalField field,
      CalendarAlignmentStrategy strategy) {
    checkArgument(interval > 0, "interval must be positive");
    requireNonNull(field, "field cannot be null");
    requireNonNull(strategy, "strategy cannot be null");
    orderedFields.stream()
        .filter(f -> f == field)
        .findAny()
        .orElseThrow(() -> new IllegalArgumentException("invalid field"));
    try {
      field.range().checkValidValue(interval, field);
    } catch (DateTimeException e) {
      throw new IllegalArgumentException("invalid interval", e);
    }
    if (field != ChronoField.YEAR) {
      checkArgument(
          (field.range().getMaximum() - field.range().getMinimum() + 1) % interval == 0,
          "The interval must be evenly divisible into the range of the field");
    }
    return IntStream.range(0, orderedFields.size())
        .filter(i -> orderedFields.get(i) == field)
        .findFirst()
        .getAsInt();

  }


}
