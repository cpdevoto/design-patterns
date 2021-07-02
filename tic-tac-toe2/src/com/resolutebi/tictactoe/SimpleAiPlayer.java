package com.resolutebi.tictactoe;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.function.Function;

public class SimpleAiPlayer extends AbstractPlayer {

  public SimpleAiPlayer(String name, Symbol symbol) {
    super(name, symbol);
  }

  @Override
  public Square move(Board board) {
    Row bestRow = board.getRows().stream()
        .map(row -> new ScoredRow(getSymbol(), row))
        .sorted(Comparator.comparing(ScoredRow::getScore).reversed())
        .findFirst()
        .orElseThrow(() -> new AssertionError("Expected at least one row"))
        .getRow();

    Square square = chooseSquare(bestRow);

    prompt();
    System.out.println(square.getRow() + ", " + square.getCol());
    return square;
  }

  private Square chooseSquare(Row bestRow) throws AssertionError {
    long emptySquares = bestRow.getSquares().stream()
        .filter(s -> s.get() == Symbol.EMPTY)
        .collect(counting());
    if (emptySquares == 1) {
      return bestRow.getSquares().stream()
          .filter(s -> s.get() == Symbol.EMPTY)
          .findAny()
          .orElseThrow(() -> new AssertionError("Expected at least one empty square"));
    } else if (bestRow.getSquares().get(1).get() == Symbol.EMPTY &&
        bestRow.getSquares().get(1).getRow() == 1 &&
        bestRow.getSquares().get(1).getCol() == 1) {
      // Center square is available, so choose it!
      return bestRow.getSquares().get(1);
    } else if (bestRow.getSquares().get(0).get() == Symbol.EMPTY) {
      return bestRow.getSquares().get(0);
    } else if (bestRow.getSquares().get(2).get() == Symbol.EMPTY) {
      return bestRow.getSquares().get(2);
    } else {
      return bestRow.getSquares().get(1);
    }
  }

  private static class ScoredRow {
    private final Row row;
    private final int score;

    private ScoredRow(Symbol playerSymbol, Row row) {
      this.row = row;

      Symbol opponentSymbol = playerSymbol == Symbol.X ? Symbol.O : Symbol.X;
      Map<Symbol, Long> symbolCounts = row.getSquares().stream()
          .map(Square::get)
          .collect(groupingBy(Function.identity(), counting()));
      for (Symbol symbol : Symbol.values()) {
        if (!symbolCounts.containsKey(symbol)) {
          symbolCounts.put(symbol, 0L);
        }
      }

      this.score = Arrays.stream(ScoringFunction.values())
          .filter(func -> func.predicate(symbolCounts, playerSymbol, opponentSymbol))
          .findFirst()
          .map(ScoringFunction::getScore)
          .orElse(-1);
    }

    public int getScore() {
      return score;
    }

    public Row getRow() {
      return row;
    }

  }

  private static enum ScoringFunction {
    NO_MOVE_AVAILABLE(-1) {

      @Override
      public boolean predicate(Map<Symbol, Long> symbolCounts, Symbol playerSymbol,
          Symbol opponentSymbol) {
        return symbolCounts.get(Symbol.EMPTY) == 0;
      }

    },
    MARKING_MOVE(0) {
      @Override
      public boolean predicate(Map<Symbol, Long> symbolCounts, Symbol playerSymbol,
          Symbol opponentSymbol) {
        return symbolCounts.get(playerSymbol) == 1 && symbolCounts.get(opponentSymbol) == 1;
      }
    },
    BUILDING_MOVE1(1) {
      @Override
      public boolean predicate(Map<Symbol, Long> symbolCounts, Symbol playerSymbol,
          Symbol opponentSymbol) {
        return symbolCounts.get(playerSymbol) == 0 && symbolCounts.get(opponentSymbol) == 1;
      }
    },
    BUILDING_MOVE2(2) {
      @Override
      public boolean predicate(Map<Symbol, Long> symbolCounts, Symbol playerSymbol,
          Symbol opponentSymbol) {
        return symbolCounts.get(playerSymbol) == 0 && symbolCounts.get(opponentSymbol) == 0;
      }
    },
    BUILDING_MOVE3(3) {
      @Override
      public boolean predicate(Map<Symbol, Long> symbolCounts, Symbol playerSymbol,
          Symbol opponentSymbol) {
        return symbolCounts.get(opponentSymbol) == 0 && symbolCounts.get(playerSymbol) == 1;
      }
    },
    BLOCKING_MOVE(4) {

      @Override
      public boolean predicate(Map<Symbol, Long> symbolCounts, Symbol playerSymbol,
          Symbol opponentSymbol) {
        return symbolCounts.get(opponentSymbol) == 2 && symbolCounts.get(Symbol.EMPTY) == 1;
      }

    },
    WINNING_MOVE(5) {
      @Override
      public boolean predicate(Map<Symbol, Long> symbolCounts, Symbol playerSymbol,
          Symbol opponentSymbol) {
        return symbolCounts.get(playerSymbol) == 2 && symbolCounts.get(Symbol.EMPTY) == 1;
      }
    };

    private int score;

    private ScoringFunction(int score) {
      this.score = score;
    }

    public int getScore() {
      return score;
    }

    public abstract boolean predicate(Map<Symbol, Long> symbolCounts, Symbol playerSymbol,
        Symbol opponentSymbol);

  }

}
