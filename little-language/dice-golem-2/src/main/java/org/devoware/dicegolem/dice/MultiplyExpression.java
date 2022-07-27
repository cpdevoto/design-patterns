package org.devoware.dicegolem.dice;

import static java.util.Objects.requireNonNull;

import java.util.Objects;

class MultiplyExpression implements Expression {

  private final Expression left;
  private final Expression right;

  MultiplyExpression(Expression left, Expression right) {
    this.left = requireNonNull(left, "left cannot be null");
    this.right = requireNonNull(right, "right cannot be null");
  }

  @Override
  public int roll() {
    return left.roll() * right.roll();
  }

  @Override
  public int hashCode() {
    return Objects.hash(left, right);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    MultiplyExpression other = (MultiplyExpression) obj;
    return Objects.equals(left, other.left) && Objects.equals(right, other.right);
  }

  @Override
  public String toString() {
    return "(" + left.toString() + " * " + right.toString() + ")";
  }
}
