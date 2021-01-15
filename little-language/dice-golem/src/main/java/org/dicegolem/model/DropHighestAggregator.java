package org.dicegolem.model;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.List;

public class DropHighestAggregator implements DieRollAggregator {

  private final int numRolls;

  public DropHighestAggregator(int numRolls) {
    checkArgument(numRolls > 0, "numRolls must be greater than zero");
    this.numRolls = numRolls;

  }

  @Override
  public int aggregate(List<Integer> rolls) {
    return rolls.stream()
        .sorted()
        .mapToInt(Integer::intValue)
        .limit(Math.max(0, rolls.size() - numRolls))
        .sum();
  }

  public int getNumRolls() {
    return numRolls;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + numRolls;
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
    DropHighestAggregator other = (DropHighestAggregator) obj;
    if (numRolls != other.numRolls)
      return false;
    return true;
  }


}