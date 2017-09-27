package com.resolutebi.baseline.expr;

import static java.util.Objects.requireNonNull;

class LessThanOperator implements RelationalOperator {
  
  private final Expression<Double> expr1;
  private final Expression<Double> expr2;

  static LessThanOperator create(Expression<Double> expr1, Expression<Double> expr2) {
    return new LessThanOperator(expr1, expr2);
  }

  private LessThanOperator(Expression<Double> expr1, Expression<Double> expr2) {
    this.expr1 = requireNonNull(expr1, "expr1 cannot be null");
    this.expr2 = requireNonNull(expr2, "expr2 cannot be null");
  }

  @Override
  public Boolean value(Inputs inputs) {
    requireNonNull(inputs, "inputs cannot be null");
    return expr1.value(inputs) < expr2.value(inputs);
  }

  @Override
  public Class<Boolean> getType() {
    return Boolean.class;
  }

  Expression<Double> getExpr1() {
    return expr1;
  }

  Expression<Double> getExpr2() {
    return expr2;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((expr1 == null) ? 0 : expr1.hashCode());
    result = prime * result + ((expr2 == null) ? 0 : expr2.hashCode());
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
    LessThanOperator other = (LessThanOperator) obj;
    if (expr1 == null) {
      if (other.expr1 != null)
        return false;
    } else if (!expr1.equals(other.expr1))
      return false;
    if (expr2 == null) {
      if (other.expr2 != null)
        return false;
    } else if (!expr2.equals(other.expr2))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "("+ expr1 + ") < (" + expr2 + ")";
  }

}
