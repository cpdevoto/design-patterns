package org.dicegolem.model;

import static java.util.Objects.requireNonNull;

public class UnaryMinus implements DiceRollExpression {
  private final DiceRollExpression operand;

  public UnaryMinus(DiceRollExpression operand) {
    this.operand = requireNonNull(operand, "operand cannot be null");
  }

  @Override
  public int roll() {
    return -1 * operand.roll();
  }

  @Override
  public double average() {
    return -1 * operand.average();
  }

  @Override
  public double averageDiceOnly() {
    return -1 * operand.averageDiceOnly();
  }

  public DiceRollExpression getOperand() {
    return operand;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((operand == null) ? 0 : operand.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    UnaryMinus other = (UnaryMinus) obj;
    if (operand == null) {
      if (other.operand != null)
        return false;
    } else if (!operand.equals(other.operand))
      return false;
    return true;
  }

}
