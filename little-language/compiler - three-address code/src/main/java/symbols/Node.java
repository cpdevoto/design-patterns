package symbols;

import lexer.Lexer;

public class Node {
  private static int labels = 0;
  private int lexline = 0;
  
  public Node () {
    this.lexline = Lexer.line;
  }
  
  public void error (String s) {
    throw new Error("near line " + lexline + ": " + s);
  } 

  public int newLabel () {
    return ++labels;
  }
  
  public void emitLabel (int i) {
    System.out.print("L" + i + ":");
  }
  
  public void emit (String s) {
    System.out.println("\t" + s);
  }
}
