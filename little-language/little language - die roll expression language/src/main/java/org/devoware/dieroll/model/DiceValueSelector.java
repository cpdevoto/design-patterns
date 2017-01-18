package org.devoware.dieroll.model;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

public abstract class DiceValueSelector implements ValueGenerator {
  private final Dice dice;
  private final int numDice;

  protected DiceValueSelector(Dice dice, int numDice) {
    requireNonNull(dice, "dice cannot be null");
    checkArgument(numDice > 0, "numDice must be greater than zero");
    checkArgument(numDice < dice.getNumDice(), "numDice must be less than " + dice.getNumDice());
    this.dice = dice;
    this.numDice = numDice;
  }

  public Dice getDice() {
    return dice;
  }

  public int getNumDice() {
    return numDice;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((dice == null) ? 0 : dice.hashCode());
    result = prime * result + numDice;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    DiceValueSelector other = (DiceValueSelector) obj;
    if (dice == null) {
      if (other.dice != null)
        return false;
    } else if (!dice.equals(other.dice))
      return false;
    if (numDice != other.numDice)
      return false;
    return true;
  }

}
