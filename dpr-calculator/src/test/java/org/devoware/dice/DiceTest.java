package org.devoware.dice;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class DiceTest {

  @Test
  public void test() {

    assertThat(Dice.parse("2d6 + 5").dpr(), equalTo(12.0));
    assertThat(Dice.parse("1d8w + 3 + 1d6").dpr(), equalTo(11.0));

  }
}
