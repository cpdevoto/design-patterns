package org.devoware.dieroll.model;

import static java.util.Objects.requireNonNull;

public class UnaryMinus implements ValueGenerator {
  private final ValueGenerator operand;

  public UnaryMinus(ValueGenerator operand) {
    this.operand = requireNonNull(operand, "operand cannot be null");
  }

  @Override
  public int value() {
    return -1 * operand.value();
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
