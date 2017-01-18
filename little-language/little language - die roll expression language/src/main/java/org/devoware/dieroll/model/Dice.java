package org.devoware.dieroll.model;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

public class Dice implements ValueGenerator {

  private final int numDice;
  private final RandomValueGenerator die;
  
  public Dice(int numDice, RandomValueGenerator die) {
    checkArgument(numDice > 0, "numDice must be greater than zero.");
    requireNonNull(die, "die cannot be null");
    this.numDice = numDice;
    this.die = die;
  }
  
  public int getNumDice() {
    return numDice;
  }

  public RandomValueGenerator getRandomValueGenerator() {
    return die;
  }

  @Override
  public int value() {
    int total = 0;
    for (int value : rawValues()) {
      total += value;
    }
    return total;
  }
  
  public int [] rawValues () {
    final int [] values = new int[numDice];
    for (int i = 0; i < numDice; i++) {
      values[i] = die.value();
    }
    return values;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((die == null) ? 0 : die.hashCode());
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
    Dice other = (Dice) obj;
    if (die == null) {
      if (other.die != null)
        return false;
    } else if (!die.equals(other.die))
      return false;
    if (numDice != other.numDice)
      return false;
    return true;
  }

}
