package com.resolutebi.tictactoe;

public class Main {

  public static void main(String[] args) {
    Player player1 = new HumanPlayer("1", Symbol.X);
    Player player2 = new SimpleAiPlayer2("2", Symbol.O);

    Game game = new Game(player1, player2);
    game.play();

  }

}
