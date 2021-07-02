package com.resolutebi.tictactoe;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HumanPlayer extends AbstractPlayer {
  private static final Pattern DELIM = Pattern.compile("\n");
  private static final Pattern COORDS = Pattern.compile("^\\s*([0-2])\\s*,\\s*([0-2])\\s*$");

  private final Scanner scanner;

  public HumanPlayer(String name, Symbol symbol) {
    super(name, symbol);
    scanner = new Scanner(System.in);
    scanner.useDelimiter(DELIM);
  }

  @Override
  public Square move(Board board) {
    while (true) {
      prompt();
      String input = scanner.next();
      Matcher matcher = COORDS.matcher(input);
      if (matcher.find()) {
        int row = Integer.parseInt(matcher.group(1));
        int col = Integer.parseInt(matcher.group(2));
        Square square = board.getSquare(row, col);
        if (square.get() == Symbol.EMPTY) {
          return square;
        }
      }
      System.out.println("\nIllegal move!\n");
    }
  }

}
