package org.dicegolem.model.fixtures;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.function.Supplier;

import org.dicegolem.model.Die;
import org.dicegolem.parser.Dice;
import org.dicegolem.parser.SyntaxException;
import org.junit.Assert;

public class Assertions {

  public static void assertSyntaxError(String expression, int line, int pos,
      String expectedMessageFragment) {
    try {
      Dice.parse(expression);
      Assert.fail("Expected a SyntaxException");
    } catch (SyntaxException e) {
      String message = e.getMessage();
      assertThat(
          "Expected an error message containing the substring \"" + expectedMessageFragment
              + "\" but found the following error message instead: " + message,
          message.indexOf(expectedMessageFragment) != -1, equalTo(true));
      String positionString = "at line " + line + ", character " + pos;
      assertThat(
          "Expected an error message containing the substring \"" + positionString
              + "\" but found the following error message instead: " + message,
          message.indexOf(positionString) != -1, equalTo(true));
    }

  }

  public static void assertRollRange(Supplier<Integer> supplier, int minValue, int maxValue) {
    int minRoll = Integer.MAX_VALUE;
    int maxRoll = Integer.MIN_VALUE;
    for (int i = 0; i < 10000000; i++) {
      int roll = supplier.get();
      if (roll < minRoll) {
        minRoll = roll;
      }
      if (roll > maxRoll) {
        maxRoll = roll;
      }
    }
    assertThat("Expected a minimum value of " + minValue + " but got a minimum value of " + minRoll,
        minRoll, equalTo(minValue));
    assertThat("Expected a maximum value of " + maxValue + " but got a maximum value of " + maxRoll,
        maxRoll, equalTo(maxValue));
  }

  public static void assertDieRange(Die die) {
    int minRoll = Integer.MAX_VALUE;
    int maxRoll = Integer.MIN_VALUE;
    for (int i = 0; i < 10000000; i++) {
      int roll = die.roll();
      if (roll < minRoll) {
        minRoll = roll;
      }
      if (roll > maxRoll) {
        maxRoll = roll;
      }
    }
    assertThat("Invalid range for " + die
        + ": expected a minimum value of 1 but got a minimum value of " + minRoll, minRoll,
        equalTo(1));
    assertThat(
        "Invalid range for " + die + ": expected a maximum value of " + die.getType()
            + " but got a maximum value of " + maxRoll,
        maxRoll, equalTo(die.getType()));
  }


  private Assertions() {}
}
