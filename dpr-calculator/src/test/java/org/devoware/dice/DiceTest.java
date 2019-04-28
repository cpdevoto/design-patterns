package org.devoware.dice;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.number.IsCloseTo.closeTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class DiceTest {

  @Test
  public void test() {

    assertThat(Dice.parse("1d6ro<2").dpr(), closeTo(4.16, 0.01)); // great weapon fighting
    assertThat(Dice.parse("2d6 + 5").dpr(), equalTo(12.0));
    assertThat(Dice.parse("1d8 + 3").dpr(), equalTo(7.5));
    assertThat(Dice.parse("1d8 + 3 + 1d6").dpr(), equalTo(11.0));
    assertThat(Dice.parse("1d8 + 3 + 0.5 * 1d6").dpr(), equalTo(9.25));
    assertThat(Dice.parse("1d8 + 5 + ((0.4 * 2d8)nc)").toString(),
        equalTo("1d8 + 5 + ((0.40 * 2d8)nc)"));
    assertThat(Dice.parse("0.3861 * 1d8nc").toString(),
        equalTo("0.39 * 1d8nc"));


  }
}
