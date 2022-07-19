package org.devoware.dicegolem.dice;

import static org.devoware.dicegolem.testinfra.AssertRandomNumberGenerator.assertThat;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class DieTest {

  @Test
  public void should_return_D4_when_get_called_with_4() {
    Assertions.assertThat(Die.get(4)).isEqualTo(Die.D4);
  }

  @Test
  public void should_return_null_when_get_called_with_2() {
    Assertions.assertThat(Die.get(2)).isNull();
  }

  @Test
  public void should_roll_between_1_and_4_when_D4() {

    assertThat(() -> Die.D4.roll())
        .generatesNumbersBetween(1, 4);

  }

  @Test
  public void should_roll_between_1_and_6_when_D6() {

    assertThat(() -> Die.D6.roll())
        .generatesNumbersBetween(1, 6);

  }

  @Test
  public void should_roll_between_1_and_8_when_D8() {

    assertThat(() -> Die.D8.roll())
        .generatesNumbersBetween(1, 8);

  }

  @Test
  public void should_roll_between_1_and_10_when_D10() {

    assertThat(() -> Die.D10.roll())
        .generatesNumbersBetween(1, 10);

  }

  @Test
  public void should_roll_between_1_and_12_when_D12() {

    assertThat(() -> Die.D12.roll())
        .generatesNumbersBetween(1, 12);

  }

  @Test
  public void should_roll_between_1_and_20_when_D20() {

    assertThat(() -> Die.D20.roll())
        .generatesNumbersBetween(1, 20);

  }

  @Test
  public void should_roll_between_1_and_100_when_D100() {

    assertThat(() -> Die.D100.roll())
        .generatesNumbersBetween(1, 100);

  }

}
