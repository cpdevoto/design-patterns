package com.resolutebi.baseline.expr;

class DoubleToken extends Token {

  private final double value;
  
  DoubleToken(double value, Position position) {
    super(Type.DOUBLE, position);
    this.value = value;
  }
  
  double value () {
    return value;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    long temp;
    temp = Double.doubleToLongBits(value);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    DoubleToken other = (DoubleToken) obj;
    if (Double.doubleToLongBits(value) != Double.doubleToLongBits(other.value))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "DoubleToken [value=" + value + ", getType()=" + getType() + ", getPosition()="
        + getPosition() + "]";
  }
  
}
