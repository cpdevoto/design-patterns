package com.resolutebi.baseline.expr;

import static java.util.Objects.requireNonNull;

class IfExpression implements Expression<Double> {
  private final Expression<Boolean> condition;
  private final Expression<Double> ifBody;
  private final Expression<Double> elseBody;

  static IfExpression create(Expression<Boolean> condition, Expression<Double> ifBody,
      Expression<Double> elseBody) {
    return new IfExpression(condition, ifBody, elseBody);
  }
  
  private IfExpression(Expression<Boolean> condition, Expression<Double> ifBody,
      Expression<Double> elseBody) {
    this.condition = requireNonNull(condition, "condition cannot be null");
    this.ifBody = requireNonNull(ifBody, "ifBody cannot be null");
    this.elseBody = requireNonNull(elseBody, "elseBody cannot be null");
  }

  @Override
  public Double evaluate(Inputs inputs) {
    if (condition.evaluate(inputs)) {
      return ifBody.evaluate(inputs);
    }
    return elseBody.evaluate(inputs);
  }

  @Override
  public Class<Double> getType() {
    return Double.class;
  }

  Expression<Boolean> getCondition() {
    return condition;
  }

  Expression<Double> getIfBody() {
    return ifBody;
  }

  Expression<Double> getElseBody() {
    return elseBody;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((condition == null) ? 0 : condition.hashCode());
    result = prime * result + ((elseBody == null) ? 0 : elseBody.hashCode());
    result = prime * result + ((ifBody == null) ? 0 : ifBody.hashCode());
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
    IfExpression other = (IfExpression) obj;
    if (condition == null) {
      if (other.condition != null)
        return false;
    } else if (!condition.equals(other.condition))
      return false;
    if (elseBody == null) {
      if (other.elseBody != null)
        return false;
    } else if (!elseBody.equals(other.elseBody))
      return false;
    if (ifBody == null) {
      if (other.ifBody != null)
        return false;
    } else if (!ifBody.equals(other.ifBody))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "if ("+ condition + ") (" + ifBody + ") else (" + elseBody + ")";
  }

}
