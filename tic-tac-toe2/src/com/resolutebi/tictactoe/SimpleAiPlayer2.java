package com.resolutebi.tictactoe;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.counting;

import java.util.ArrayList;
import java.util.List;

public class SimpleAiPlayer2 extends AbstractPlayer {

  public SimpleAiPlayer2(String name, Symbol symbol) {
    super(name, symbol);
  }

  @Override
  public Square move(Board board) {

    List<Row> rows = new ArrayList<>(board.getRows());
    // Collections.shuffle(rows);

    Row bestRow = rows.stream()
        .map(r -> new ScoredRow(r, getSymbol()))
        .sorted(comparing(ScoredRow::getScore).reversed())
        .findFirst()
        .orElseThrow(() -> new AssertionError("Expected at least one row"))
        .getRow();

    Square square = chooseSquare(bestRow);

    prompt();
    System.out.println(square.getRow() + ", " + square.getCol());
    return square;
  }

  private Square chooseSquare(Row bestRow) {
    if (bestRow.getSquares().stream()
        .map(Square::get)
        .filter(s -> s == Symbol.EMPTY)
        .collect(counting()) == 1) {
      return bestRow.getSquares().stream()
          .filter(s -> s.get() == Symbol.EMPTY)
          .findFirst()
          .orElseThrow(() -> new AssertionError("Expected at least one empty square"));
    } else if (bestRow.getSquares().get(1).getRow() == 1 &&
        bestRow.getSquares().get(1).getCol() == 1 &&
        bestRow.getSquares().get(1).get() == Symbol.EMPTY) {
      return bestRow.getSquares().get(1);
    } else if (bestRow.getSquares().get(0).get() == Symbol.EMPTY) {
      return bestRow.getSquares().get(0);
    } else if (bestRow.getSquares().get(2).get() == Symbol.EMPTY) {
      return bestRow.getSquares().get(2);
    }
    return bestRow.getSquares().get(1);
  }



}
