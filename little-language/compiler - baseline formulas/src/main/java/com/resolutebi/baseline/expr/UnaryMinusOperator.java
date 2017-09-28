package com.resolutebi.baseline.expr;

import static java.util.Objects.requireNonNull;

class UnaryMinusOperator implements Expression<Double> {

  private NumericLiteral literal;
  
  static UnaryMinusOperator create(NumericLiteral literal) {
    return new UnaryMinusOperator(literal);
  }
  
  private UnaryMinusOperator(NumericLiteral literal) {
    this.literal = requireNonNull(literal, "literal cannot be null");
  }

  @Override
  public Double evaluate(Inputs inputs) {
    return -1 * literal.evaluate(inputs);
  }

  @Override
  public Class<Double> getType() {
    return Double.class;
  }

  NumericLiteral getLiteral() {
    return literal;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((literal == null) ? 0 : literal.hashCode());
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
    UnaryMinusOperator other = (UnaryMinusOperator) obj;
    if (literal == null) {
      if (other.literal != null)
        return false;
    } else if (!literal.equals(other.literal))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "-"+ literal;
  }

}
