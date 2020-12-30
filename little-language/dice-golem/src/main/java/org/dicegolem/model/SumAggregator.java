package org.dicegolem.model;

import java.util.List;

public class SumAggregator implements DieRollAggregator {

  public SumAggregator() {}

  @Override
  public int aggregate(List<Integer> rolls) {
    return rolls.stream()
        .mapToInt(Integer::intValue)
        .sum();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result;
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
    return true;
  }

}
