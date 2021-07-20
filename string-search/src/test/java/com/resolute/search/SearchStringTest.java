package com.resolute.search;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

public class SearchStringTest {

  @Test
  public void test_simple_expression() {
    SearchString s = new SearchString("fox");

    assertThat(s.matches("The red fox raced across the road.")).isTrue();
    assertThat(s.matches("The brown fox hid under a bush.")).isTrue();
    assertThat(s.matches("The black fox snuck into the hen-house.")).isTrue();
    assertThat(s.matches("The black hen clucked and squawked.")).isFalse();

  }

  @Test
  public void test_complex_expression() {
    SearchString s = new SearchString("fox aND not (brown or BLACK)");

    assertThat(s.matches("The red fox raced across the road.")).isTrue();
    assertThat(s.matches("The brown fox hid under a bush.")).isFalse();
    assertThat(s.matches("The black fox snuck into the hen-house.")).isFalse();
    assertThat(s.matches("The red hen clucked and squawked.")).isFalse();

  }
  
  @Test
  public void test_operator_precedence() {
    SearchString s = new SearchString("fox aND not brown or BLACK");

    assertThat(s.matches("The red fox raced across the road.")).isTrue();
    assertThat(s.matches("The brown fox hid under a bush.")).isFalse();
    assertThat(s.matches("The black fox snuck into the hen-house.")).isTrue();
    assertThat(s.matches("The black hen clucked and squawked.")).isTrue();

  }
  

  @Test
  public void test_syntax_exception_empty_string() {
    assertThatThrownBy(() -> {
      new SearchString("");
    })
        .isInstanceOf(ParseException.class)
        .hasMessage("Syntax error at line 1, character 1: found the end of the string when expecting a word");


  }

  @Test
  public void test_syntax_exception_non_terminated_paren() {
    assertThatThrownBy(() -> {
      new SearchString("fox and not\n    (brown or )");
    })
        .isInstanceOf(ParseException.class)
        .hasMessage("Syntax error at line 2, character 15: found ')' when expecting a word");

  }

  @Test
  public void test_syntax_exception_not_without_operand() {
    assertThatThrownBy(() -> {
      new SearchString("fox and not");
    })
        .isInstanceOf(ParseException.class)
        .hasMessage("Syntax error at line 1, character 12: found the end of the string when expecting a word");

  }

  @Test
  public void test_syntax_exception_and_without_left_operand() {
    assertThatThrownBy(() -> {
      new SearchString("and brown");
    })
        .isInstanceOf(ParseException.class)
        .hasMessage("Syntax error at line 1, character 1: found 'and' when expecting a word");

  }

  @Test
  public void test_syntax_exception_and_without_right_operand() {
    assertThatThrownBy(() -> {
      new SearchString("fox and");
    })
        .isInstanceOf(ParseException.class)
        .hasMessage("Syntax error at line 1, character 8: found the end of the string when expecting a word");

  }

  @Test
  public void test_syntax_exception_or_without_left_operand() {
    assertThatThrownBy(() -> {
      new SearchString("or brown");
    })
        .isInstanceOf(ParseException.class)
        .hasMessage("Syntax error at line 1, character 1: found 'or' when expecting a word");

  }

  @Test
  public void test_syntax_exception_or_without_right_operand() {
    assertThatThrownBy(() -> {
      new SearchString("fox or");
    })
        .isInstanceOf(ParseException.class)
        .hasMessage("Syntax error at line 1, character 7: found the end of the string when expecting a word");
  }


}
