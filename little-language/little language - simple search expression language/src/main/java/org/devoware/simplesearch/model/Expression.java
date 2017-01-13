package org.devoware.simplesearch.model;

public interface Expression {
  
  public boolean search (String s);

  public boolean search (String s, boolean ignoreCase);

}
