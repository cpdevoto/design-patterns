package org.dicegolem.model;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.dicegolem.model.Die;
import org.dicegolem.model.DieRollModifier;
import org.dicegolem.model.RerollOnceModifier;
import org.junit.Test;

public class DieRollModifierTest {

  @Test
  public void test_reroll_once_modifier() {

    Die die = mock(Die.class);
    when(die.roll()).thenReturn(5);

    DieRollModifier modifier = new RerollOnceModifier(2);
    assertThat(modifier.modify(die, 1), equalTo(5));
    assertThat(modifier.modify(die, 2), equalTo(5));
    assertThat(modifier.modify(die, 3), equalTo(3));
    assertThat(modifier.modify(die, 4), equalTo(4));

  }

}
