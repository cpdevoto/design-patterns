package com.resolutebi.tictactoe;

public class Square {

  private int index;
  private Symbol symbol;

  public Square(int index) {
    if (index < 0 || index > 8) {
      throw new IllegalArgumentException("index must be between 0 and 8");
    }
    this.index = index;
    this.symbol = Symbol.EMPTY;
  }

  public int getRow() {
    return index / 3;
  }

  public int getCol() {
    return index % 3;
  }

  public Symbol get() {
    return symbol;
  }

  public void set(Symbol symbol) {
    if (symbol == null) {
      throw new NullPointerException("symbol cannot be null");
    }
    this.symbol = symbol;
  }

  @Override
  public String toString() {
    return " " + symbol + " ";
  }

}
