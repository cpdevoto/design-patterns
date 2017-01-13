package symbols;

import lexer.Token;

public class Expr extends Node {
  public Token op;
  public Type type;
  
  public Expr(Token tok, Type p) {
    op = tok;
    type = p;
  }
  
  public Expr gen () {
    return this;
  }
  
  public Expr reduce () {
    return this;
  }
  
  public void jumping (int t, int f) {
    emitJumps(toString(), t, f);
  }

  public void emitJumps (String test, int t, int f) {
    if (t != 0 && f != 0) {
      emit("if " + test + " goto L" + t);
    } else if (t != 0) {
      emit("if " + test + " goto L" + t);
    } else if (f != 0) {
      emit("iffalse " + test + " goto L" + f);
    } else {
      // nothing, since both t and f fall through
    }
  }
  
  @Override
  public String toString() {
    return op.toString();
  }
}
