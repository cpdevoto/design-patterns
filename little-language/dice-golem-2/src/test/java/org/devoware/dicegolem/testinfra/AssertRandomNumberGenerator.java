package org.devoware.dicegolem.testinfra;

import static java.util.Objects.requireNonNull;

import java.util.function.Supplier;

import org.assertj.core.api.Assertions;

public class AssertRandomNumberGenerator {

  private final Supplier<Integer> actual;

  public static AssertRandomNumberGenerator assertThat(Supplier<Integer> rng) {
    return new AssertRandomNumberGenerator(rng);
  }

  private AssertRandomNumberGenerator(Supplier<Integer> actual) {
    this.actual = requireNonNull(actual, "actual cannot be null");
  }

  public AssertRandomNumberGenerator generatesNumbersBetween(int expectedMin, int expectedMax) {
    int min = Integer.MAX_VALUE;
    int max = Integer.MIN_VALUE;
    for (int i = 0; i < 1_000_000; i++) {
      int result = actual.get();
      if (result < min) {
        min = result;
      }
      if (result > max) {
        max = result;
      }
    }
    Assertions.assertThat(min).as("check minimum value").isEqualTo(expectedMin);
    Assertions.assertThat(max).as("check maximum value").isEqualTo(expectedMax);
    return this;

  }

}
