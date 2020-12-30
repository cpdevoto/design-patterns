package org.dicegolem.model;

import static java.util.Objects.requireNonNull;

public class Minus implements DiceRollExpression {
  private final DiceRollExpression operand1;
  private final DiceRollExpression operand2;

  public Minus(DiceRollExpression operand1, DiceRollExpression operand2) {
    this.operand1 = requireNonNull(operand1, "operand1 cannot be null");
    this.operand2 = requireNonNull(operand2, "operand2 cannot be null");
  }

  @Override
  public int roll() {
    return operand1.roll() - operand2.roll();
  }

  public DiceRollExpression getOperand1() {
    return operand1;
  }

  public DiceRollExpression getOperand2() {
    return operand2;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((operand1 == null) ? 0 : operand1.hashCode());
    result = prime * result + ((operand2 == null) ? 0 : operand2.hashCode());
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
    Minus other = (Minus) obj;
    if (operand1 == null) {
      if (other.operand1 != null)
        return false;
    } else if (!operand1.equals(other.operand1))
      return false;
    if (operand2 == null) {
      if (other.operand2 != null)
        return false;
    } else if (!operand2.equals(other.operand2))
      return false;
    return true;
  }

}
