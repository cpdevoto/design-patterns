package inter;

public class Break extends Stmt {

  private Stmt stmt;
  
  public Break() {
    if (Stmt.Enclosing == Stmt.NULL) {
      error("unenclosed break");
    }
    stmt = Stmt.Enclosing;
  }
  
  @Override
  public void gen(int b, int a) {
    emit("goto L" + stmt.after);
  }

}
