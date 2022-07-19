package org.devoware.dicegolem.dice;

import static java.util.Objects.requireNonNull;

import java.util.Objects;

class UnaryMinusExpression implements Expression {

  private final ValueExpression expression;

  UnaryMinusExpression(ValueExpression expression) {
    this.expression = requireNonNull(expression, "expression cannot be null");
  }

  @Override
  public int roll() {
    return -1 * expression.roll();
  }

  @Override
  public int hashCode() {
    return Objects.hash(expression);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    UnaryMinusExpression other = (UnaryMinusExpression) obj;
    return Objects.equals(expression, other.expression);
  }

  @Override
  public String toString() {
    return "-" + expression.toString();
  }

}
