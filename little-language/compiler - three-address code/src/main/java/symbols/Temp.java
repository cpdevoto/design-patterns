package symbols;

import lexer.Word;

public class Temp extends Expr {

  private static int count = 0;
    
  private int number = 0;
    
  public Temp (Type p) {
    super(Word.TEMP, p);
    number = ++count;
  }
  
  public String toString () {
    return "t" + number;
  }
}
