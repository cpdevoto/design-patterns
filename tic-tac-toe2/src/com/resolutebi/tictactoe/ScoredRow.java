package com.resolutebi.tictactoe;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;

public class ScoredRow {
  private final Row row;
  private final int score;

  public ScoredRow(Row row, Symbol symbol) {
    this.row = row;

    Symbol opponentSymbol = (symbol == Symbol.X ? Symbol.O : Symbol.X);

    Map<Symbol, Long> symbolCounts = row.getSquares().stream()
        .map(Square::get)
        .collect(groupingBy(Function.identity(), counting()));

    for (Symbol s : Symbol.values()) {
      if (!symbolCounts.containsKey(s)) {
        symbolCounts.put(s, 0L);
      }
    }

    this.score = Arrays.stream(ScoringFunction.values())
        .filter(func -> func.predicate(symbolCounts, symbol, opponentSymbol))
        .map(func -> func.getScore())
        .findAny()
        .orElseThrow(() -> new AssertionError("Expected at least one matching function"));
  }

  public Row getRow() {
    return row;
  }

  public int getScore() {
    return score;
  }
}
