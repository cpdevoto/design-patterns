package org.devoware.dice;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.stream.IntStream;

import com.google.common.collect.ImmutableList;

public class Dice implements DieRollExpression {
  private final int number;
  private final Die die;
  private final boolean isWeapon;

  public static DieRollExpression parse(String expression) {
    return Parser.parse(expression);
  }

  Dice(int number, Die die, boolean isWeapon) {
    checkArgument(number > 0, "number must be greater than zero");
    this.die = requireNonNull(die, "die cannot be null");
    this.number = number;
    this.isWeapon = false;
  }

  public boolean isWeapon() {
    return isWeapon;
  }

  public int getNumber() {
    return number;
  }

  public Die getDie() {
    return die;
  }

  @Override
  public double dpr() {
    return IntStream.range(0, number)
        .mapToDouble(i -> die.dpr())
        .sum();
  }

  @Override
  public List<Dice> getDice() {
    return ImmutableList.of(this);
  }

  @Override
  public int roll() {
    return IntStream.range(0, number)
        .map(i -> die.roll())
        .sum();
  }

  @Override
  public String toString() {
    return number + "d" + die.type();
  }
}
