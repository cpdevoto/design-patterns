package symbols;

import lexer.Token;

public class Unary extends Expr {
  
  public Expr expr;
  
  public Unary (Token tok, Expr x) { // handles minus, for ! see Not
    super(tok, null);
    expr = x;
    type = Type.max(Type.INT, expr.type);
    if (type == null) {
      error("type error");
    }
  }
  
  public Expr gen () {
    return new Unary(op, expr.reduce());
  }
  
  @Override
  public String toString() {
    return op.toString() + " " + expr.toString();
  }

}
