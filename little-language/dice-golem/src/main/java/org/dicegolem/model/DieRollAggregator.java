package org.dicegolem.model;

import java.util.List;

public interface DieRollAggregator {

  public int aggregate(List<Integer> rolls);
}
