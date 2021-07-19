package com.resolute.search;

import static java.util.Objects.requireNonNull;

class NotExpression implements Expression {
  private final Expression expr;

  NotExpression(Expression expr) {
    this.expr = requireNonNull(expr, "expr cannot be null");
  }

  @Override
  public boolean matches(String s) {
    requireNonNull(s, "s cannot be null");
    return !expr.matches(s);
  }

}
