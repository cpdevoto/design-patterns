package com.resolute.search;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

public class ParserTest {

  @Test
  public void test_simple_expression() {
    Expression e = Parser.parse("fox");

    assertThat(e.matches("The red fox raced across the road.")).isTrue();
    assertThat(e.matches("The brown fox hid under a bush.")).isTrue();
    assertThat(e.matches("The black fox snuck into the hen-house.")).isTrue();
    assertThat(e.matches("The black hen clucked and squawked.")).isFalse();

  }

  @Test
  public void test_complex_expression() {
    Expression e = Parser.parse("fox aND not\n    (brown or BLACK)");

    assertThat(e.matches("The red fox raced across the road.")).isTrue();
    assertThat(e.matches("The brown fox hid under a bush.")).isFalse();
    assertThat(e.matches("The black fox snuck into the hen-house.")).isFalse();
    assertThat(e.matches("The black hen clucked and squawked.")).isFalse();

  }

  @Test
  public void test_operator_precedence() {
    Expression e = Parser.parse("fox aND not brown or BLACK");

    assertThat(e.matches("The red fox raced across the road.")).isTrue();
    assertThat(e.matches("The brown fox hid under a bush.")).isFalse();
    assertThat(e.matches("The black fox snuck into the hen-house.")).isTrue();
    assertThat(e.matches("The black hen clucked and squawked.")).isTrue();

  }

  @Test
  public void test_syntax_exception_empty_string() {
    assertThatThrownBy(() -> {
      Parser.parse("");
    })
        .isInstanceOf(ParseException.class)
        .hasMessage("Syntax error at line 1, character 1: found the end of the string when expecting a word");


  }

  @Test
  public void test_syntax_exception_non_terminated_paren() {
    assertThatThrownBy(() -> {
      Parser.parse("fox and not\n    (brown or )");
    })
        .isInstanceOf(ParseException.class)
        .hasMessage("Syntax error at line 2, character 15: found ')' when expecting a word");

  }

  @Test
  public void test_syntax_exception_not_without_operand() {
    assertThatThrownBy(() -> {
      Parser.parse("fox and not");
    })
        .isInstanceOf(ParseException.class)
        .hasMessage("Syntax error at line 1, character 12: found the end of the string when expecting a word");

  }

  @Test
  public void test_syntax_exception_and_without_left_operand() {
    assertThatThrownBy(() -> {
      Parser.parse("and brown");
    })
        .isInstanceOf(ParseException.class)
        .hasMessage("Syntax error at line 1, character 1: found 'and' when expecting a word");

  }

  @Test
  public void test_syntax_exception_and_without_right_operand() {
    assertThatThrownBy(() -> {
      Parser.parse("fox and");
    })
        .isInstanceOf(ParseException.class)
        .hasMessage("Syntax error at line 1, character 8: found the end of the string when expecting a word");

  }

  @Test
  public void test_syntax_exception_or_without_left_operand() {
    assertThatThrownBy(() -> {
      Parser.parse("or brown");
    })
        .isInstanceOf(ParseException.class)
        .hasMessage("Syntax error at line 1, character 1: found 'or' when expecting a word");

  }

  @Test
  public void test_syntax_exception_or_without_right_operand() {
    assertThatThrownBy(() -> {
      Parser.parse("fox or");
    })
        .isInstanceOf(ParseException.class)
        .hasMessage("Syntax error at line 1, character 7: found the end of the string when expecting a word");
  }
}
