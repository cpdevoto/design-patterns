package org.devoware.attack;

import static org.devoware.attack.Attack.attack;
import static org.devoware.attack.AttackRoutine.attackRoutine;
import static org.hamcrest.number.IsCloseTo.closeTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class AttackRoutineTest {

  @Test
  public void test_baseline_dpr() {
    // @formatter:off
    // ------------
    // BASELINE (assumes attack score raised at 4 and 8)
    // ------------
    // Level 1:   5.85
    // Level 2:   7.65
    // Level 4:   8.25
    // Level 5:  16.50
    // Level 8:  17.70
    // Level 11: 26.55
    // Level 17: 35.40
    //
    // ALMOST OR EQUAL TO BASELINE: OK
    // LESS THAN BASELINE:          POOR
    // MORE THAN BASELINE:          GOOD
    // 1/2 BASELINE OR LESS:        TERRIBLE
    // 1 1/2 x BASELINE:            VERY GOOD
    // 2 x BASELINE:                TERRIFIC
    
    // @formatter:on

    AttackRoutine routine = attackRoutine(
        attack("1d10 + 1d6"));
    assertThat(routine.dpr(), closeTo(5.85, 0.001));

    routine = attackRoutine(
        attack("1d10 + 3 + 1d6"));
    assertThat(routine.dpr(), closeTo(7.65, 0.001));

    routine = attackRoutine(
        attack("1d10 + 4 + 1d6"));
    assertThat(routine.dpr(), closeTo(8.25, 0.001));

    routine = attackRoutine(
        attack("1d10 + 4 + 1d6"),
        attack("1d10 + 4 + 1d6"));
    assertThat(routine.dpr(), closeTo(16.5, 0.001));

    routine = attackRoutine(
        attack("1d10 + 5 + 1d6"),
        attack("1d10 + 5 + 1d6"));
    assertThat(routine.dpr(), closeTo(17.7, 0.001));

    routine = attackRoutine(
        attack("1d10 + 5 + 1d6"),
        attack("1d10 + 5 + 1d6"),
        attack("1d10 + 5 + 1d6"));
    assertThat(routine.dpr(), closeTo(26.55, 0.001));

    routine = attackRoutine(
        attack("1d10 + 5 + 1d6"),
        attack("1d10 + 5 + 1d6"),
        attack("1d10 + 5 + 1d6"),
        attack("1d10 + 5 + 1d6"));
    assertThat(routine.dpr(), closeTo(35.4, 0.001));

  }

  @Test
  public void test_baseline_damage_on_hit() {
    // @formatter:off
    // ------------
    // BASELINE
    // ------------
    // Level 1:   9
    // Level 2:  12
    // Level 4:  13
    // Level 5:  26
    // Level 8:  28
    // Level 11: 42
    // Level 17: 56
    //
    // ALMOST OR EQUAL TO BASELINE: OK
    // LESS THAN BASELINE:          POOR
    // MORE THAN BASELINE:          GOOD
    // 1/2 BASELINE OR LESS:        TERRIBLE
    // 1 1/2 x BASELINE:            VERY GOOD
    // 2 x BASELINE:                TERRIFIC
    // @formatter:on

    AttackRoutine routine = attackRoutine(
        attack("1d10 + 1d6"));
    assertThat(routine.damageOnHit(), closeTo(9, 0.001));

    routine = attackRoutine(
        attack("1d10 + 3 + 1d6"));
    assertThat(routine.damageOnHit(), closeTo(12, 0.001));

    routine = attackRoutine(
        attack("1d10 + 4 + 1d6"));
    assertThat(routine.damageOnHit(), closeTo(13, 0.001));

    routine = attackRoutine(
        attack("1d10 + 4 + 1d6"),
        attack("1d10 + 4 + 1d6"));
    assertThat(routine.damageOnHit(), closeTo(26, 0.001));

    routine = attackRoutine(
        attack("1d10 + 5 + 1d6"),
        attack("1d10 + 5 + 1d6"));
    assertThat(routine.damageOnHit(), closeTo(28, 0.001));

    routine = attackRoutine(
        attack("1d10 + 5 + 1d6"),
        attack("1d10 + 5 + 1d6"),
        attack("1d10 + 5 + 1d6"));
    assertThat(routine.damageOnHit(), closeTo(42, 0.001));

    routine = attackRoutine(
        attack("1d10 + 5 + 1d6"),
        attack("1d10 + 5 + 1d6"),
        attack("1d10 + 5 + 1d6"),
        attack("1d10 + 5 + 1d6"));
    assertThat(routine.damageOnHit(), closeTo(56, 0.001));

  }

  @Test
  public void test_barb_great_weapon_mastery_at_level_4() {
    // +2 Strength at level 4
    AttackRoutine routine = attackRoutine(
        attack("2d6 + 6", a -> a.advantage()));
    assertThat(routine.dpr(), closeTo(11.603, 0.001));

    System.out.printf("AGAINST BASELINE: %,.2f%n", (11.603 - 8.25) / 8.25 * 100);

    // GWM at level 4
    routine = attackRoutine(
        attack("2d6 + 15", a -> a.advantage().hitModifier(-6)));
    assertThat(routine.dpr(), closeTo(11.902, 0.001));

    System.out.printf("AGAINST BASELINE: %,.2f%n", (11.902 - 8.25) / 8.25 * 100);

  }

}
