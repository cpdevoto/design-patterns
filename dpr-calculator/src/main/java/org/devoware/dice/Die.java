package org.devoware.dice;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toMap;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import com.google.common.collect.ImmutableMap;

public enum Die {
  D4(4), D6(6), D8(8), D10(10), D12(12), D20(20);

  private static final Map<Integer, Die> TYPES = Arrays.stream(Die.values())
      .collect(collectingAndThen(toMap(Die::type, Function.identity()), ImmutableMap::copyOf));

  private final int type;

  public static Optional<Die> get(int type) {
    return Optional.ofNullable(TYPES.get(type));
  }

  private Die(int type) {
    this.type = type;
  }

  public int type() {
    return type;
  }

  public double dpr() {
    return (1.0 + type) / 2.0;
  }

  public int roll() {
    return (int) (Math.random() * type + 1);
  }

  @Override
  public String toString() {
    return "d" + type;
  }


}
