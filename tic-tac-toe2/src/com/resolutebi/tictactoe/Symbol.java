package com.resolutebi.tictactoe;

public enum Symbol {
  X("X"), O("O"), EMPTY(" ");

  private final String value;

  private Symbol(String value) {
    this.value = value;
  }

  public String toString() {
    return value;
  }

}
