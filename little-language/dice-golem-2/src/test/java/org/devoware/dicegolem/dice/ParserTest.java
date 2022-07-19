package org.devoware.dicegolem.dice;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.devoware.dicegolem.dice.Die.D4;
import static org.devoware.dicegolem.dice.Die.D6;
import static org.devoware.dicegolem.dice.Token.Type.DIE;
import static org.devoware.dicegolem.dice.Token.Type.EOS;
import static org.devoware.dicegolem.dice.Token.Type.LEFT_PAREN;
import static org.devoware.dicegolem.dice.Token.Type.NUMBER;
import static org.devoware.dicegolem.dice.Token.Type.PLUS;
import static org.devoware.dicegolem.dice.Token.Type.RIGHT_PAREN;

import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;

public class ParserTest {

  @Test
  public void should_parse_full_dice_expression() {
    // When
    Expression actual = Parser.parse("2d4");

    // Then
    Expression expected = new DiceExpression(2, D4);
    assertThat(actual).isEqualTo(expected);

  }

  @Test
  public void should_parse_dice_expression_with_missing_num_dice() {
    // When
    Expression actual = Parser.parse("d6");

    // Then
    Expression expected = new DiceExpression(1, D6);
    assertThat(actual).isEqualTo(expected);

  }

  @Test
  public void should_parse_value_expression() {
    // When
    Expression actual = Parser.parse("8");

    // Then
    Expression expected = new ValueExpression(8);
    assertThat(actual).isEqualTo(expected);

  }

  @Test
  public void should_parse_unary_minus_expression() {
    // When
    Expression actual = Parser.parse("-12");

    // Then
    Expression expected = new UnaryMinusExpression(new ValueExpression(12));
    assertThat(actual).isEqualTo(expected);

  }

  @Test
  public void should_parse_plus_expression() {
    // When
    Expression actual = Parser.parse("7 + 20");

    // Then
    Expression expected = new PlusExpression(new ValueExpression(7), new ValueExpression(20));
    assertThat(actual).isEqualTo(expected);

  }

  @Test
  public void should_parse_minus_expression() {
    // When
    Expression actual = Parser.parse("17 - 4");

    // Then
    Expression expected = new MinusExpression(new ValueExpression(17), new ValueExpression(4));
    assertThat(actual).isEqualTo(expected);

  }

  @Test
  public void default_precedence_is_left_to_right() {
    // When
    Expression actual = Parser.parse("17 + 9 - 4 + 1");

    // Then -> expect the result to equal (((17 + 9) - 4) + 1)
    Expression expected = new PlusExpression(
        new MinusExpression(
            new PlusExpression(new ValueExpression(17), new ValueExpression(9)),
            new ValueExpression(4)),
        new ValueExpression(1));
    assertThat(actual).isEqualTo(expected);

  }

  /**
   * NOTE: Parentheses do not impact the result of dice rolls currently, but may in future, if
   * decide to introduce mathematical operators with a higher precedence than addition and
   * subtraction (e.g. multiplication and division).
   */
  @Test
  public void parentheses_can_override_default_precedence() {
    // When
    Expression actual = Parser.parse("17 + (9 - (4 + 1))");

    // Then -> expect the result to equal (17 + (9 - (4 + 1)))
    Expression expected = new PlusExpression(
        new ValueExpression(17),
        new MinusExpression(new ValueExpression(9),
            new PlusExpression(new ValueExpression(4), new ValueExpression(1))));
    assertThat(actual).isEqualTo(expected);

  }

  @Test
  public void should_throw_unexpected_token_exception_when_expression_starts_with_plus() {
    // When
    ThrowingCallable statement = () -> Parser.parse(" + 2d4");

    // Then
    assertThatThrownBy(statement)
        .isInstanceOfSatisfying(UnexpectedTokenException.class, ex -> {
          assertThat(ex.getToken().getType()).isEqualTo(PLUS);
          assertThat(ex.getExpectedType()).isEqualTo(NUMBER);
        });

  }

  @Test
  public void should_throw_unexpected_token_exception_when_expression_starts_with_right_paren() {
    // When
    ThrowingCallable statement = () -> Parser.parse(" ) 2d4");

    // Then
    assertThatThrownBy(statement)
        .isInstanceOfSatisfying(UnexpectedTokenException.class, ex -> {
          assertThat(ex.getToken().getType()).isEqualTo(RIGHT_PAREN);
          assertThat(ex.getExpectedType()).isEqualTo(NUMBER);
        });

  }

  @Test
  public void should_throw_unexpected_token_exception_when_unary_minus_not_followed_by_number() {
    // When
    ThrowingCallable statement = () -> Parser.parse(" (- ) + 7 ");

    // Then
    assertThatThrownBy(statement)
        .isInstanceOfSatisfying(UnexpectedTokenException.class, ex -> {
          assertThat(ex.getToken().getType()).isEqualTo(RIGHT_PAREN);
          assertThat(ex.getExpectedType()).isEqualTo(NUMBER);
        });

    // When
    statement = () -> Parser.parse(" - (4 + 2d8) ");

    // Then
    assertThatThrownBy(statement)
        .isInstanceOfSatisfying(UnexpectedTokenException.class, ex -> {
          assertThat(ex.getToken().getType()).isEqualTo(LEFT_PAREN);
          assertThat(ex.getExpectedType()).isEqualTo(NUMBER);
        });
  }

  @Test
  public void should_throw_unexpected_token_exception_when_unary_minus_followed_by_die_roll_expression() {
    // When
    ThrowingCallable statement = () -> Parser.parse(" - 2d8");

    // Then
    assertThatThrownBy(statement)
        .isInstanceOfSatisfying(UnexpectedTokenException.class, ex -> {
          assertThat(ex.getToken().getType()).isEqualTo(DIE);
          assertThat(ex.getExpectedType()).isEqualTo(NUMBER);
        });

  }

  @Test
  public void should_throw_unexpected_token_exception_when_parentheses_not_terminated() {
    // When
    ThrowingCallable statement = () -> Parser.parse(" 4 + (2d8 - 4");

    // Then
    assertThatThrownBy(statement)
        .isInstanceOfSatisfying(UnexpectedTokenException.class, ex -> {
          assertThat(ex.getToken().getType()).isEqualTo(EOS);
          assertThat(ex.getExpectedType()).isEqualTo(RIGHT_PAREN);
        });

  }

  @Test
  public void should_throw_unexpected_token_exception_when_nested_parentheses_not_terminated() {
    // When
    ThrowingCallable statement = () -> Parser.parse(" 4 + (2d8 - 4 + (1d100 + 7)");

    // Then
    assertThatThrownBy(statement)
        .isInstanceOfSatisfying(UnexpectedTokenException.class, ex -> {
          assertThat(ex.getToken().getType()).isEqualTo(EOS);
          assertThat(ex.getExpectedType()).isEqualTo(RIGHT_PAREN);
        });

  }

  @Test
  public void should_throw_invalid_die_type_exception_when_die_type_is_not_recognized() {
    // When
    ThrowingCallable statement = () -> Parser.parse("2d13");

    // Then
    assertThatThrownBy(statement)
        .isInstanceOfSatisfying(InvalidDieTypeException.class, ex -> {
          assertThat(ex.getToken())
              .isInstanceOfSatisfying(NumberToken.class, tok -> {
                assertThat(tok.getValue()).isEqualTo(13);
              });
          assertThat(ex.getDieType()).isEqualTo(13);
        });

  }
}
