package inter;

import symbols.Node;

public class Stmt extends Node {
  
  public static final Stmt NULL = new Stmt();
  public static Stmt Enclosing = Stmt.NULL; // used for break stmts
  
  protected int after = 0; // saves label after
  
  public Stmt() {}
  
  public void gen(int b, int a) {} // called with begin and after

}
