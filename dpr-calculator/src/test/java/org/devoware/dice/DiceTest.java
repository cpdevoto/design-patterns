package org.devoware.dice;

import org.junit.Test;

public class DiceTest {

  @Test
  public void test() {

    System.out.println(Dice.parse("2d6 + 5").dpr());
  }
}
