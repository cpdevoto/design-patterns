package org.dicegolem.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.dicegolem.model.fixtures.Assertions.assertDieRange;

import org.junit.jupiter.api.Test;

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
    assertThat(die).isNotNull();
    assertThat(die.getType()).isEqualTo(4);

    die = Die.get(54);
    assertThat(die).isNotNull();
    assertThat(die.getType()).isEqualTo(54);
  }

  @Test
  public void test_values() {
    int numDice = Die.values().size();
    Die.get(100);
    assertThat(Die.values().size()).isEqualTo(numDice)
        .as("Expected the size of the Die cache to remain the same");
    Die.get(121);
    int newNumDice = Die.values().size();
    assertThat(newNumDice).isGreaterThan(numDice)
        .as("Expected a new Die object to be added to the Die cache");
    int oldType = -1;
    for (Die d : Die.values()) {
      if (oldType != -1) {
        assertThat(oldType).isLessThan(d.getType())
            .as("Expected values() to return the Die objects sorted by type ascending");
      }
      oldType = d.getType();
    }
  }

  @Test
  public void test_to_string() {
    for (Die die : Die.values()) {
      assertThat(die.toString()).isEqualTo("D" + die.getType());
    }
  }

}
