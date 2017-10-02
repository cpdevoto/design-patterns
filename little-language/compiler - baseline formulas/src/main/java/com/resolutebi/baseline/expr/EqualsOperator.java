package com.resolutebi.baseline.expr;

import static java.util.Objects.requireNonNull;

class EqualsOperator implements RelationalOperator {
  
  private final Expression<?> expr1;
  private final Expression<?> expr2;

  static EqualsOperator create(Expression<?> expr1, Expression<?> expr2) {
    return new EqualsOperator(expr1, expr2);
  }
  
  private EqualsOperator(Expression<?> expr1, Expression<?> expr2) {
    this.expr1 = requireNonNull(expr1, "expr1 cannot be null");
    this.expr2 = requireNonNull(expr2, "expr2 cannot be null");
  }

  @Override
  public Boolean evaluate(Inputs inputs) {
    requireNonNull(inputs, "inputs cannot be null");
    return expr1.evaluate(inputs).equals(expr2.evaluate(inputs));
  }

  @Override
  public Class<Boolean> getType() {
    return Boolean.class;
  }

  Expression<?> getExpr1() {
    return expr1;
  }

  Expression<?> getExpr2() {
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
    EqualsOperator other = (EqualsOperator) obj;
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
    return "("+ expr1 + ") == (" + expr2 + ")";
  }
  
}
