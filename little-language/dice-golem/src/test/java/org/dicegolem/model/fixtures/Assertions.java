package org.dicegolem.model.fixtures;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.util.function.Supplier;

import org.dicegolem.Dice;
import org.dicegolem.SyntaxException;
import org.dicegolem.model.Die;

public class Assertions {

  public static void assertSyntaxError(String expression, int line, int pos,
      String expectedMessageFragment) {
    try {
      Dice.parse(expression);
      fail("Expected a SyntaxException");
    } catch (SyntaxException e) {
      String actualMessage = e.getMessage();
      assertThat(actualMessage.indexOf(expectedMessageFragment)).isNotEqualTo(-1)
          .as("Expected an error message containing the substring \"" + expectedMessageFragment
              + "\" but found the following error message instead: " + actualMessage);
      String positionString = "at line " + line + ", character " + pos;
      assertThat(actualMessage.indexOf(positionString)).isNotEqualTo(-1)
          .as("Expected an error message containing the substring \"%s\" but found the following error message instead: %s",
              positionString, actualMessage);
    }

  }

  public static void assertRollRange(Supplier<Integer> supplier, int expectedMin, int expectedMax) {
    int actualMin = Integer.MAX_VALUE;
    int actualMax = Integer.MIN_VALUE;
    for (int i = 0; i < 10000000; i++) {
      int roll = supplier.get();
      if (roll < actualMin) {
        actualMin = roll;
      }
      if (roll > actualMax) {
        actualMax = roll;
      }
    }
    assertThat(actualMin).isEqualTo(expectedMin)
        .as("Expected a minimum value of %d but got a minimum value of %d", expectedMin, actualMin);
    assertThat(actualMax).isEqualTo(expectedMax)
        .as("Expected a maximum value of %d but got a maximum value of %d", expectedMax, actualMax);
  }

  public static void assertDieRange(Die die) {
    int actualMin = Integer.MAX_VALUE;
    int actualMax = Integer.MIN_VALUE;
    for (int i = 0; i < 10000000; i++) {
      int roll = die.roll();
      if (roll < actualMin) {
        actualMin = roll;
      }
      if (roll > actualMax) {
        actualMax = roll;
      }
    }
    assertThat(actualMin).isEqualTo(1)
        .as("Invalid range for %s: expected a minimum value of 1 but got a minimum value of %d",
            die, actualMin);
    assertThat(actualMax).isEqualTo(die.getType())
        .as("Invalid range for %s: expected a maximum value of %d but got a minimum value of %d",
            die, die.getType(), actualMax);
  }


  private Assertions() {}
}
