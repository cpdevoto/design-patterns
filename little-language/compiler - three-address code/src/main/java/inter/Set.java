package inter;

import symbols.Expr;
import symbols.Id;
import symbols.Type;

public class Set extends Stmt {

  public Id id;
  public Expr expr;
  
  public Set(Id i, Expr x) {
    id = i;
    expr = x;
    if (check(id.type, expr.type) == null) {
      error("type error");
    }
  }

  public Type check(Type p1, Type p2) {
    if (Type.numeric(p1) && Type.numeric(p2)) {
      return p2;
    } else if (p1 == Type.BOOL && p2 == Type.BOOL) {
      return p2;
    }
    return null;
  }
  
  @Override
  public void gen(int b, int a) {
    emit(id.toString() + " = " + expr.gen().toString());
  }

}
