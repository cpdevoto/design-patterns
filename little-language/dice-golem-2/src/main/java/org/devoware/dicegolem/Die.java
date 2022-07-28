package org.devoware.dicegolem;

import static java.util.Objects.requireNonNull;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;


enum Die {
  D4(4), D6(6), D8(8), D10(10), D12(12), D20(20), D100(100);

  private static AtomicReference<RandomNumberGenerator> RNG =
      new AtomicReference<>(RandomNumberGenerator.DEFAULT);

  private static Map<Integer, Die> DIE_MAP = Arrays.stream(Die.values())
      .collect(Collectors.toUnmodifiableMap(Die::type, Function.identity()));

  private final int type;

  static void setRandomNumberGenerator(RandomNumberGenerator rng) {
    requireNonNull(rng, "rng cannot be null");
    RNG.set(rng);
  }

  static void clearRandomNumberGenerator() {
    RNG.set(RandomNumberGenerator.DEFAULT);
  }

  static Die get(int type) {
    return DIE_MAP.get(type);
  }

  private Die(int type) {
    this.type = type;
  }

  int roll() {
    return RNG.get().nextInt(type);
  }

  int type() {
    return type;
  }

}
