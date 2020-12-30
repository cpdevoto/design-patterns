package org.dicegolem.model;

import static org.dicegolem.model.fixtures.Assertions.assertDieRange;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class DieTest {


  @Test
  public void test_roll() {
    for (Die die : Die.values()) {
      assertDieRange(die);
    }
  }

  @Test
  public void test_get() {
    Die die = Die.get(4);
    assertThat(die, notNullValue());
    assertThat(die.getType(), equalTo(4));

    die = Die.get(54);
    assertThat(die, notNullValue());
    assertThat(die.getType(), equalTo(54));
  }

  @Test
  public void test_values() {
    int numDice = Die.values().size();
    Die.get(100);
    assertThat("Expected the size of the Die cache to remain the same", Die.values().size(),
        equalTo(numDice));
    Die.get(121);
    int newNumDice = Die.values().size();
    assertThat("Expected a new Die object to be added to the Die cache", newNumDice > numDice,
        equalTo(true));
    int oldType = -1;
    for (Die d : Die.values()) {
      if (oldType != -1) {
        assertThat("Expected values() to return the Die objects sorted by type ascending",
            oldType < d.getType(), equalTo(true));
      }
      oldType = d.getType();
    }
  }

  @Test
  public void test_to_string() {
    for (Die die : Die.values()) {
      assertThat(die.toString(), equalTo("D" + die.getType()));
    }
  }

}
