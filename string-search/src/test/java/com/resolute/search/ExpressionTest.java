package com.resolute.search;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class ExpressionTest {

  @Test
  public void test() {
    // @formatter:off
    // Equivalent to "fox and not (brown or BLACK)"
    Expression e = new AndExpression(
          new WordExpression("fox"),
          new NotExpression(
              new OrExpression(
                  new WordExpression("brown"),
                  new WordExpression("BLACK")
                  )
              )
        );
    // @formatter:on

    assertThat(e.matches("The red fox raced across the road.")).isTrue();
    assertThat(e.matches("The brown fox hid under a bush.")).isFalse();
    assertThat(e.matches("The black fox snuck into the hen-house.")).isFalse();
    assertThat(e.matches("The black hen clucked and squawked.")).isFalse();

  }

  @Test
  public void test_operator_precedence() {
    // @formatter:off
    // Equivalent to "fox and not brown or BLACK"
    Expression e = new OrExpression(
          new AndExpression(
              new WordExpression("fox"),
              new NotExpression(
                  new WordExpression("brown")
                  )
              ),
          new WordExpression("BLACK")
        );
    // @formatter:on

    assertThat(e.matches("The red fox raced across the road.")).isTrue();
    assertThat(e.matches("The brown fox hid under a bush.")).isFalse();
    assertThat(e.matches("The black fox snuck into the hen-house.")).isTrue();
    assertThat(e.matches("The black hen clucked and squawked.")).isTrue();

  }
}
