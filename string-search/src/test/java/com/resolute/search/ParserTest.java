package com.resolute.search;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

public class ParserTest {

  @Test
  public void test() {
    Expression e = Parser.parse("fox and not\n    (brown or BLACK)");

    assertThat(e.matches("The red fox raced across the road.")).isTrue();
    assertThat(e.matches("The brown fox hid under a bush.")).isFalse();
    assertThat(e.matches("The black fox snuck into the hen-house.")).isFalse();
    assertThat(e.matches("The red hen clucked and squawked.")).isFalse();

  }

  @Test
  public void test_syntax_exception() {
    assertThatThrownBy(() -> {
      Parser.parse("fox and not\n    (brown or )");
    })
        .isInstanceOf(ParseException.class)
        .hasMessage("Syntax error at line 2, character 15: found ')' when expecting a word");


  }


}
