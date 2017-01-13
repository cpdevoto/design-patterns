package org.devoware.simplesearch.model;

import static java.util.Objects.requireNonNull;

public class AndExpression implements Expression {
  
  private final Expression e1;
  private final Expression e2;

  public AndExpression(Expression e1, Expression e2) {
    this.e1 = requireNonNull(e1, "e1 cannot be null");
    this.e2 = requireNonNull(e2, "e2 cannot be null");
  }

  @Override
  public boolean search(String s) {
    return search(s, false); 
  }
  
  @Override
  public boolean search(String s, boolean ignoreCase) {
    requireNonNull(s, "s cannot be null");
    return e1.search(s, ignoreCase) && e2.search(s, ignoreCase); 
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((e1 == null) ? 0 : e1.hashCode());
    result = prime * result + ((e2 == null) ? 0 : e2.hashCode());
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
    AndExpression other = (AndExpression) obj;
    if (e1 == null) {
      if (other.e1 != null)
        return false;
    } else if (!e1.equals(other.e1))
      return false;
    if (e2 == null) {
      if (other.e2 != null)
        return false;
    } else if (!e2.equals(other.e2))
      return false;
    return true;
  }

    
}
