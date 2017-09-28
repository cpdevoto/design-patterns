package com.resolutebi.baseline.expr;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.Reader;

public class BaselineExpression {

  private final Expression<Double> expr;
  
  public static BaselineExpression parse (String expression) {
    Parser parser = Parser.create();
    Expression<Double> expr = parser.parse(expression);
    return new BaselineExpression(expr);
  }
  
  public static BaselineExpression parse (Reader in) throws IOException {
    Parser parser = Parser.create();
    Expression<Double> expr = parser.parse(in);
    return new BaselineExpression(expr);
  }


  private BaselineExpression(Expression<Double> expr) {
    this.expr = requireNonNull(expr, "expr cannot be null");
  }

  public double evaluate(Inputs inputs) {
    return expr.evaluate(inputs);
  }

  Expression<Double> getExpr() {
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
    BaselineExpression other = (BaselineExpression) obj;
    if (expr == null) {
      if (other.expr != null)
        return false;
    } else if (!expr.equals(other.expr))
      return false;
    return true;
  }
  
  @Override
  public String toString() {
    return expr.toString();
  }

}
