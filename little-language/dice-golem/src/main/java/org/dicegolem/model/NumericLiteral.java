package org.dicegolem.model;

public class NumericLiteral implements DiceRollExpression {
  private final int value;

  public NumericLiteral(int value) {
    this.value = value;
  }

  @Override
  public int roll() {
    return value;
  }

  @Override
  public double average() {
    return value;
  }

  @Override
  public double averageDiceOnly() {
    return 0;
  }


  public int getValue() {
    return value;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + value;
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
    NumericLiteral other = (NumericLiteral) obj;
    if (value != other.value)
      return false;
    return true;
  }

}
