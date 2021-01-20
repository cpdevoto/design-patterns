package org.dicegolem.model;

import java.util.List;
import java.util.stream.IntStream;

public class SumAggregator implements DieRollAggregator {

  public SumAggregator() {}

  @Override
  public int aggregate(List<Integer> rolls) {
    return rolls.stream()
        .mapToInt(Integer::intValue)
        .sum();
  }

  @Override
  public double aggregateAverages(int numDice, Die die, DieRollModifier modifier) {
    double total = 0;
    int sampleSize = SAMPLE_SIZE;
    for (int i = 0; i < sampleSize; i++) {
      total += IntStream.range(0, numDice)
          .map(idx -> {
            int roll = die.roll();
            if (modifier != null) {
              roll = modifier.modify(die, roll);
            }
            return roll;
          })
          .sum();
    }

    return total / sampleSize;
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
