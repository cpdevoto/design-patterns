package org.devoware.simplesearch.model;

import static java.util.Objects.requireNonNull;

public class NotExpression implements Expression {
  
  private final Expression e;

  public NotExpression(Expression e) {
    this.e = requireNonNull(e, "e cannot be null");
    
  }

  @Override
  public boolean search(String s) {
    return search(s, false); 
  }
  
  @Override
  public boolean search(String s, boolean ignoreCase) {
    requireNonNull(s, "s cannot be null");
    return !e.search(s, ignoreCase); 
  }
  

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((e == null) ? 0 : e.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    NotExpression other = (NotExpression) obj;
    if (e == null) {
      if (other.e != null)
        return false;
    } else if (!e.equals(other.e))
      return false;
    return true;
  }
  
}
