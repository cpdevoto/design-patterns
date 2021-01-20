package org.dicegolem.model;

import java.util.List;

public interface DieRollAggregator {
  static final int SAMPLE_SIZE = 1_000_000;

  public int aggregate(List<Integer> rolls);

  public double aggregateAverages(int numDice, Die die, DieRollModifier modifier);
}
