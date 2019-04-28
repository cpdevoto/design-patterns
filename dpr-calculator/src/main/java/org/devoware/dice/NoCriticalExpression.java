package org.devoware.dice;

import static java.util.Objects.requireNonNull;

import java.util.List;

import com.google.common.collect.ImmutableList;

class NoCriticalExpression implements DieRollExpression {

  private final DieRollExpression expression;

  NoCriticalExpression(DieRollExpression expression) {
    this.expression = requireNonNull(expression, "expression cannot be null");
  }

  @Override
  public double dpr() {
    return expression.dpr();
  }

  @Override
  public double roll() {
    return expression.roll();
  }

  @Override
  public List<Dice> getDice() {
    return ImmutableList.of();
  }

  @Override
  public String toString() {
    return expression.toString() + "nc";
  }

}
