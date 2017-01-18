package org.devoware.dieroll.model;

import static java.util.Objects.requireNonNull;

public class Minus implements ValueGenerator {
  private final ValueGenerator operand1;
  private final ValueGenerator operand2;

  public Minus(ValueGenerator operand1, ValueGenerator operand2) {
    this.operand1 = requireNonNull(operand1, "operand1 cannot be null");
    this.operand2 = requireNonNull(operand2, "operand2 cannot be null");
  }

  @Override
  public int value() {
    return operand1.value() - operand2.value();
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
