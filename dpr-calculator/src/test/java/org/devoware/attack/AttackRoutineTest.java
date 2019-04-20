package org.devoware.attack;

import static org.devoware.attack.Attack.attack;
import static org.devoware.attack.AttackRoutine.attackRoutine;
import static org.hamcrest.number.IsCloseTo.closeTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class AttackRoutineTest {

  @Test
  public void test() {
    AttackRoutine routine = attackRoutine(
        attack("1d10 + 5"),
        attack("1d10 + 5"));

    assertThat(routine.dpr(), closeTo(13.150, 0.001));
  }

  @Test
  public void test_damage_on_hit() {
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


}
