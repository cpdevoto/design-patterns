package inter;

import lexer.Tag;
import lexer.Word;
import symbols.Expr;
import symbols.Id;
import symbols.Op;
import symbols.Type;

public class Access extends Op {
  public Id array;
  public Expr index;

  public Access(Id a, Expr i, Type p) { // p is element type after flattening the array
    super(new Word("[]", Tag.INDEX), p);
    array = a;
    index = i;
  }
  
  @Override
  public Expr gen() {
    return new Access(array, index.reduce(), type);
  }
  
  @Override
  public void jumping(int t, int f) {
   emitJumps(reduce().toString(), t, f); 
  }
  
  @Override
  public String toString() {
    return array.toString() + " [ " + index.toString() + " ]";
  }

}
