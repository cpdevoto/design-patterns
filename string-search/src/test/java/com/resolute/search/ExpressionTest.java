package com.resolute.search;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class ExpressionTest {

  @Test
  public void test() {
    // @formatter:off
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
    assertThat(e.matches("The red hen clucked and squawked.")).isFalse();

  }

}
