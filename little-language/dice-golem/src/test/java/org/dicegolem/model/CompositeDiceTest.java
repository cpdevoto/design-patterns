package org.dicegolem.model;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.dicegolem.model.CompositeDice;
import org.dicegolem.model.Die;
import org.dicegolem.model.KeepHighestAggregator;
import org.dicegolem.model.RerollOnceModifier;
import org.junit.Test;

public class CompositeDiceTest {

  @Test
  public void test_roll() {

    Die die = mock(Die.class);
    when(die.roll()).thenReturn(1, 2, 3, 4);

    CompositeDice dice = CompositeDice.builder()
        .withNumDice(4)
        .withDie(die)
        .build();
    assertThat(dice.roll(), equalTo(10));

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
    assertThat(dice.roll(), equalTo(17));

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
    assertThat(dice.roll(), equalTo(14));

  }

}
