package inter;

import lexer.Num;
import lexer.Token;
import lexer.Word;
import symbols.Expr;
import symbols.Type;

public class Constant extends Expr {

  public static final Constant TRUE = new Constant(Word.TRUE, Type.BOOL);
  public static final Constant FALSE = new Constant(Word.FALSE, Type.BOOL);
  
  public Constant(Token tok, Type p) {
    super(tok, p);
  }
  
  public Constant(int i) {
    super(new Num(i), Type.INT);
  }
  
  public void jumping (int t, int f) {
    if (this == TRUE && t != 0) {
      emit("goto L" + t);
    } else if (this == FALSE && f != 0) {
      emit("goto L" + f);
    }  
  }

}
