package com.resolutebi.tictactoe;

import static java.util.Objects.requireNonNull;

public abstract class AbstractPlayer implements Player {

  private final String name;
  private final Symbol symbol;

  public AbstractPlayer(String name, Symbol symbol) {
    this.name = requireNonNull(name, "name cannot be null");
    this.symbol = requireNonNull(symbol, "symbol cannot be null");
    if (this.symbol == Symbol.EMPTY) {

    }
  }

  @Override
  public final Symbol getSymbol() {
    return symbol;
  }

  @Override
  public final String getName() {
    return name;
  }

  protected void prompt() {
    System.out.print("Player " + name + " move - row, col: ");
  }
}
