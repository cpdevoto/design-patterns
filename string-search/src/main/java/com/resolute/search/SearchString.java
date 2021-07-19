package com.resolute.search;

import static java.util.Objects.requireNonNull;

public class SearchString {
  private final Expression e;

  public SearchString(String s) {
    requireNonNull(s, "s cannot be null");
    this.e = Parser.parse(s);
  }

  public boolean matches(String s) {
    return e.matches(s);
  }

}
