package org.dicegolem.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

public class CompositeDiceTest {

  @Test
  public void test_roll() {

    Die die = mock(Die.class);
    when(die.roll()).thenReturn(1, 2, 3, 4);

    CompositeDice dice = CompositeDice.builder()
        .withNumDice(4)
        .withDie(die)
        .build();
    assertThat(dice.roll()).isEqualTo(10);

  }

  @Test
  public void test_roll_with_reroll_once_modifier() {

    Die die = mock(Die.class);
    when(die.roll()).thenReturn(1, 4, 2, 6, 3, 4);

    CompositeDice dice = CompositeDice.builder()
        .withNumDice(4)
        .withDie(die)
        .withModifier(new RerollOnceModifier(2))
        .build();
    assertThat(dice.roll()).isEqualTo(17);

  }

  @Test
  public void test_roll_with_reroll_once_modifier_and_keep_highest_aggregator() {

    Die die = mock(Die.class);
    when(die.roll()).thenReturn(1, 4, 2, 6, 3, 4);

    CompositeDice dice = CompositeDice.builder()
        .withNumDice(4)
        .withDie(die)
        .withModifier(new RerollOnceModifier(2))
        .withAggregator(new KeepHighestAggregator(3))
        .build();
    assertThat(dice.roll()).isEqualTo(14);

  }

}
