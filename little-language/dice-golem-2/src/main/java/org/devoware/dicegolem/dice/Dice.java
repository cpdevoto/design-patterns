package org.devoware.dicegolem.dice;

import static java.util.Objects.requireNonNull;

public class Dice {

  public static int roll(String expression) {
    requireNonNull(expression, "expression cannot be null");
    Expression e = Parser.parse(expression);
    return e.roll();
  }

  private Dice() {}


}
