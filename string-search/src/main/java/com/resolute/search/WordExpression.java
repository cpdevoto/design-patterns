package com.resolute.search;

import static java.util.Objects.requireNonNull;

class WordExpression implements Expression {
  private final String word;

  WordExpression(String word) {
    this.word = requireNonNull(word, "word cannot be null");
  }

  @Override
  public boolean matches(String s) {
    requireNonNull(s, "s cannot be null");
    return s.toLowerCase().contains(word.toLowerCase());
  }

}
