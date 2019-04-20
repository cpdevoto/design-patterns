package org.devoware.dice;

import java.util.List;

public interface DieRollExpression {

  public double dpr();

  public int roll();

  public List<Dice> getDice();

}
