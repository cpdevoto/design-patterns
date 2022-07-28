package org.devoware.dicegolem;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Objects;

class ValueExpression implements Expression {

  private final int value;

  ValueExpression(int value) {
    checkArgument(value >= 0, "value must be greater than or equal to zero");
    this.value = value;
  }

  @Override
  public int roll() {
    return value;
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    ValueExpression other = (ValueExpression) obj;
    return value == other.value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }

}
