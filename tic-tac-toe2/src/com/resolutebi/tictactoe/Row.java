package com.resolutebi.tictactoe;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class Row {

  private final List<Square> squares;

  public Row(List<Square> boardSquares, int square1, int square2, int square3) {
    requireNonNull(boardSquares, "boardSquares cannot be null");
    if (square1 < 0 || square1 > 8) {
      throw new IllegalArgumentException("square1 must be between 0 and 8");
    }
    if (square2 < 0 || square2 > 8) {
      throw new IllegalArgumentException("square2 must be between 0 and 8");
    }
    if (square3 < 0 || square3 > 8) {
      throw new IllegalArgumentException("square1 must be between 0 and 8");
    }
    if (square1 == square2 || square1 == square3 || square2 == square3) {
      throw new IllegalArgumentException("The three squares must be different");
    }
    List<Square> squares = new ArrayList<>(3);
    squares.add(boardSquares.get(square1));
    squares.add(boardSquares.get(square2));
    squares.add(boardSquares.get(square3));
    this.squares = Collections.unmodifiableList(squares);

  }

  public List<Square> getSquares() {
    return squares;
  }

}
