package org.devoware.dicegolem.dice;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Objects;
import java.util.stream.IntStream;

class DiceExpression implements Expression {

  private final int numDice;
  private final Die die;

  DiceExpression(int numDice, Die die) {
    checkArgument(numDice > 0, "numDice must be greater than zero");
    Objects.requireNonNull(die, "die cannot be null");
    this.numDice = numDice;
    this.die = die;
  }

  @Override
  public int roll() {
    return IntStream.range(0, numDice)
        .map(i -> die.roll())
        .sum();
  }

  @Override
  public int hashCode() {
    return Objects.hash(die, numDice);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    DiceExpression other = (DiceExpression) obj;
    return die == other.die && numDice == other.numDice;
  }

  @Override
  public String toString() {
    return numDice + "d" + die.type();
  }


}
