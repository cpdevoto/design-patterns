package com.resolutebi.tictactoe;

import static java.util.Objects.requireNonNull;

public class Game {
  private final Player[] players = new Player[2];
  private final Board board = new Board();

  private int moveCount = 0;

  public Game(Player player1, Player player2) {
    players[0] = requireNonNull(player1, "player1 cannot be null");
    players[1] = requireNonNull(player2, "player2 cannot be null");
  }

  public void play() {
    do {
      Player currentPlayer = players[moveCount++ % 2];
      Square square = currentPlayer.move(board);
      square.set(currentPlayer.getSymbol());
      System.out.println("\n" + board + "\n");
    } while (!gameOver());
  }

  private boolean gameOver() {
    return playerWins(players[0]) || playerWins(players[1]) || catsGame();
  }

  private boolean playerWins(Player player) {
    Symbol symbol = player.getSymbol();
    for (Row row : board.getRows()) {
      if (!row.getSquares().stream()
          .filter(s -> symbol != s.get())
          .findAny()
          .isPresent()) {
        System.out.println("\nPlayer " + player.getName() + " wins!!");
        return true;

      }
    }
    return false;
  }

  private boolean catsGame() {
    for (int row = 0; row < 3; row++) {
      for (int col = 0; col < 3; col++) {
        if (board.getSquare(row, col).get() == Symbol.EMPTY) {
          return false;
        }
      }
    }
    System.out.println("\nCat's game!!");

    return true;
  }
}
