package com.resolute.utils.simple;

import static com.resolute.utils.simple.MoreTemporalAdjusters.firstDayOfWeek;
import static com.resolute.utils.simple.MoreTemporalAdjusters.firstWeekdayOfMonth;
import static com.resolute.utils.simple.MoreTemporalAdjusters.firstWeekendDayOfMonth;
import static com.resolute.utils.simple.MoreTemporalAdjusters.lastDayOfWeek;
import static com.resolute.utils.simple.MoreTemporalAdjusters.lastWeekdayOfMonth;
import static com.resolute.utils.simple.MoreTemporalAdjusters.lastWeekendDayOfMonth;
import static com.resolute.utils.simple.MoreTemporalAdjusters.nextWeekday;
import static com.resolute.utils.simple.MoreTemporalAdjusters.nextWeekendDay;
import static com.resolute.utils.simple.MoreTemporalAdjusters.previousWeekday;
import static com.resolute.utils.simple.MoreTemporalAdjusters.previousWeekendDay;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class MoreTemporalAdjustersTest {

  @Test
  public void test_first_day_of_week() {
    assertThat(LocalDate.of(2019, 3, 17).with(firstDayOfWeek()))
        .isEqualTo(LocalDate.of(2019, 3, 17));
    assertThat(LocalDate.of(2019, 3, 19).with(firstDayOfWeek()))
        .isEqualTo(LocalDate.of(2019, 3, 17));
    assertThat(LocalDate.of(2019, 3, 23).with(firstDayOfWeek()))
        .isEqualTo(LocalDate.of(2019, 3, 17));
    assertThat(LocalDate.of(2019, 3, 24).with(firstDayOfWeek()))
        .isEqualTo(LocalDate.of(2019, 3, 24));
  }

  @Test
  public void test_last_day_of_week() {
    assertThat(LocalDate.of(2019, 3, 17).with(lastDayOfWeek()))
        .isEqualTo(LocalDate.of(2019, 3, 23));
    assertThat(LocalDate.of(2019, 3, 19).with(lastDayOfWeek()))
        .isEqualTo(LocalDate.of(2019, 3, 23));
    assertThat(LocalDate.of(2019, 3, 23).with(lastDayOfWeek()))
        .isEqualTo(LocalDate.of(2019, 3, 23));
    assertThat(LocalDate.of(2019, 3, 24).with(lastDayOfWeek()))
        .isEqualTo(LocalDate.of(2019, 3, 30));
  }

  @Test
  public void test_first_weekday_of_month() {
    assertThat(LocalDate.of(2019, 3, 17).with(firstWeekdayOfMonth()))
        .isEqualTo(LocalDate.of(2019, 3, 1));
    assertThat(LocalDate.of(2019, 6, 17).with(firstWeekdayOfMonth()))
        .isEqualTo(LocalDate.of(2019, 6, 3));
  }

  @Test
  public void test_last_weekday_of_month() {
    assertThat(LocalDate.of(2019, 3, 17).with(lastWeekdayOfMonth()))
        .isEqualTo(LocalDate.of(2019, 3, 29));
    assertThat(LocalDate.of(2019, 4, 17).with(lastWeekdayOfMonth()))
        .isEqualTo(LocalDate.of(2019, 4, 30));
  }

  @Test
  public void test_first_weekend_day_of_month() {
    assertThat(LocalDate.of(2019, 3, 17).with(firstWeekendDayOfMonth()))
        .isEqualTo(LocalDate.of(2019, 3, 2));
    assertThat(LocalDate.of(2019, 6, 17).with(firstWeekendDayOfMonth()))
        .isEqualTo(LocalDate.of(2019, 6, 1));
  }

  @Test
  public void test_last_weekend_day_of_month() {
    assertThat(LocalDate.of(2019, 3, 17).with(lastWeekendDayOfMonth()))
        .isEqualTo(LocalDate.of(2019, 3, 31));
    assertThat(LocalDate.of(2019, 4, 17).with(lastWeekendDayOfMonth()))
        .isEqualTo(LocalDate.of(2019, 4, 28));
  }

  @Test
  public void test_next_weekday() {
    assertThat(LocalDate.of(2019, 3, 17).with(nextWeekday())).isEqualTo(LocalDate.of(2019, 3, 18));
    assertThat(LocalDate.of(2019, 3, 18).with(nextWeekday())).isEqualTo(LocalDate.of(2019, 3, 19));
    assertThat(LocalDate.of(2019, 3, 19).with(nextWeekday())).isEqualTo(LocalDate.of(2019, 3, 20));
    assertThat(LocalDate.of(2019, 3, 20).with(nextWeekday())).isEqualTo(LocalDate.of(2019, 3, 21));
    assertThat(LocalDate.of(2019, 3, 21).with(nextWeekday())).isEqualTo(LocalDate.of(2019, 3, 22));
    assertThat(LocalDate.of(2019, 3, 22).with(nextWeekday())).isEqualTo(LocalDate.of(2019, 3, 25));
    assertThat(LocalDate.of(2019, 3, 23).with(nextWeekday())).isEqualTo(LocalDate.of(2019, 3, 25));
    assertThat(LocalDate.of(2019, 3, 24).with(nextWeekday())).isEqualTo(LocalDate.of(2019, 3, 25));
  }

  @Test
  public void test_previous_weekday() {
    assertThat(LocalDate.of(2019, 3, 17).with(previousWeekday()))
        .isEqualTo(LocalDate.of(2019, 3, 15));
    assertThat(LocalDate.of(2019, 3, 18).with(previousWeekday()))
        .isEqualTo(LocalDate.of(2019, 3, 15));
    assertThat(LocalDate.of(2019, 3, 19).with(previousWeekday()))
        .isEqualTo(LocalDate.of(2019, 3, 18));
    assertThat(LocalDate.of(2019, 3, 20).with(previousWeekday()))
        .isEqualTo(LocalDate.of(2019, 3, 19));
    assertThat(LocalDate.of(2019, 3, 21).with(previousWeekday()))
        .isEqualTo(LocalDate.of(2019, 3, 20));
    assertThat(LocalDate.of(2019, 3, 22).with(previousWeekday()))
        .isEqualTo(LocalDate.of(2019, 3, 21));
    assertThat(LocalDate.of(2019, 3, 23).with(previousWeekday()))
        .isEqualTo(LocalDate.of(2019, 3, 22));
    assertThat(LocalDate.of(2019, 3, 24).with(previousWeekday()))
        .isEqualTo(LocalDate.of(2019, 3, 22));
    assertThat(LocalDate.of(2019, 3, 25).with(previousWeekday()))
        .isEqualTo(LocalDate.of(2019, 3, 22));
    assertThat(LocalDate.of(2019, 3, 26).with(previousWeekday()))
        .isEqualTo(LocalDate.of(2019, 3, 25));
  }

  @Test
  public void test_next_weekend_day() {
    assertThat(LocalDate.of(2019, 3, 17).with(nextWeekendDay()))
        .isEqualTo(LocalDate.of(2019, 3, 23));
    assertThat(LocalDate.of(2019, 3, 18).with(nextWeekendDay()))
        .isEqualTo(LocalDate.of(2019, 3, 23));
    assertThat(LocalDate.of(2019, 3, 19).with(nextWeekendDay()))
        .isEqualTo(LocalDate.of(2019, 3, 23));
    assertThat(LocalDate.of(2019, 3, 20).with(nextWeekendDay()))
        .isEqualTo(LocalDate.of(2019, 3, 23));
    assertThat(LocalDate.of(2019, 3, 21).with(nextWeekendDay()))
        .isEqualTo(LocalDate.of(2019, 3, 23));
    assertThat(LocalDate.of(2019, 3, 22).with(nextWeekendDay()))
        .isEqualTo(LocalDate.of(2019, 3, 23));
    assertThat(LocalDate.of(2019, 3, 23).with(nextWeekendDay()))
        .isEqualTo(LocalDate.of(2019, 3, 24));
    assertThat(LocalDate.of(2019, 3, 24).with(nextWeekendDay()))
        .isEqualTo(LocalDate.of(2019, 3, 30));
  }

  @Test
  public void test_previous_weekend_day() {
    assertThat(LocalDate.of(2019, 3, 17).with(previousWeekendDay()))
        .isEqualTo(LocalDate.of(2019, 3, 16));
    assertThat(LocalDate.of(2019, 3, 18).with(previousWeekendDay()))
        .isEqualTo(LocalDate.of(2019, 3, 17));
    assertThat(LocalDate.of(2019, 3, 19).with(previousWeekendDay()))
        .isEqualTo(LocalDate.of(2019, 3, 17));
    assertThat(LocalDate.of(2019, 3, 20).with(previousWeekendDay()))
        .isEqualTo(LocalDate.of(2019, 3, 17));
    assertThat(LocalDate.of(2019, 3, 21).with(previousWeekendDay()))
        .isEqualTo(LocalDate.of(2019, 3, 17));
    assertThat(LocalDate.of(2019, 3, 22).with(previousWeekendDay()))
        .isEqualTo(LocalDate.of(2019, 3, 17));
    assertThat(LocalDate.of(2019, 3, 23).with(previousWeekendDay()))
        .isEqualTo(LocalDate.of(2019, 3, 17));
    assertThat(LocalDate.of(2019, 3, 24).with(previousWeekendDay()))
        .isEqualTo(LocalDate.of(2019, 3, 23));
    assertThat(LocalDate.of(2019, 3, 25).with(previousWeekendDay()))
        .isEqualTo(LocalDate.of(2019, 3, 24));
    assertThat(LocalDate.of(2019, 3, 26).with(previousWeekendDay()))
        .isEqualTo(LocalDate.of(2019, 3, 24));
  }
}
