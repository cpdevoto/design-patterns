package org.devoware.dice;

import java.util.List;

import com.google.common.collect.ImmutableList;

class FloatExpression implements DieRollExpression {
  private final double value;

  FloatExpression(double value) {
    this.value = value;
  }

  @Override
  public double dpr() {
    return value;
  }

  @Override
  public double roll() {
    return value;
  }

  @Override
  public List<Dice> getDice() {
    return ImmutableList.of();
  }

  @Override
  public String toString() {
    return String.format("%,.2f", value);
  }

}
