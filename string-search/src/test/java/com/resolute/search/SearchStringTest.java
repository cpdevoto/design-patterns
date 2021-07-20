package com.resolute.search;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

public class SearchStringTest {

  @Test
  public void test() {
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
  public void test_syntax_exception() {
    assertThatThrownBy(() -> {
      new SearchString("fox and not\n    (brown or )");
    })
        .isInstanceOf(ParseException.class)
        .hasMessage("Syntax error at line 2, character 15: found ')' when expecting a word");


  }


}
