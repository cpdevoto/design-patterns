package org.devoware.dice;

import static java.util.Objects.requireNonNull;

import java.util.List;

class ParenthesesExpression implements DieRollExpression {

  private final DieRollExpression expression;

  ParenthesesExpression(DieRollExpression expression) {
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
    return expression.getDice();
  }

  @Override
  public String toString() {
    return "(" + expression.toString() + ")";
  }

}
