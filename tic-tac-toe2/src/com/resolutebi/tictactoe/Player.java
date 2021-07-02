package com.resolutebi.tictactoe;

public interface Player {

  public Symbol getSymbol();

  public String getName();

  public Square move(Board board);

}
