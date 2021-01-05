package org.dicegolem.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

public class DieRollModifierTest {

  @Test
  public void test_reroll_once_modifier() {

    Die die = mock(Die.class);
    when(die.roll()).thenReturn(5);

    DieRollModifier modifier = new RerollOnceModifier(2);
    assertThat(modifier.modify(die, 1)).isEqualTo(5);
    assertThat(modifier.modify(die, 2)).isEqualTo(5);
    assertThat(modifier.modify(die, 3)).isEqualTo(3);
    assertThat(modifier.modify(die, 4)).isEqualTo(4);

  }

}
