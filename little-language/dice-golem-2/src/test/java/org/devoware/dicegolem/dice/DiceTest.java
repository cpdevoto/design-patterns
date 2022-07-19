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
