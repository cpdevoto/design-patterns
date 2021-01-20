package org.dicegolem.attack;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.dicegolem.attack.AttackRoutine.attack;

import org.junit.jupiter.api.Test;

public class AttackRoutineTest {

  @Test
  public void test_dpr() {

    double expectedDpr =
        ((1 - 0.4 * 0.4) * 12) + ((1 - 0.9 * 0.9) * 7) + // attack 1
            ((1 - 0.4 * 0.4) * 12) + ((1 - 0.9 * 0.9) * 7) + // attack 2
            ((1 - (1 - (1 - 0.4 * 0.4)) * (1 - (1 - 0.4 * 0.4))) * 10.5) + // extra damage on hit
            ((1 - (1 - (1 - 0.9 * 0.9)) * (1 - (1 - 0.9 * 0.9)))
                * (((1 - 0.4 * 0.4) * 12) + ((1 - 0.9 * 0.9) * 7))); // extra attack on crit
    // @formatter:off
    double actualDpr = AttackRoutine.newAttackRoutine()
        .extraDamageOnHit("3d6")
        .extraAttackOnCrit(
            attack(a -> a.advantage()
                .toHitModifier(5)
                .critOn(19)
                .damage("2d6 + 5")
            ))
        .attacks(
            attack(a -> a.advantage()
                .toHitModifier(5)
                .critOn(19)
                .damage("2d6 + 5")
            ),
            attack(a -> a.advantage()
                .toHitModifier(5)
                .critOn(19)
                .damage("2d6 + 5")
            ))
        .create()
        .dpr();
    // @formatter:off
    
    System.out.println("Computed DPR is: " + actualDpr);
    assertThat(actualDpr).isCloseTo(expectedDpr, within(0.01));
  }

  @Test
  public void test_hit_probability() {

    double expectedHitProbability =
        (1 - 0.4 * 0.4) + ((1 - (1 - 0.4 * 0.4)) * (1 - 0.4 * 0.4));
    // @formatter:off
    double actualHitProbability = AttackRoutine.newAttackRoutine()
        .attacks(
            attack(a -> a.advantage()
                .toHitModifier(5)
                .critOn(19)
                .damage("2d6 + 5")
            ),
            attack(a -> a.advantage()
                .toHitModifier(5)
                .critOn(19)
                .damage("2d6 + 5")
            ))
        .create()
        .hitProbability();
    // @formatter:off
    
    System.out.println("Computed Hit Probability is: " + actualHitProbability);
    assertThat(actualHitProbability).isCloseTo(expectedHitProbability, within(0.01));
  }

  @Test
  public void test_crit_probability() {

    double expectedCritProbability =
        (1 - 0.9 * 0.9) + ((1 - (1 - 0.9 * 0.9)) * (1 - 0.9 * 0.9));
    // @formatter:off
    double actualCritProbability = AttackRoutine.newAttackRoutine()
        .attacks(
            attack(a -> a.advantage()
                .toHitModifier(5)
                .critOn(19)
                .damage("2d6 + 5")
            ),
            attack(a -> a.advantage()
                .toHitModifier(5)
                .critOn(19)
                .damage("2d6 + 5")
            ))
        .create()
        .critProbability();
    // @formatter:off
    
    System.out.println("Computed Crit Probability is: " + actualCritProbability);
    assertThat(actualCritProbability).isCloseTo(expectedCritProbability, within(0.01));
  }

}
