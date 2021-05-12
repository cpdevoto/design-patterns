package org.dicegolem;

import static org.dicegolem.attack.Attack.attack;
import static org.dicegolem.attack.AttackRoutine.newAttackRoutine;

import org.dicegolem.attack.Attack;
import org.dicegolem.attack.AttackRoutine;
import org.dicegolem.attack.AttackStatGenerator;
import org.junit.jupiter.api.Test;

public class Sorlock {

  @Test
  public void test() {
    // Configure your own test here by copying from the sample test!
    AttackRoutine attackRoutine = newAttackRoutine()
        .extraDamageOnHit("2d6") // Sneak Attack
        .attacks(
            attack(a -> a.advantage() // Assuming bonus action Hide, Steady Aim, or familiar w/Help
                .targetAc(18)
                .toHitModifier("6 + 1d4") // Assuming Bless!
                .damage("1d8 + 4")))
        .create();

    printStats("Neph vs. Venomfang", attackRoutine);

  }


  @Test
  public void test_sorlock_dpr() {
    AttackRoutine attackRoutine = newAttackRoutine()
        .attacks(
            attack(a -> a
                .critOn(19)
                .advantage()
                .damage("1d10 + 11")),
            attack(a -> a
                .critOn(19)
                // .advantage()
                .damage("1d10 + 11")),
            attack(a -> a
                .critOn(19)
                // .advantage()
                .damage("1d10 + 11")),
            attack(a -> a
                .critOn(19)
                // .advantage()
                .damage("1d10 + 11"))
        // Spacer
        )
        .create();

    printStats("Sorlock - Level 1", attackRoutine);
  }

  @Test
  public void test_baseline_warlock_dpr() {
    AttackRoutine attackRoutine = newAttackRoutine()
        .attacks(
            attack(a -> a
                .damage("1d10 + 1d6"))) // Eldritch Blast and Hex
        .create();

    printStats("Warlock - Level 1", attackRoutine);

    attackRoutine = newAttackRoutine()
        .attacks(
            attack(a -> a
                .damage("1d10 + 3 + 1d6"))) // Eldritch Blast, Agonizing Blast, and Hex
        .create();

    printStats("Warlock - Level 2 and 3", attackRoutine);

    attackRoutine = newAttackRoutine()
        .attacks(
            attack(a -> a
                .damage("1d10 + 4 + 1d6"))) // Eldritch Blast, Agonizing Blast, and Hex
        .create();

    printStats("Warlock - Level 4", attackRoutine);

    attackRoutine = newAttackRoutine()
        .attacks(
            attack(a -> a
                .damage("1d10 + 4 + 1d6")),
            attack(a -> a
                .damage("1d10 + 4 + 1d6")))
        .create();

    printStats("Warlock - Level 5 thru 7", attackRoutine);

    attackRoutine = newAttackRoutine()
        .attacks(
            attack(a -> a
                .damage("1d10 + 5 + 1d6")),
            attack(a -> a
                .damage("1d10 + 5 + 1d6")))
        .create();

    printStats("Warlock - Level 8 - 10", attackRoutine);

    attackRoutine = newAttackRoutine()
        .attacks(
            attack(a -> a
                .damage("1d10 + 5 + 1d6")),
            attack(a -> a
                .damage("1d10 + 5 + 1d6")),
            attack(a -> a
                .damage("1d10 + 5 + 1d6")))
        .create();

    printStats("Warlock - Level 11 - 16", attackRoutine);

    attackRoutine = newAttackRoutine()
        .attacks(
            attack(a -> a
                .damage("1d10 + 5 + 1d6")),
            attack(a -> a
                .damage("1d10 + 5 + 1d6")),
            attack(a -> a
                .damage("1d10 + 5 + 1d6")),
            attack(a -> a
                .damage("1d10 + 5 + 1d6")))
        .create();

    printStats("Warlock - Level 17 - 20", attackRoutine);

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
        // .extraDamageOnHit("3d6")
        .extraAttackOnCrit(
            attack(a -> a.advantage()
                .toHitModifier(0)
                .targetAc(14)
                .critOn(19)
                .damage("2d6 + 15")))
        .attacks(
            attack(a -> a.advantage()
                .toHitModifier(0)
                .targetAc(14)
                .critOn(19)
                .damage("2d6 + 15")),
            attack(a -> a.advantage()
                .toHitModifier(0)
                .targetAc(14)
                .critOn(19)
                .damage("2d6 + 15")))
        .create();

    printStats("Barbarian w/ Maul and GWM - Power Attack", attackRoutine);

    AttackRoutine attackRoutine2 = newAttackRoutine()
        // .extraDamageOnHit("3d6")
        .extraAttackOnCrit(
            attack(a -> a.advantage()
                .toHitModifier(5)
                .targetAc(14)
                .critOn(19)
                .damage("2d6 + 5")))
        .attacks(
            attack(a -> a.advantage()
                .toHitModifier(5)
                .targetAc(14)
                .critOn(19)
                .damage("2d6 + 5")),
            attack(a -> a.advantage()
                .toHitModifier(5)
                .targetAc(14)
                .critOn(19)
                .damage("2d6 + 5")))
        .create();

    printStats("Barbarian w/ Maul and GWM - No Power Attack", attackRoutine2);
  }

  private void printStats(int attackIndex, AttackStatGenerator attack) {
    printStats("ATTACK " + attackIndex, attack);
  }

  private void printStats(String attackName, AttackStatGenerator attack) {
    System.out.println("--------------------------------------");
    System.out.println(attackName);
    System.out.println("--------------------------------------");
    System.out.println();
    System.out.println(String.format("Hit Probability: %,.3f", attack.hitProbability()));
    System.out.println(String.format("Crit Probability: %,.3f", attack.critProbability()));
    System.out.println(String.format("DPR: %,.3f", attack.dpr()));
    System.out.println();

  }

}
