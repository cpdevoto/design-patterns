package com.resolute.search;

enum TokenType {

  // @formatter:off
  OR("'or'"),
  AND("'and'"),
  NOT("'not'"),
  LEFT_PAREN("'('"),
  RIGHT_PAREN("')'"),
  WORD("a word"),
  EOS("the end of the string");
  // @formatter:on

  final String value;

  private TokenType(String value) {
    this.value = value;
  }

  String getValue() {
    return value;
  }

  public String toString() {
    return value;
  }
}
