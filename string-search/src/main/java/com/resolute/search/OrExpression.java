package com.resolute.search;

import static java.util.Objects.requireNonNull;

class OrExpression implements Expression {
  private final Expression left;
  private final Expression right;

  OrExpression(Expression left, Expression right) {
    this.left = requireNonNull(left, "left cannot be null");
    this.right = requireNonNull(right, "right cannot be null");
  }

  @Override
  public boolean matches(String s) {
    requireNonNull(s, "s cannot be null");
    return left.matches(s) || right.matches(s);
  }

}
