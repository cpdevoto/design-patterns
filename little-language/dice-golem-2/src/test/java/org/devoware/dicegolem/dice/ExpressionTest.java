package org.devoware.dicegolem.dice;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ExpressionTest {

  @BeforeAll
  public static void beforeAll() {
    // To remove the random element from these tests,
    // whenever Die.roll() is called, it will always return 3!
    Die.setRandomNumberGenerator(type -> 3);
  }

  @AfterAll
  public static void afterAll() {
    // Reconfigure Die to roll random numbers again!
    Die.clearRandomNumberGenerator();
  }

  @Test
  public void should_roll_9_when_expression_is_9() {
    // Given
    Expression expression = new ValueExpression(9);

    // When
    int actual = expression.roll();

    // Then
    assertThat(actual).isEqualTo(9);
  }

  @Test
  public void should_roll_minus_5_when_expression_is_minus_5() {
    // Given
    Expression expression = new UnaryMinusExpression(new ValueExpression(5));

    // When
    int actual = expression.roll();

    // Then
    assertThat(actual).isEqualTo(-5);
  }

  @Test
  public void should_roll_15_when_expression_is_1d20_multiply_by_5() {
    // Given
    Expression expression = new MultiplyExpression(
        new DiceExpression(1, Die.D20),
        new ValueExpression(5));

    // When
    int actual = expression.roll();

    // Then
    assertThat(actual).isEqualTo(15);
  }

  @Test
  public void should_roll_3_when_expression_is_2d20_divide_by_2() {
    // Given
    Expression expression = new DivideExpression(
        new DiceExpression(2, Die.D20),
        new ValueExpression(2));

    // When
    int actual = expression.roll();

    // Then
    assertThat(actual).isEqualTo(3);
  }

  @Test
  public void should_roll_8_when_expression_is_1d20_plus_5() {
    // Given
    Expression expression = new PlusExpression(
        new DiceExpression(1, Die.D20),
        new ValueExpression(5));

    // When
    int actual = expression.roll();

    // Then
    assertThat(actual).isEqualTo(8);
  }

  @Test
  public void should_roll_2_when_expression_is_1d20_minus_1() {
    // Given
    Expression expression = new MinusExpression(
        new DiceExpression(1, Die.D20),
        new ValueExpression(1));

    // When
    int actual = expression.roll();

    // Then
    assertThat(actual).isEqualTo(2);
  }

  @Test
  public void should_roll_13_when_expression_is_1d20_plus_2d4_minus_negative_4() {
    // Given
    Expression expression = new PlusExpression(
        new DiceExpression(1, Die.D20),
        new MinusExpression(
            new DiceExpression(2, Die.D4),
            new UnaryMinusExpression(new ValueExpression(4))));

    // When
    int actual = expression.roll();

    // Then
    assertThat(actual).isEqualTo(13);
  }
}
