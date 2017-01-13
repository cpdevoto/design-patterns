package org.devoware.simplesearch.model;

import static java.util.Objects.requireNonNull;

public class WordExpression implements Expression {
  
  private final String word;

  public WordExpression(String word) {
    requireNonNull(word, "word cannot be null");
    if (word.isEmpty()) {
      throw new IllegalArgumentException("word cannot be empty");
    }
    this.word = word;
    
  }

  @Override
  public boolean search(String s) {
    return search(s, false);  }
  
  @Override
  public boolean search(String s, boolean ignoreCase) {
    requireNonNull(s, "s cannot be null");
    String stringToSearch = s;
    String word = this.word;
    if (ignoreCase) {
      stringToSearch = s.toLowerCase();
      word = word.toLowerCase();
    }
    if (stringToSearch.indexOf(word) >= 0) {
      return true;
    }
    return false;
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((word == null) ? 0 : word.hashCode());
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
    WordExpression other = (WordExpression) obj;
    if (word == null) {
      if (other.word != null)
        return false;
    } else if (!word.equals(other.word))
      return false;
    return true;
  }

}
