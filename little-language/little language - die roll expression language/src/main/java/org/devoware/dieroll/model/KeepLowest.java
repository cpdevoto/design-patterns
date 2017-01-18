package org.devoware.dieroll.model;

import java.util.Arrays;

public class KeepLowest extends DiceValueSelector {

  public KeepLowest(Dice dice, int numDice) {
    super(dice, numDice);
  }

  @Override
  public int value() {
    int [] values = getDice().rawValues();
    Arrays.sort(values);
    int total = 0;
    int diceToKeep = getNumDice();
    for (int i = 0; i < diceToKeep; i++) {
      total += values[i];
    }
    return total;
  }


}
