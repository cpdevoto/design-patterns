package inter;

import symbols.Expr;
import symbols.Type;

public class While extends Stmt {

  private Expr expr;
  private Stmt stmt;
  
  public While() {}
  
  public void init (Expr x, Stmt s) {
    expr = x;
    stmt = s;
    if (expr.type != Type.BOOL) {
      expr.error("boolean required in while");
    }
  }
  
  @Override
  public void gen(int b, int a) {
    after = a; // save label a
    expr.jumping(0, a);
    int label = newLabel();
    emitLabel(label);
    stmt.gen(label, b);
    emit("goto L" + b);
  }

}
