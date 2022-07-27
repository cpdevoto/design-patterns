package org.devoware.dicegolem.dice;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class DiceTest {

  @BeforeAll
  public static void beforeAll() {
    // To remove the random element from these tests,
    // whenever Die.roll() is called, it will always return 3!
    Die.setRandomNumberGenerator(type -> 3);
  }

  @AfterAll
  public static void afterAll() {
    // Reconfigure Die to generate random numbers again!
    Die.clearRandomNumberGenerator();
  }

  @Test
  public void end_to_end_test() {
    // This test illustrates all aspects of the little language, including:
    // 1. Support for dice roll expressions such as 3d4 or d8 (the latter is interpreted as 1d8).
    // 2. Support for positive and negative integer values such as 1, 12, or -3.
    // 2. Support for multiplication, division, addition, and subtraction.
    // 3. Multiplication and division have higher precedence than addition and subtraction
    // 4. Precedence is left to right. ==> i.e. 7 + 5 - 2 is evaluated as ((7 + 5) - 2)
    // 5. Precedence rules can be overridden with parentheses.
    // 6. All whitespace is ignored.

    // When
    int actual = Dice.roll(" (   1 +   \n  3d4)*((12- 2d6) / -   3) + d8");
    // == (1 + 3d4) * ((12 - 2d6) / -3) + d8
    // == (1 + 9) * ((12 - 6) / -3) + 3 ==> since every die is configured to roll a 3
    // == 10 * (6 / -3) + 3
    // == 10 * -2 + 3
    // == -20 + 3
    // == -17

    // Then
    assertThat(actual).isEqualTo(-17);
  }

  @Test
  public void should_roll_9_when_expression_is_9() {
    // When
    int actual = Dice.roll("9");

    // Then
    assertThat(actual).isEqualTo(9);
  }

  @Test
  public void should_roll_minus_5_when_expression_is_minus_5() {
    // When
    int actual = Dice.roll("-5");


    // Then
    assertThat(actual).isEqualTo(-5);
  }

  @Test
  public void should_roll_15_when_expression_is_1d20_multiply_by_5() {
    // When
    int actual = Dice.roll("1d20 * 5");

    // Then
    assertThat(actual).isEqualTo(15);
  }

  @Test
  public void should_roll_3_when_expression_is_2d20_divide_by_2() {
    // When
    int actual = Dice.roll("2d20 / 2");

    // Then
    assertThat(actual).isEqualTo(3);
  }


  @Test
  public void should_roll_8_when_expression_is_1d20_plus_5() {
    // When
    int actual = Dice.roll("1d20 + 5");

    // Then
    assertThat(actual).isEqualTo(8);
  }

  @Test
  public void should_roll_2_when_expression_is_1d20_minus_1() {
    // When
    int actual = Dice.roll("1d20 - 1");

    // Then
    assertThat(actual).isEqualTo(2);
  }

  @Test
  public void should_roll_13_when_expression_is_1d20_plus_2d4_minus_negative_4() {
    // When
    int actual = Dice.roll("1d20 + (2d4 - -4)");

    // Then
    assertThat(actual).isEqualTo(13);
  }

  @Test
  public void should_ignore_random_whitespace() {
    // When
    int actual = Dice.roll("1d20     +2d4       --4");

    // Then
    assertThat(actual).isEqualTo(13);
  }

}
