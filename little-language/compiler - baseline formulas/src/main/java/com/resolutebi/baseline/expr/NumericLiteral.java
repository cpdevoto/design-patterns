package com.resolutebi.baseline.expr;

import static java.util.Objects.requireNonNull;

import java.math.BigDecimal;

class NumericLiteral implements Expression<Double> {

  private final double value;
  
  static NumericLiteral create (double value) {
    return new NumericLiteral(value);
  }
  
  private NumericLiteral(double value) {
    this.value = value;
  }

  @Override
  public Double value(Inputs inputs) {
    requireNonNull(inputs, "inputs cannot be null");
    return value;
  }

  @Override
  public Class<Double> getType() {
    return Double.class;
  }

  double getValue() {
    return value;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    long temp;
    temp = Double.doubleToLongBits(value);
    result = prime * result + (int) (temp ^ (temp >>> 32));
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
    if (Double.doubleToLongBits(value) != Double.doubleToLongBits(other.value))
      return false;
    return true;
  }
  
  @Override
  public String toString() {
    String s = new BigDecimal(value).toPlainString();
    int idx = s.indexOf('.');
    if (idx != -1) {
      if (s.length() - 1 - idx > 4) {
        s = String.format("%.4f", value);
      }
    }
    return s;
  }

}
