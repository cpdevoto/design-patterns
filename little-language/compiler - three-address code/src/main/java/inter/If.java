package inter;

import symbols.Expr;
import symbols.Type;

public class If extends Stmt {
  private Expr expr;
  private Stmt stmt;

  public If(Expr x, Stmt s) {
    expr = x;
    stmt = s;
    if (expr.type != Type.BOOL) {
      expr.error("boolean required in if");
    }
  }
  
  @Override
  public void gen(int b, int a) {
    int label = newLabel(); // label for the code for stmt
    expr.jumping(0, a);     // fall through on true, goto a on false
    emitLabel(label);
    stmt.gen(label, a);
  }
  
  

}
