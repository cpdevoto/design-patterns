package org.devoware.dice;

import java.util.List;

import com.google.common.collect.ImmutableList;

class Modifier implements DieRollExpression {
  private final int value;

  Modifier(int value) {
    this.value = value;
  }

  @Override
  public double dpr() {
    return value;
  }

  @Override
  public int roll() {
    return value;
  }

  @Override
  public List<Dice> getDice() {
    return ImmutableList.of();
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }

}
