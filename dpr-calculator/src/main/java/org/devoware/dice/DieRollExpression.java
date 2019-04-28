package org.devoware.dice;

import static java.util.Objects.requireNonNull;

import java.util.List;

public interface DieRollExpression {

  public static DieRollExpression plus(DieRollExpression d1, DieRollExpression d2) {
    requireNonNull(d1, "d1 cannot be null");
    requireNonNull(d2, "d2 cannot be null");
    return new PlusExpression(d1, d2);
  }

  public static DieRollExpression minus(DieRollExpression d1, DieRollExpression d2) {
    requireNonNull(d1, "d1 cannot be null");
    requireNonNull(d2, "d2 cannot be null");
    return new MinusExpression(d1, d2);
  }

  public double dpr();

  public double roll();

  public List<Dice> getDice();

}
