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
  public void test_leugren() {
    AttackRoutine attackRoutine = newAttackRoutine()
        .attacks(
            attack(a -> a // Warhammer (DPR is low, but there is always the threat of Divine Smite)
                .targetAc(14)
                .toHitModifier(5)
                .damage("1d8 + 3")))
        .create();

    printStats("Leugren - Level 1 - 3", attackRoutine);

    attackRoutine = newAttackRoutine()
        .attacks(
            attack(a -> a // Warhammer
                .targetAc(15)
                .toHitModifier(5)
                .damage("1d8 + 3")))
        .create();

    printStats("Leugren - Level 4", attackRoutine);

    attackRoutine = newAttackRoutine()
        .attacks(
            attack(a -> a // Warhammer
                .targetAc(16)
                .toHitModifier(6)
                .damage("1d8 + 3")),
            attack(a -> a // Warhammer
                .targetAc(16)
                .toHitModifier(6)
                .damage("1d8 + 3")))
        .create();

    printStats("Leugren - Level 5 - 8", attackRoutine);

    attackRoutine = newAttackRoutine()
        .attacks(
            attack(a -> a // Warhammer
                .targetAc(18)
                .toHitModifier(8)
                .damage("1d8 + 4")),
            attack(a -> a // Warhammer
                .targetAc(18)
                .toHitModifier(8)
                .damage("1d8 + 4")))
        .create();

    printStats("Leugren - Level 9", attackRoutine);

    attackRoutine = newAttackRoutine()
        .attacks(
            attack(a -> a // Warhammer and Spirit Shroud
                .targetAc(18)
                .toHitModifier(8)
                .damage("1d8 + 4 + 1d8")),
            attack(a -> a // Warhammer and Spirit Shroud
                .targetAc(18)
                .toHitModifier(8)
                .damage("1d8 + 4 + 1d8")))
        .create();

    printStats("Leugren - Level 10", attackRoutine);

    attackRoutine = newAttackRoutine()
        .attacks(
            attack(a -> a // Warhammer and Spirit Shroud and Improved Divine Smite
                .targetAc(18)
                .toHitModifier(8)
                .damage("1d8 + 4 + 2d8")),
            attack(a -> a // Warhammer and Spirit Shroud and Improved Divine Smite
                .targetAc(18)
                .toHitModifier(8)
                .damage("1d8 + 4 + 2d8")))
        .create();

    printStats("Leugren - Level 11 - 12", attackRoutine);

    attackRoutine = newAttackRoutine()
        .attacks(
            attack(a -> a // Warhammer and Spirit Shroud and Improved Divine Smite
                .targetAc(19)
                .toHitModifier(9)
                .damage("1d8 + 4 + 2d8")),
            attack(a -> a // Warhammer and Spirit Shroud and Improved Divine Smite
                .targetAc(19)
                .toHitModifier(9)
                .damage("1d8 + 4 + 2d8")))
        .create();

    printStats("Leugren - Level 13 - 16", attackRoutine);

    attackRoutine = newAttackRoutine()
        .attacks(
            attack(a -> a // Warhammer and Spirit Shroud and Improved Divine Smite
                .targetAc(20)
                .toHitModifier(10)
                .damage("1d8 + 4 + 2d8")),
            attack(a -> a // Warhammer and Spirit Shroud and Improved Divine Smite
                .targetAc(20)
                .toHitModifier(10)
                .damage("1d8 + 4 + 2d8")))
        .create();

    printStats("Leugren - Level 17 - 20", attackRoutine);

  }

  @Test
  public void test_barbarian_w_gwm_and_pam() {
    AttackRoutine attackRoutine = newAttackRoutine()
        .attacks(
            attack(a -> a.advantage()
                .targetAc(16)
                .toHitModifier(1)
                .damage("1d10 + 15")),
            attack(a -> a.advantage()
                .targetAc(16)
                .toHitModifier(1)
                .damage("1d10 + 15")),
            attack(a -> a.advantage()
                .targetAc(16)
                .toHitModifier(1)
                .damage("1d4 + 15")))
        .create();

    printStats("Barbarian w/ GWM and PAM - Level 5", attackRoutine);

  }

  @Test
  public void test_hexblade_w_gwm_and_pam() {

    AttackRoutine attackRoutine = newAttackRoutine()
        .attacks(
            attack(a -> a // Spear and Hex
                .targetAc(14)
                .toHitModifier(5)
                .damage("1d6 + 3 + 1d6")),
            attack(a -> a
                .targetAc(14)
                .toHitModifier(5)
                .damage("1d4 + 3 + 1d6")))
        .create();

    printStats("Hexblade PAM - Level 1 and 2", attackRoutine);

    attackRoutine = newAttackRoutine()
        .attacks(
            attack(a -> a // Spear and Hex and Hexblade's Curse
                .targetAc(14)
                .toHitModifier(5)
                .critOn(19)
                .damage("1d6 + 5 + 1d6")),
            attack(a -> a
                .targetAc(14)
                .toHitModifier(5)
                .critOn(19)
                .damage("1d4 + 5 + 1d6")))
        .create();

    printStats("Hexblade PAM - Level 1 and 2 (Hexblade's Curse)", attackRoutine);

    attackRoutine = newAttackRoutine()
        .attacks(
            attack(a -> a.advantage() // Glaive and Hex and Familiar Help
                .targetAc(14)
                .toHitModifier(5)
                .damage("1d10 + 3 + 1d6")),
            attack(a -> a
                .targetAc(14)
                .toHitModifier(5)
                .damage("1d4 + 3 + 1d6")))
        .create();

    printStats("Hexblade PAM - Level 3", attackRoutine);

    attackRoutine = newAttackRoutine()
        .attacks(
            attack(a -> a.advantage() // Glaive and Hex and Familiar Help
                .targetAc(14)
                .toHitModifier(5)
                .critOn(19)
                .damage("1d10 + 5 + 1d6")),
            attack(a -> a
                .targetAc(14)
                .toHitModifier(5)
                .critOn(19)
                .damage("1d4 + 5 + 1d6")))
        .create();

    printStats("Hexblade PAM - Level 3 (Hexblade's Curse)", attackRoutine);

    attackRoutine = newAttackRoutine()
        .attacks(
            attack(a -> a.advantage() // Glaive and Hex and Familiar Help
                .targetAc(15)
                .toHitModifier(0)
                .damage("1d10 + 13 + 1d6")),
            attack(a -> a
                .targetAc(15)
                .toHitModifier(0)
                .damage("1d4 + 13 + 1d6")))
        .create();

    printStats("Hexblade PAM and GWM - Level 4", attackRoutine);

    attackRoutine = newAttackRoutine()
        .attacks(
            attack(a -> a.advantage() // Glaive and Hex and Familiar Help
                .targetAc(15)
                .toHitModifier(0)
                .critOn(19)
                .damage("1d10 + 15 + 1d6")),
            attack(a -> a
                .targetAc(15)
                .toHitModifier(0)
                .critOn(19)
                .damage("1d4 + 15 + 1d6")))
        .create();

    printStats("Hexblade PAM and GWM - Level 4 (Hexblade's Curse)", attackRoutine);

    attackRoutine = newAttackRoutine()
        .attacks(
            attack(a -> a.advantage() // Glaive and Spirit Shroud and Familiar Help
                .targetAc(16)
                .toHitModifier(1)
                .damage("1d10 + 13 + 1d8")),
            attack(a -> a
                .targetAc(16)
                .toHitModifier(1)
                .damage("1d10 + 13 + 1d8")),
            attack(a -> a
                .targetAc(16)
                .toHitModifier(1)
                .damage("1d4 + 13 + 1d8")))
        .create();

    printStats("Hexblade PAM and GWM - Level 5 and 6", attackRoutine);

    attackRoutine = newAttackRoutine()
        .attacks(
            attack(a -> a.advantage() // Glaive and Spirit Shroud and Familiar Help
                .targetAc(16)
                .toHitModifier(1)
                .critOn(19)
                .damage("1d10 + 16 + 1d8")),
            attack(a -> a
                .targetAc(16)
                .toHitModifier(1)
                .critOn(19)
                .damage("1d10 + 16 + 1d8")),
            attack(a -> a
                .targetAc(16)
                .toHitModifier(1)
                .critOn(19)
                .damage("1d4 + 16 + 1d8")))
        .create();

    printStats("Hexblade PAM and GWM - Level 5 and 6 (Hexblade's Curse)", attackRoutine);

    attackRoutine = newAttackRoutine()
        .attacks(
            attack(a -> a.advantage() // Glaive and Spirit Shroud and Familiar Help
                .targetAc(16)
                .toHitModifier(1)
                .damage("1d10 + 13 + 1d8")),
            attack(a -> a
                .targetAc(16)
                .toHitModifier(1)
                .damage("1d10 + 13 + 1d8")),
            attack(a -> a
                .targetAc(16)
                .toHitModifier(1)
                .damage("1d4 + 13 + 1d8")))
        .create();

    printStats("Hexblade PAM and GWM - Level 7 (Spirit Shroud)", attackRoutine);

    attackRoutine = newAttackRoutine()
        .attacks(
            attack(a -> a.advantage() // Glaive and Spirit Shroud and Familiar Help
                .targetAc(16)
                .toHitModifier(1)
                .critOn(19)
                .damage("1d10 + 16 + 1d8")),
            attack(a -> a
                .targetAc(16)
                .toHitModifier(1)
                .critOn(19)
                .damage("1d10 + 16 + 1d8")),
            attack(a -> a
                .targetAc(16)
                .toHitModifier(1)
                .critOn(19)
                .damage("1d4 + 16 + 1d8")))
        .create();

    printStats("Hexblade PAM and GWM - Level 7 (Spirit Shroud / Hexblade's Curse)", attackRoutine);

    attackRoutine = newAttackRoutine()
        .attacks(
            attack(a -> a.advantage() // Glaive and Shadow of Moil
                .targetAc(16)
                .toHitModifier(1)
                .damage("1d10 + 13")),
            attack(a -> a.advantage()
                .targetAc(16)
                .toHitModifier(1)
                .damage("1d10 + 13")),
            attack(a -> a.advantage()
                .targetAc(16)
                .toHitModifier(1)
                .damage("1d4 + 13")))
        .create();

    printStats("Hexblade PAM and GWM - Level 7 (Shadow of Moil)", attackRoutine);

    attackRoutine = newAttackRoutine()
        .attacks(
            attack(a -> a.advantage() // Glaive and Shadow of Moil
                .targetAc(16)
                .toHitModifier(1)
                .critOn(19)
                .damage("1d10 + 16")),
            attack(a -> a.advantage()
                .targetAc(16)
                .toHitModifier(1)
                .critOn(19)
                .damage("1d10 + 16")),
            attack(a -> a.advantage()
                .targetAc(16)
                .toHitModifier(1)
                .critOn(19)
                .damage("1d4 + 16")))
        .create();

    printStats("Hexblade PAM and GWM - Level 7 (Shadow of Moil / Hexblade's Curse)", attackRoutine);

    attackRoutine = newAttackRoutine()
        .attacks(
            attack(a -> a.advantage() // Glaive and Shadow of Moil
                .targetAc(17)
                .toHitModifier(2)
                .damage("1d10 + 14")),
            attack(a -> a.advantage()
                .targetAc(17)
                .toHitModifier(2)
                .damage("1d10 + 14")),
            attack(a -> a.advantage()
                .targetAc(17)
                .toHitModifier(2)
                .damage("1d4 + 14")))
        .create();

    printStats("Hexblade PAM and GWM - Level 8 (Shadow of Moil)", attackRoutine);

    attackRoutine = newAttackRoutine()
        .attacks(
            attack(a -> a.advantage() // Glaive and Shadow of Moil
                .targetAc(17)
                .toHitModifier(2)
                .critOn(19)
                .damage("1d10 + 17")),
            attack(a -> a.advantage()
                .targetAc(17)
                .toHitModifier(2)
                .critOn(19)
                .damage("1d10 + 17")),
            attack(a -> a.advantage()
                .targetAc(17)
                .toHitModifier(2)
                .critOn(19)
                .damage("1d4 + 17")))
        .create();

    printStats("Hexblade PAM and GWM - Level 8 (Shadow of Moil / Hexblade's Curse)", attackRoutine);

    attackRoutine = newAttackRoutine()
        .attacks(
            attack(a -> a.advantage() // Glaive and Shadow of Moil
                .targetAc(18)
                .toHitModifier(3)
                .damage("1d10 + 14")),
            attack(a -> a.advantage()
                .targetAc(18)
                .toHitModifier(3)
                .damage("1d10 + 14")),
            attack(a -> a.advantage()
                .targetAc(18)
                .toHitModifier(3)
                .damage("1d4 + 14")))
        .create();

    printStats("Hexblade PAM and GWM - Level 9 - 11 (Shadow of Moil)", attackRoutine);

    attackRoutine = newAttackRoutine()
        .attacks(
            attack(a -> a.advantage() // Glaive and Shadow of Moil
                .targetAc(18)
                .toHitModifier(3)
                .critOn(19)
                .damage("1d10 + 18")),
            attack(a -> a.advantage()
                .targetAc(18)
                .toHitModifier(3)
                .critOn(19)
                .damage("1d10 + 18")),
            attack(a -> a.advantage()
                .targetAc(18)
                .toHitModifier(3)
                .critOn(19)
                .damage("1d4 + 18")))
        .create();

    printStats("Hexblade PAM and GWM - Level 9 - 11 (Shadow of Moil / Hexblade's Curse)",
        attackRoutine);

    attackRoutine = newAttackRoutine()
        .attacks(
            attack(a -> a.advantage() // Glaive and Shadow of Moil and Lifedrinker
                .targetAc(18)
                .toHitModifier(4)
                .damage("1d10 + 20")),
            attack(a -> a.advantage()
                .targetAc(18)
                .toHitModifier(4)
                .damage("1d10 + 20")),
            attack(a -> a.advantage()
                .targetAc(18)
                .toHitModifier(4)
                .damage("1d4 + 20")))
        .create();

    printStats("Hexblade PAM and GWM - Level 12 (Shadow of Moil)", attackRoutine);

    attackRoutine = newAttackRoutine()
        .attacks(
            attack(a -> a.advantage() // Glaive and Shadow of Moil and Lifedrinker
                .targetAc(18)
                .toHitModifier(4)
                .critOn(19)
                .damage("1d10 + 24")),
            attack(a -> a.advantage()
                .targetAc(18)
                .toHitModifier(4)
                .critOn(19)
                .damage("1d10 + 24")),
            attack(a -> a.advantage()
                .targetAc(18)
                .toHitModifier(4)
                .critOn(19)
                .damage("1d4 + 24")))
        .create();

    printStats("Hexblade PAM and GWM - Level 12 (Shadow of Moil / Hexblade's Curse)",
        attackRoutine);

    attackRoutine = newAttackRoutine()
        .attacks(
            attack(a -> a.advantage() // Glaive and Shadow of Moil and Lifedrinker
                .targetAc(19)
                .toHitModifier(5)
                .damage("1d10 + 20")),
            attack(a -> a.advantage()
                .targetAc(19)
                .toHitModifier(5)
                .damage("1d10 + 20")),
            attack(a -> a.advantage()
                .targetAc(19)
                .toHitModifier(5)
                .damage("1d4 + 20")))
        .create();

    printStats("Hexblade PAM and GWM - Level 13 - 16 (Shadow of Moil)", attackRoutine);

    attackRoutine = newAttackRoutine()
        .attacks(
            attack(a -> a.advantage() // Glaive and Shadow of Moil and Lifedrinker
                .targetAc(19)
                .toHitModifier(5)
                .critOn(19)
                .damage("1d10 + 25")),
            attack(a -> a.advantage()
                .targetAc(19)
                .toHitModifier(5)
                .critOn(19)
                .damage("1d10 + 25")),
            attack(a -> a.advantage()
                .targetAc(19)
                .toHitModifier(5)
                .critOn(19)
                .damage("1d4 + 25")))
        .create();

    printStats("Hexblade PAM and GWM - Level 13 - 16 (Shadow of Moil / Hexblade's Curse)",
        attackRoutine);

    attackRoutine = newAttackRoutine()
        .attacks(
            attack(a -> a.advantage() // Glaive and Shadow of Moil
                .targetAc(20)
                .toHitModifier(6)
                .damage("1d10 + 20")),
            attack(a -> a.advantage()
                .targetAc(20)
                .toHitModifier(6)
                .damage("1d10 + 20")),
            attack(a -> a.advantage()
                .targetAc(20)
                .toHitModifier(6)
                .damage("1d4 + 20")))
        .create();

    printStats("Hexblade PAM and GWM - Level 17 - 20 (Shadow of Moil)", attackRoutine);

    attackRoutine = newAttackRoutine()
        .attacks(
            attack(a -> a.advantage() // Glaive and Shadow of Moil
                .targetAc(20)
                .toHitModifier(6)
                .critOn(19)
                .damage("1d10 + 26")),
            attack(a -> a.advantage()
                .targetAc(20)
                .toHitModifier(6)
                .critOn(19)
                .damage("1d10 + 26")),
            attack(a -> a.advantage()
                .targetAc(20)
                .toHitModifier(6)
                .critOn(19)
                .damage("1d4 + 26")))
        .create();

    printStats("Hexblade PAM and GWM - Level 17 - 20 (Shadow of Moil / Hexblade's Curse)",
        attackRoutine);

  }

  @Test
  public void test_ranger_w_ss_and_crossbow_expert() {
    AttackRoutine attackRoutine = newAttackRoutine()
        .attacks(
            attack(a -> a // Hand Crossbow and Hunter's Mark
                .targetAc(16)
                .toHitModifier(3)
                .damage("1d6 + 15 + 1d6")),
            attack(a -> a
                .targetAc(16)
                .toHitModifier(3)
                .damage("1d6 + 15 + 1d6")),
            attack(a -> a
                .targetAc(16)
                .toHitModifier(3)
                .damage("1d6 + 15 + 1d6")))
        .create();

    printStats("Fighter w/ Sharpshooter and Crossbow Expert - Level 5", attackRoutine);

  }

  @Test
  public void test_kalrys() {
    AttackRoutine attackRoutine = newAttackRoutine()
        .attacks(
            attack(a -> a.targetAc(14)
                .toHitModifier(4)
                .damage("1d8 + 2")))
        .create();

    printStats("Kalrys Light Crossbow", attackRoutine);

    attackRoutine = newAttackRoutine()
        .attacks(
            attack(a -> a.targetAc(14)
                .toHitModifier(5)
                .damage("1d10")))
        .create();

    printStats("Kalrys Fire Bolt", attackRoutine);
  }

  @Test
  public void test_neph_booming_blade_dpr() {
    AttackRoutine attackRoutine = newAttackRoutine()
        .extraDamageOnHit("1d6")
        .attacks(
            attack(a -> a
                .damage("1d8 + 3"))) // Light Crossbow
        .create();

    printStats("Neph - Level 1", attackRoutine);

    attackRoutine = newAttackRoutine()
        .extraDamageOnHit("1d6")
        .attacks(
            attack(a -> a.advantage() // Bonus Action Hide or Steady Aim
                .damage("1d8 + 3")))// Light Crossbow
        .create();

    printStats("Neph - Level 2", attackRoutine);

    attackRoutine = newAttackRoutine()
        .extraDamageOnHit("2d6")
        .attacks(
            attack(a -> a.advantage() // Bonus Action Hide or Steady Aim
                .damage("1d8 + 3"))) // Light Crossbow
        .create();

    printStats("Neph - Level 3", attackRoutine);

    attackRoutine = newAttackRoutine()
        .extraDamageOnHit("2d6")
        .attacks(
            attack(a -> a.advantage() // Bonus Action Hide or Steady Aim
                .damage("1d8 + 4"))) // Light Crossbow
        .create();

    printStats("Neph - Level 4", attackRoutine);

    attackRoutine = newAttackRoutine()
        .extraDamageOnHit("3d6")
        .attacks(
            attack(a -> a.advantage() // Advantage from Familiar
                .damage("1d8 + 4 + 1d8"))) // Booming Blade
        .create();

    printStats("Neph - Level 5 and 6", attackRoutine);

    attackRoutine = newAttackRoutine()
        .extraDamageOnHit("4d6")
        .attacks(
            attack(a -> a.advantage() // Advantage from Familiar
                .damage("1d8 + 4 + 1d8"))) // Booming Blade
        .create();

    printStats("Neph - Level 7", attackRoutine);

    attackRoutine = newAttackRoutine()
        .extraDamageOnHit("4d6")
        .attacks(
            attack(a -> a.advantage() // Advantage from Familiar
                .damage("1d8 + 5 + 1d8"))) // Booming Blade
        .create();

    printStats("Neph - Level 8", attackRoutine);

    attackRoutine = newAttackRoutine()
        .extraDamageOnHit("5d6")
        .attacks(
            attack(a -> a.advantage() // Advantage from Familiar
                .damage("1d8 + 5 + 1d8"))) // Booming Blade
        .create();

    printStats("Neph - Level 9 and 10", attackRoutine);

    attackRoutine = newAttackRoutine()
        .extraDamageOnHit("6d6")
        .attacks(
            attack(a -> a.advantage() // Advantage from Familiar
                .damage("1d8 + 5 + 2d8"))) // Booming Blade
        .create();

    printStats("Neph - Level 11 and 12", attackRoutine);

    attackRoutine = newAttackRoutine()
        .extraDamageOnHit("7d6")
        .attacks(
            attack(a -> a.advantage() // Advantage from Familiar
                .damage("1d8 + 5 + 2d8"))) // Booming Blade
        .create();

    printStats("Neph - Level 13 and 14", attackRoutine);

    attackRoutine = newAttackRoutine()
        .extraDamageOnHit("8d6")
        .attacks(
            attack(a -> a.advantage() // Advantage from Familiar
                .damage("1d8 + 5 + 2d8"))) // Booming Blade
        .create();

    printStats("Neph - Level 15 and 16", attackRoutine);

    attackRoutine = newAttackRoutine()
        .extraDamageOnHit("9d6")
        .attacks(
            attack(a -> a.advantage() // Advantage from Familiar
                .damage("1d8 + 5 + 3d8"))) // Booming Blade
        .create();

    printStats("Neph - Level 17 and 18", attackRoutine);

    attackRoutine = newAttackRoutine()
        .extraDamageOnHit("10d6")
        .attacks(
            attack(a -> a.advantage() // Advantage from Familiar
                .damage("1d8 + 5 + 3d8"))) // Booming Blade
        .create();

    printStats("Neph - Level 19 and 20", attackRoutine);

  }

  @Test
  public void test_neph_light_crossbow_dpr() {
    AttackRoutine attackRoutine = newAttackRoutine()
        .extraDamageOnHit("1d6")
        .attacks(
            attack(a -> a
                .damage("1d8 + 3")))
        .create();

    printStats("Neph - Level 1", attackRoutine);

    attackRoutine = newAttackRoutine()
        .extraDamageOnHit("1d6")
        .attacks(
            attack(a -> a.advantage() // Bonus Action Hide or Steady Aim
                .damage("1d8 + 3")))
        .create();

    printStats("Neph - Level 2", attackRoutine);

    attackRoutine = newAttackRoutine()
        .extraDamageOnHit("2d6")
        .attacks(
            attack(a -> a.advantage() // Bonus Action Hide or Steady Aim
                .damage("1d8 + 3")))
        .create();

    printStats("Neph - Level 3", attackRoutine);

    attackRoutine = newAttackRoutine()
        .extraDamageOnHit("2d6")
        .attacks(
            attack(a -> a.advantage() // Bonus Action Hide or Steady Aim
                .damage("1d8 + 4")))
        .create();

    printStats("Neph - Level 4", attackRoutine);

    attackRoutine = newAttackRoutine()
        .extraDamageOnHit("3d6")
        .attacks(
            attack(a -> a.advantage() // Bonus Action Hide or Steady Aim
                .damage("1d8 + 4")))
        .create();

    printStats("Neph - Level 5 and 6", attackRoutine);

    attackRoutine = newAttackRoutine()
        .extraDamageOnHit("4d6")
        .attacks(
            attack(a -> a.advantage() // Bonus Action Hide or Steady Aim
                .damage("1d8 + 4")))
        .create();

    printStats("Neph - Level 7", attackRoutine);

    attackRoutine = newAttackRoutine()
        .extraDamageOnHit("4d6")
        .attacks(
            attack(a -> a.advantage() // Bonus Action Hide or Steady Aim
                .damage("1d8 + 5")))
        .create();

    printStats("Neph - Level 8", attackRoutine);

    attackRoutine = newAttackRoutine()
        .extraDamageOnHit("5d6")
        .attacks(
            attack(a -> a.advantage() // Bonus Action Hide or Steady Aim
                .damage("1d8 + 5")))
        .create();

    printStats("Neph - Level 9 and 10", attackRoutine);

    attackRoutine = newAttackRoutine()
        .extraDamageOnHit("6d6")
        .attacks(
            attack(a -> a.advantage() // Bonus Action Hide or Steady Aim
                .damage("1d8 + 5")))
        .create();

    printStats("Neph - Level 11 and 12", attackRoutine);

    attackRoutine = newAttackRoutine()
        .extraDamageOnHit("7d6")
        .attacks(
            attack(a -> a.advantage() // Bonus Action Hide or Steady Aim
                .damage("1d8 + 5")))
        .create();

    printStats("Neph - Level 13 and 14", attackRoutine);

    attackRoutine = newAttackRoutine()
        .extraDamageOnHit("8d6")
        .attacks(
            attack(a -> a.advantage() // Bonus Action Hide or Steady Aim
                .damage("1d8 + 5")))
        .create();

    printStats("Neph - Level 15 and 16", attackRoutine);

    attackRoutine = newAttackRoutine()
        .extraDamageOnHit("9d6")
        .attacks(
            attack(a -> a.advantage() // Bonus Action Hide or Steady Aim
                .damage("1d8 + 5")))
        .create();

    printStats("Neph - Level 17 and 18", attackRoutine);

    attackRoutine = newAttackRoutine()
        .extraDamageOnHit("10d6")
        .attacks(
            attack(a -> a.advantage() // Bonus Action Hide or Steady Aim
                .damage("1d8 + 5")))
        .create();

    printStats("Neph - Level 19 and 20", attackRoutine);

  }

  @Test
  public void test_neph_twf_dpr() {
    AttackRoutine attackRoutine = newAttackRoutine()
        .extraDamageOnHit("1d6")
        .attacks(
            attack(a -> a
                .damage("1d6 + 3")),
            attack(a -> a
                .damage("1d6")))
        .create();

    printStats("Neph - Level 1", attackRoutine);

    attackRoutine = newAttackRoutine()
        .extraDamageOnHit("2d6")
        .attacks(
            attack(a -> a.advantage() // Advantage from Familiar
                .damage("1d6 + 3")),
            attack(a -> a
                .damage("1d6")))
        .create();

    printStats("Neph - Level 3", attackRoutine);

    attackRoutine = newAttackRoutine()
        .extraDamageOnHit("2d6")
        .attacks(
            attack(a -> a.advantage() // Advantage from Familiar
                .damage("1d6 + 4")),
            attack(a -> a
                .damage("1d6")))
        .create();

    printStats("Neph - Level 4", attackRoutine);

    attackRoutine = newAttackRoutine()
        .extraDamageOnHit("3d6")
        .attacks(
            attack(a -> a.advantage() // Advantage from Familiar
                .damage("1d6 + 4")),
            attack(a -> a
                .damage("1d6")))
        .create();

    printStats("Neph - Level 5 and 6", attackRoutine);

    attackRoutine = newAttackRoutine()
        .extraDamageOnHit("4d6")
        .attacks(
            attack(a -> a.advantage() // Advantage from Familiar
                .damage("1d6 + 4")),
            attack(a -> a
                .damage("1d6")))
        .create();

    printStats("Neph - Level 7", attackRoutine);

    attackRoutine = newAttackRoutine()
        .extraDamageOnHit("4d6")
        .attacks(
            attack(a -> a.advantage() // Advantage from Familiar
                .damage("1d6 + 5")),
            attack(a -> a
                .damage("1d6")))
        .create();

    printStats("Neph - Level 8", attackRoutine);

    attackRoutine = newAttackRoutine()
        .extraDamageOnHit("5d6")
        .attacks(
            attack(a -> a.advantage() // Advantage from Familiar
                .damage("1d6 + 5")),
            attack(a -> a
                .damage("1d6")))
        .create();

    printStats("Neph - Level 9", attackRoutine);

    attackRoutine = newAttackRoutine()
        .extraDamageOnHit("5d6")
        .attacks(
            attack(a -> a.advantage() // Advantage from Familiar
                .damage("1d6 + 5")),
            attack(a -> a
                .damage("1d6 + 5")))
        .create();

    printStats("Neph - Level 10", attackRoutine);

    attackRoutine = newAttackRoutine()
        .extraDamageOnHit("6d6")
        .attacks(
            attack(a -> a.advantage() // Advantage from Familiar
                .damage("1d6 + 5")),
            attack(a -> a
                .damage("1d6 + 5")))
        .create();

    printStats("Neph - Level 11", attackRoutine);

    attackRoutine = newAttackRoutine()
        .extraDamageOnHit("6d6")
        .attacks(
            attack(a -> a.advantage() // Advantage from Familiar
                .damage("1d8 + 5")),
            attack(a -> a
                .damage("1d8 + 5")))
        .create();

    printStats("Neph - Level 12", attackRoutine);

    attackRoutine = newAttackRoutine()
        .extraDamageOnHit("7d6")
        .attacks(
            attack(a -> a.advantage() // Advantage from Familiar
                .damage("1d8 + 5")),
            attack(a -> a
                .damage("1d8 + 5")))
        .create();

    printStats("Neph - Level 13 and 14", attackRoutine);

    attackRoutine = newAttackRoutine()
        .extraDamageOnHit("8d6")
        .attacks(
            attack(a -> a.advantage() // Advantage from Familiar
                .damage("1d8 + 5")),
            attack(a -> a
                .damage("1d8 + 5")))
        .create();

    printStats("Neph - Level 15 and 16", attackRoutine);

    attackRoutine = newAttackRoutine()
        .extraDamageOnHit("9d6")
        .attacks(
            attack(a -> a.advantage() // Advantage from Familiar
                .damage("1d8 + 5")),
            attack(a -> a
                .damage("1d8 + 5")))
        .create();

    printStats("Neph - Level 17 and 18", attackRoutine);

    attackRoutine = newAttackRoutine()
        .extraDamageOnHit("10d6")
        .attacks(
            attack(a -> a.advantage() // Advantage from Familiar
                .damage("1d8 + 5")),
            attack(a -> a
                .damage("1d8 + 5")))
        .create();

    printStats("Neph - Level 19 and 20", attackRoutine);

  }

  @Test
  public void test_neph_light_crossbow_sharpshooter_dpr() {
    AttackRoutine attackRoutine = newAttackRoutine()
        .extraDamageOnHit("1d6")
        .attacks(
            attack(a -> a
                .damage("1d8 + 3")))
        .create();

    printStats("Neph - Level 1", attackRoutine);

    attackRoutine = newAttackRoutine()
        .extraDamageOnHit("1d6")
        .attacks(
            attack(a -> a.advantage() // Bonus Action Hide or Steady Aim
                .damage("1d8 + 3")))
        .create();

    printStats("Neph - Level 2", attackRoutine);

    attackRoutine = newAttackRoutine()
        .extraDamageOnHit("2d6")
        .attacks(
            attack(a -> a.advantage() // Bonus Action Hide or Steady Aim
                .damage("1d8 + 3")))
        .create();

    printStats("Neph - Level 3", attackRoutine);

    attackRoutine = newAttackRoutine()
        .extraDamageOnHit("2d6")
        .attacks(
            attack(a -> a.advantage() // Bonus Action Hide or Steady Aim
                .targetAc(14)
                .damage("1d8 + 13")))
        .create();

    printStats("Neph - Level 4", attackRoutine);

    attackRoutine = newAttackRoutine()
        .extraDamageOnHit("3d6")
        .attacks(
            attack(a -> a.advantage() // Bonus Action Hide or Steady Aim
                .targetAc(14)
                .damage("1d8 + 13")))
        .create();

    printStats("Neph - Level 5 and 6", attackRoutine);

    attackRoutine = newAttackRoutine()
        .extraDamageOnHit("4d6")
        .attacks(
            attack(a -> a.advantage() // Bonus Action Hide or Steady Aim
                .targetAc(14)
                .damage("1d8 + 13")))
        .create();

    printStats("Neph - Level 7", attackRoutine);

    attackRoutine = newAttackRoutine()
        .extraDamageOnHit("4d6")
        .attacks(
            attack(a -> a.advantage() // Bonus Action Hide or Steady Aim
                .targetAc(14)
                .damage("1d8 + 14")))
        .create();

    printStats("Neph - Level 8", attackRoutine);

    attackRoutine = newAttackRoutine()
        .extraDamageOnHit("5d6")
        .attacks(
            attack(a -> a.advantage() // Bonus Action Hide or Steady Aim
                .targetAc(14)
                .damage("1d8 + 14")))
        .create();

    printStats("Neph - Level 9", attackRoutine);

    attackRoutine = newAttackRoutine()
        .extraDamageOnHit("5d6")
        .attacks(
            attack(a -> a.advantage() // Bonus Action Hide or Steady Aim
                .targetAc(14)
                .damage("1d8 + 15")))
        .create();

    printStats("Neph - Level 10", attackRoutine);

    attackRoutine = newAttackRoutine()
        .extraDamageOnHit("6d6")
        .attacks(
            attack(a -> a.advantage() // Bonus Action Hide or Steady Aim
                .targetAc(14)
                .damage("1d8 + 15")))
        .create();

    printStats("Neph - Level 11 and 12", attackRoutine);

    attackRoutine = newAttackRoutine()
        .extraDamageOnHit("7d6")
        .attacks(
            attack(a -> a.advantage() // Bonus Action Hide or Steady Aim
                .targetAc(14)
                .damage("1d8 + 15")))
        .create();

    printStats("Neph - Level 13 and 14", attackRoutine);

    attackRoutine = newAttackRoutine()
        .extraDamageOnHit("8d6")
        .attacks(
            attack(a -> a.advantage() // Bonus Action Hide or Steady Aim
                .targetAc(14)
                .damage("1d8 + 15")))
        .create();

    printStats("Neph - Level 15 and 16", attackRoutine);

    attackRoutine = newAttackRoutine()
        .extraDamageOnHit("9d6")
        .attacks(
            attack(a -> a.advantage() // Bonus Action Hide or Steady Aim
                .targetAc(14)
                .damage("1d8 + 15")))
        .create();

    printStats("Neph - Level 17 and 18", attackRoutine);

    attackRoutine = newAttackRoutine()
        .extraDamageOnHit("10d6")
        .attacks(
            attack(a -> a.advantage() // Bonus Action Hide or Steady Aim
                .targetAc(14)
                .damage("1d8 + 15")))
        .create();

    printStats("Neph - Level 19 and 20", attackRoutine);

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
