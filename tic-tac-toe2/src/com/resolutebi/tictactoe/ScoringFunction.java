package com.resolutebi.tictactoe;

import java.util.Map;

public enum ScoringFunction {
  NO_MOVE(0) {

    @Override
    public boolean predicate(Map<Symbol, Long> symbolCounts, Symbol symbol, Symbol opponentSymbol) {
      return symbolCounts.get(Symbol.EMPTY) == 0;
    }

  },
  MARKING_MOVE(1) {

    @Override
    public boolean predicate(Map<Symbol, Long> symbolCounts, Symbol symbol, Symbol opponentSymbol) {
      return symbolCounts.get(symbol) == 1 && symbolCounts.get(opponentSymbol) == 1;
    }

  },
  BUILDING_MOVE1(2) {

    @Override
    public boolean predicate(Map<Symbol, Long> symbolCounts, Symbol symbol, Symbol opponentSymbol) {
      return symbolCounts.get(symbol) == 0 && symbolCounts.get(opponentSymbol) == 1;
    }

  },
  BUILDING_MOVE2(3) {

    @Override
    public boolean predicate(Map<Symbol, Long> symbolCounts, Symbol symbol, Symbol opponentSymbol) {
      return symbolCounts.get(symbol) == 0 && symbolCounts.get(opponentSymbol) == 0;
    }

  },
  BUILDING_MOVE3(4) {

    @Override
    public boolean predicate(Map<Symbol, Long> symbolCounts, Symbol symbol, Symbol opponentSymbol) {
      return symbolCounts.get(symbol) == 1 && symbolCounts.get(opponentSymbol) == 0;
    }

  },
  BLOCKING_MOVE(5) {

    @Override
    public boolean predicate(Map<Symbol, Long> symbolCounts, Symbol symbol, Symbol opponentSymbol) {
      return symbolCounts.get(symbol) == 0 && symbolCounts.get(opponentSymbol) == 2;
    }

  },
  WINNING_MOVE(6) {

    @Override
    public boolean predicate(Map<Symbol, Long> symbolCounts, Symbol symbol, Symbol opponentSymbol) {
      return symbolCounts.get(symbol) == 2 && symbolCounts.get(opponentSymbol) == 0;
    }

  },
  ;

  private final int score;

  private ScoringFunction(int score) {
    this.score = score;
  }

  public int getScore() {
    return score;
  }

  public abstract boolean predicate(Map<Symbol, Long> symbolCounts, Symbol symbol,
      Symbol opponentSymbol);
}
