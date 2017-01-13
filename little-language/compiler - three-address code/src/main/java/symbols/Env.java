package symbols;

import java.util.HashMap;

import lexer.Token;

public class Env {
  
  protected Env prev;
  private HashMap<Token, Id> table;
  
  public Env (Env n) {
    table = new HashMap<>();
    prev = n;
  }
  
  public void put (Token w, Id i) {
    table.put(w, i);
  }
  
  public Id get (Token w) {
   for (Env e = this; e != null; e = e.prev) {
     Id found = e.table.get(w);
     if (found != null) {
       return found;
     }
   }
   return null;
  }
}
