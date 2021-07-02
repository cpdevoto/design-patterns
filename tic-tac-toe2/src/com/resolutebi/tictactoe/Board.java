package com.resolutebi.tictactoe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Board {
  private final List<Square> squares = new ArrayList<>(9);
  private final List<Row> rows;

  public Board() {
    for (int i = 0; i < 9; i++) {
      squares.add(new Square(i));
    }

    List<Row> rows = new ArrayList<>(8);
    rows.add(new Row(squares, 0, 1, 2));
    rows.add(new Row(squares, 3, 4, 5));
    rows.add(new Row(squares, 6, 7, 8));
    rows.add(new Row(squares, 0, 3, 6));
    rows.add(new Row(squares, 1, 4, 7));
    rows.add(new Row(squares, 2, 5, 8));
    rows.add(new Row(squares, 0, 4, 8));
    rows.add(new Row(squares, 2, 4, 6));
    this.rows = Collections.unmodifiableList(rows);
  }

  public Square getSquare(int row, int col) {
    if (row < 0 || row > 2) {
      throw new IllegalArgumentException("row must be between 0 and 2");
    }
    if (col < 0 || col > 2) {
      throw new IllegalArgumentException("col must be between 0 and 2");
    }
    return squares.get(row * 3 + col);
  }

  public List<Row> getRows() {
    return rows;
  }

  public String toString() {
    return squares.get(0) + "|" + squares.get(1) + "|" + squares.get(2) + "\n" +
        "-----------\n" +
        squares.get(3) + "|" + squares.get(4) + "|" + squares.get(5) + "\n" +
        "-----------\n" +
        squares.get(6) + "|" + squares.get(7) + "|" + squares.get(8) + "\n";

  }


}
