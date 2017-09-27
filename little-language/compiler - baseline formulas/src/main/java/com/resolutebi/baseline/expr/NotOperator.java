package com.resolutebi.baseline.expr;

import static java.util.Objects.requireNonNull;

class NotOperator implements LogicalOperator {
  
  private final Expression<Boolean> expr;

  static NotOperator create(Expression<Boolean> expr) {
    return new NotOperator(expr);
  }
  
  private NotOperator(Expression<Boolean> expr) {
    this.expr = requireNonNull(expr, "expr cannot be null");
  }

  @Override
  public Boolean value(Inputs inputs) {
    requireNonNull(inputs, "inputs cannot be null");
    return !expr.value(inputs);
  }

  @Override
  public Class<Boolean> getType() {
    return Boolean.class;
  }

  Expression<Boolean> getExpr() {
    return expr;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((expr == null) ? 0 : expr.hashCode());
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
    NotOperator other = (NotOperator) obj;
    if (expr == null) {
      if (other.expr != null)
        return false;
    } else if (!expr.equals(other.expr))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "!("+ expr + ")";
  }

}
