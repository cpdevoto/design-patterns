package org.dicegolem;

import static org.dicegolem.attack.Attack.attack;
import static org.dicegolem.attack.AttackRoutine.newAttackRoutine;

import org.dicegolem.attack.Attack;
import org.dicegolem.attack.AttackRoutine;
import org.dicegolem.attack.AttackStatGenerator;
import org.junit.jupiter.api.Test;

public class Main {

  @Test
  public void test() {
    // Configure your own test here by copying from the sample test!
  }

  @Test
  public void sample_test() {

    Attack attack = attack(a -> a.toHitModifier(5)
        .targetAc(18)
        .critOn(19)
        .advantage()
        .elvenAccuracy()
        .damage("1d8 + 3"));

    printStats(1, attack);

    AttackRoutine attackRoutine = newAttackRoutine()
        .extraDamageOnHit("3d6")
        .extraAttackOnCrit(
            attack(a -> a.advantage()
                .toHitModifier(5)
                .critOn(19)
                .damage("2d6 + 5")))
        .attacks(
            attack(a -> a.advantage()
                .toHitModifier(5)
                .critOn(19)
                .damage("2d6 + 5")),
            attack(a -> a.advantage()
                .toHitModifier(5)
                .critOn(19)
                .damage("2d6 + 5")))
        .create();

    printStats(2, attackRoutine);

  }

  private void printStats(int attackIndex, AttackStatGenerator attack) {
    System.out.println("--------------------------------------");
    System.out.println("ATTACK " + attackIndex);
    System.out.println("--------------------------------------");
    System.out.println();
    System.out.println(String.format("Hit Probability: %,.3f", attack.hitProbability()));
    System.out.println(String.format("Crit Probability: %,.3f", attack.critProbability()));
    System.out.println(String.format("DPR: %,.3f", attack.dpr()));
    System.out.println();

  }

}
