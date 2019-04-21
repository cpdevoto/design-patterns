package org.devoware.dice;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import com.google.common.collect.ImmutableList;

public class Dice implements DieRollExpression {
  private final int number;
  private final Die die;
  private final Integer rerollOnceThreshold;

  public static DieRollExpression parse(String expression) {
    return Parser.parse(expression);
  }

  Dice(int number, Die die, Integer rerollOnceThreshold) {
    checkArgument(number > 0, "number must be greater than zero");
    requireNonNull(die, "die cannot be null");
    checkArgument(rerollOnceThreshold == null || rerollOnceThreshold <= die.type(),
        "rerollOnceThreshold must be less than or equal to " + die.type());
    this.number = number;
    this.die = die;
    this.rerollOnceThreshold = rerollOnceThreshold;
  }

  public Optional<Integer> getRerollOnceThreshold() {
    return Optional.ofNullable(rerollOnceThreshold);
  }

  public int getNumber() {
    return number;
  }

  public Die getDie() {
    return die;
  }

  @Override
  public double dpr() {
    if (rerollOnceThreshold != null) {
      return IntStream.range(0, number)
          .mapToDouble(i -> computeRerollDpr())
          .sum();
    }
    return IntStream.range(0, number)
        .mapToDouble(i -> die.dpr())
        .sum();
  }

  private double computeRerollDpr() {
    return IntStream.range(0, die.type())
        .mapToDouble(i -> i < rerollOnceThreshold ? die.dpr() : i + 1.0)
        .sum() / die.type();
  }

  @Override
  public List<Dice> getDice() {
    return ImmutableList.of(this);
  }

  @Override
  public int roll() {
    return IntStream.range(0, number)
        .map(i -> {
          int r = die.roll();
          if (rerollOnceThreshold != null && r <= rerollOnceThreshold) {
            r = die.roll();
          }
          return r;
        })
        .sum();
  }

  @Override
  public String toString() {
    return number + "d" + die.type()
        + (rerollOnceThreshold != null ? "ro<" + rerollOnceThreshold : "");
  }
}
