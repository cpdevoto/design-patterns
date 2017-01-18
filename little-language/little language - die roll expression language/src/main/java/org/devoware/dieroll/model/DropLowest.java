package org.devoware.dieroll.model;

import java.util.Arrays;

public class DropLowest extends DiceValueSelector {

  public DropLowest(Dice dice, int numDice) {
    super(dice, numDice);
  }

  @Override
  public int value() {
    int [] values = getDice().rawValues();
    Arrays.sort(values);
    int total = 0;
    int diceToKeep = getDice().getNumDice() - getNumDice();
    for (int i = 0; i < diceToKeep; i++) {
      total += values[values.length - (1 + i)];
    }
    return total;
  }


}
