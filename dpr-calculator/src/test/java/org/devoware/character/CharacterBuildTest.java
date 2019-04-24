package org.devoware.character;

import static org.devoware.attack.Attack.attack;
import static org.devoware.attack.AttackRoutine.damageOnAnyHit;
import static org.devoware.character.BuildPrinter.printDamageOnHit;
import static org.devoware.character.BuildPrinter.printDpr;
import static org.devoware.character.CharacterBuild.characterBuild;

import org.junit.Test;

public class CharacterBuildTest {

  @Test
  public void test_dpr() {
    CharacterBuild warlock = characterBuild("Eldritch Blast Warlock", build -> {
      build.attackRoutineForLevel(1, attack("1d10 + 1d6"));
      build.attackRoutineForLevel(2, attack("1d10 + 3 + 1d6"));
      build.attackRoutineForLevel(4, attack("1d10 + 4 + 1d6"));
      build.attackRoutineForLevel(5, attack("1d10 + 4 + 1d6"), attack("1d10 + 4 + 1d6"));
      build.attackRoutineForLevel(8, attack("1d10 + 5 + 1d6"), attack("1d10 + 5 + 1d6"));
      build.attackRoutineForLevel(11, attack("1d10 + 5 + 1d6"), attack("1d10 + 5 + 1d6"),
          attack("1d10 + 5 + 1d6"));
      build.attackRoutineForLevel(17, attack("1d10 + 5 + 1d6"), attack("1d10 + 5 + 1d6"),
          attack("1d10 + 5 + 1d6"), attack("1d10 + 5 + 1d6"));
    });

    // This is a half-elf that wields a greatsword, picks up Elven Accuracy at level 4, +2 Str at
    // level 9, +2 Str at level 11, GWM at level 13
    CharacterBuild critFisher = characterBuild("Critfish GWF Barb5 / Fghtr 11 / Barb 9 ", build -> {
      build.attackRoutineForLevel(1, attack("2d6 + 5"));
      build.attackRoutineForLevel(2, attack("2d6 + 5", a -> a.advantage()));
      build.attackRoutineForLevel(4,
          attack("2d6 + 5", a -> a.advantage().elvenAccuracy().hitModifier(-1)));
      build.attackRoutineForLevel(5,
          attack("2d6 + 5", a -> a.advantage().elvenAccuracy().hitModifier(-1)),
          attack("2d6 + 5", a -> a.advantage().elvenAccuracy().hitModifier(-1)));
      build.attackRoutineForLevel(6,
          attack("2d6ro<2 + 5", a -> a.advantage().elvenAccuracy().hitModifier(-1)),
          attack("2d6ro<2 + 5", a -> a.advantage().elvenAccuracy().hitModifier(-1)));
      build.attackRoutineForLevel(8,
          attack("2d6ro<2 + 5", a -> a.advantage().elvenAccuracy().critOn(19).hitModifier(-2)),
          attack("2d6ro<2 + 5", a -> a.advantage().elvenAccuracy().critOn(19).hitModifier(-2)));
      build.attackRoutineForLevel(9,
          attack("2d6ro<2 + 6", a -> a.advantage().elvenAccuracy().critOn(19).hitModifier(-1)),
          attack("2d6ro<2 + 6", a -> a.advantage().elvenAccuracy().critOn(19).hitModifier(-1)));
      build.attackRoutineForLevel(11,
          attack("2d6ro<2 + 7", a -> a.advantage().elvenAccuracy().critOn(19)),
          attack("2d6ro<2 + 7", a -> a.advantage().elvenAccuracy().critOn(19)));
      build.attackRoutineForLevel(13,
          attack("2d6ro<2 + 17", a -> a.advantage().elvenAccuracy().critOn(19).hitModifier(-5)),
          attack("2d6ro<2 + 17", a -> a.advantage().elvenAccuracy().critOn(19).hitModifier(-5)));
      build.attackRoutineForLevel(16,
          attack("2d6ro<2 + 17", a -> a.advantage().elvenAccuracy().critOn(19).hitModifier(-5)),
          attack("2d6ro<2 + 17", a -> a.advantage().elvenAccuracy().critOn(19).hitModifier(-5)),
          attack("2d6ro<2 + 17", a -> a.advantage().elvenAccuracy().critOn(19).hitModifier(-5)));
    });

    // This is a half-elf that wields two scimitars, picks up Elven Accuracy at level 6, +2 Str at
    // level 11, +2 Str at level 15
    CharacterBuild critFisher2 =
        characterBuild("Critfish TWF Barb2 / Fghtr 5 / Rogue 13 ", build -> {
          build.attackRoutineForLevel(1,
              attack("1d6 + 5"),
              attack("1d6 + 2"));
          build.attackRoutineForLevel(2,
              attack("1d6 + 5", a -> a.advantage()),
              attack("1d6 + 2", a -> a.advantage()));
          build.attackRoutineForLevel(3,
              attack("1d6 + 5", a -> a.advantage()),
              attack("1d6 + 5", a -> a.advantage()));
          build.attackRoutineForLevel(4,
              attack("1d6 + 5", a -> a.advantage().hitModifier(-1)),
              attack("1d6 + 5", a -> a.advantage().hitModifier(-1)));
          build.attackRoutineForLevel(5,
              attack("1d6 + 5", a -> a.advantage().critOn(19).hitModifier(-1)),
              attack("1d6 + 5", a -> a.advantage().critOn(19).hitModifier(-1)));
          build.attackRoutineForLevel(6,
              attack("1d6 + 5", a -> a.advantage().critOn(19).elvenAccuracy().hitModifier(-1)),
              attack("1d6 + 5", a -> a.advantage().critOn(19).elvenAccuracy().hitModifier(-1)));
          build.attackRoutineForLevel(7,
              attack("1d6 + 5", a -> a.advantage().critOn(19).elvenAccuracy().hitModifier(-1)),
              attack("1d6 + 5", a -> a.advantage().critOn(19).elvenAccuracy().hitModifier(-1)),
              attack("1d6 + 5", a -> a.advantage().critOn(19).elvenAccuracy().hitModifier(-1)));
          build.attackRoutineForLevel(8, damageOnAnyHit("1d6"),
              attack("1d6 + 5", a -> a.advantage().critOn(19).elvenAccuracy().hitModifier(-2)),
              attack("1d6 + 5", a -> a.advantage().critOn(19).elvenAccuracy().hitModifier(-2)),
              attack("1d6 + 5", a -> a.advantage().critOn(19).elvenAccuracy().hitModifier(-2)));
          build.attackRoutineForLevel(10, damageOnAnyHit("2d6"),
              attack("1d6 + 5", a -> a.advantage().critOn(19).elvenAccuracy().hitModifier(-2)),
              attack("1d6 + 5", a -> a.advantage().critOn(19).elvenAccuracy().hitModifier(-2)),
              attack("1d6 + 5", a -> a.advantage().critOn(19).elvenAccuracy().hitModifier(-2)));
          build.attackRoutineForLevel(11, damageOnAnyHit("2d6"),
              attack("1d6 + 5", a -> a.advantage().critOn(19).elvenAccuracy().hitModifier(-1)),
              attack("1d6 + 5", a -> a.advantage().critOn(19).elvenAccuracy().hitModifier(-1)),
              attack("1d6 + 5", a -> a.advantage().critOn(19).elvenAccuracy().hitModifier(-1)));
          build.attackRoutineForLevel(12, damageOnAnyHit("3d6"),
              attack("1d6 + 5", a -> a.advantage().critOn(19).elvenAccuracy().hitModifier(-1)),
              attack("1d6 + 5", a -> a.advantage().critOn(19).elvenAccuracy().hitModifier(-1)),
              attack("1d6 + 5", a -> a.advantage().critOn(19).elvenAccuracy().hitModifier(-1)));
          build.attackRoutineForLevel(14, damageOnAnyHit("4d6"),
              attack("1d6 + 5", a -> a.advantage().critOn(19).elvenAccuracy().hitModifier(-1)),
              attack("1d6 + 5", a -> a.advantage().critOn(19).elvenAccuracy().hitModifier(-1)),
              attack("1d6 + 5", a -> a.advantage().critOn(19).elvenAccuracy().hitModifier(-1)));
          build.attackRoutineForLevel(15, damageOnAnyHit("4d6"),
              attack("1d6 + 5", a -> a.advantage().critOn(19).elvenAccuracy()),
              attack("1d6 + 5", a -> a.advantage().critOn(19).elvenAccuracy()),
              attack("1d6 + 5", a -> a.advantage().critOn(19).elvenAccuracy()));
          build.attackRoutineForLevel(16, damageOnAnyHit("5d6"),
              attack("1d6 + 5", a -> a.advantage().critOn(19).elvenAccuracy()),
              attack("1d6 + 5", a -> a.advantage().critOn(19).elvenAccuracy()),
              attack("1d6 + 5", a -> a.advantage().critOn(19).elvenAccuracy()));
          build.attackRoutineForLevel(18, damageOnAnyHit("6d6"),
              attack("1d6 + 5", a -> a.advantage().critOn(19).elvenAccuracy()),
              attack("1d6 + 5", a -> a.advantage().critOn(19).elvenAccuracy()),
              attack("1d6 + 5", a -> a.advantage().critOn(19).elvenAccuracy()));
          build.attackRoutineForLevel(20, damageOnAnyHit("7d6"),
              attack("1d6 + 5", a -> a.advantage().critOn(19).elvenAccuracy()),
              attack("1d6 + 5", a -> a.advantage().critOn(19).elvenAccuracy()),
              attack("1d6 + 5", a -> a.advantage().critOn(19).elvenAccuracy()));
        });

    CharacterBuild rogue =
        characterBuild("Rogue 20 TWF", build -> {
          build.attackRoutineForLevel(1, damageOnAnyHit("1d6"),
              attack("1d6 + 3"),
              attack("1d6"));
          build.attackRoutineForLevel(3, damageOnAnyHit("2d6"),
              attack("1d6 + 3"),
              attack("1d6"));
          build.attackRoutineForLevel(4, damageOnAnyHit("2d6"),
              attack("1d6 + 4"),
              attack("1d6"));
          build.attackRoutineForLevel(5, damageOnAnyHit("3d6"),
              attack("1d6 + 4"),
              attack("1d6"));
          build.attackRoutineForLevel(7, damageOnAnyHit("4d6"),
              attack("1d6 + 4"),
              attack("1d6"));
          build.attackRoutineForLevel(8, damageOnAnyHit("4d6"),
              attack("1d6 + 5"),
              attack("1d6"));
          build.attackRoutineForLevel(9, damageOnAnyHit("5d6"),
              attack("1d6 + 5"),
              attack("1d6"));
          build.attackRoutineForLevel(11, damageOnAnyHit("6d6"),
              attack("1d6 + 5"),
              attack("1d6"));
          build.attackRoutineForLevel(13, damageOnAnyHit("7d6"),
              attack("1d6 + 5"),
              attack("1d6"));
          build.attackRoutineForLevel(15, damageOnAnyHit("8d6"),
              attack("1d6 + 5"),
              attack("1d6"));
          build.attackRoutineForLevel(17, damageOnAnyHit("9d6"),
              attack("1d6 + 5"),
              attack("1d6"));
          build.attackRoutineForLevel(19, damageOnAnyHit("10d6"),
              attack("1d6 + 5"),
              attack("1d6"));
        });

    CharacterBuild rogue2 =
        characterBuild("Rogue 20 Booming Blade", build -> {
          build.attackRoutineForLevel(1, damageOnAnyHit("1d6"),
              attack("1d8 + 3"));
          build.attackRoutineForLevel(3, damageOnAnyHit("2d6"),
              attack("1d8 + 3"));
          build.attackRoutineForLevel(4, damageOnAnyHit("2d6"),
              attack("1d8 + 4"));
          build.attackRoutineForLevel(5, damageOnAnyHit("3d6"),
              attack("1d8 + 1d8 + 4"));
          build.attackRoutineForLevel(7, damageOnAnyHit("4d6"),
              attack("1d8 + 1d8 + 4"));
          build.attackRoutineForLevel(8, damageOnAnyHit("4d6"),
              attack("1d8 + 1d8 + 5"));
          build.attackRoutineForLevel(9, damageOnAnyHit("5d6"),
              attack("1d8 + 1d8 + 5"));
          build.attackRoutineForLevel(11, damageOnAnyHit("6d6"),
              attack("1d8 + 2d8 + 5"));
          build.attackRoutineForLevel(13, damageOnAnyHit("7d6"),
              attack("1d8 + 2d8 + 5"));
          build.attackRoutineForLevel(15, damageOnAnyHit("8d6"),
              attack("1d8 + 2d8 + 5"));
          build.attackRoutineForLevel(17, damageOnAnyHit("9d6"),
              attack("1d8 + 3d8 + 5"));
          build.attackRoutineForLevel(19, damageOnAnyHit("10d6"),
              attack("1d8 + 3d8 + 5"));
        });

    CharacterBuild rangerRogue =
        characterBuild("Ranger 5 / Rogue 15 TWF", build -> {
          build.attackRoutineForLevel(1,
              attack("1d6 + 3"),
              attack("1d6"));
          build.attackRoutineForLevel(2,
              attack("1d6 + 3 + 1d6"),
              attack("1d6 + 3 + 1d6"));
          build.attackRoutineForLevel(4,
              attack("1d6 + 4 + 1d6"),
              attack("1d6 + 4 + 1d6"));
          build.attackRoutineForLevel(5,
              attack("1d6 + 4 + 1d6"),
              attack("1d6 + 4 + 1d6"),
              attack("1d6 + 4 + 1d6"));
          build.attackRoutineForLevel(6, damageOnAnyHit("1d6"),
              attack("1d6 + 4 + 1d6"),
              attack("1d6 + 4 + 1d6"),
              attack("1d6 + 4 + 1d6"));
          build.attackRoutineForLevel(8, damageOnAnyHit("2d6"),
              attack("1d6 + 5 + 1d6"),
              attack("1d6 + 5 + 1d6"),
              attack("1d6 + 5 + 1d6"));
          build.attackRoutineForLevel(10, damageOnAnyHit("3d6"),
              attack("1d6 + 5 + 1d6"),
              attack("1d6 + 5 + 1d6"),
              attack("1d6 + 5 + 1d6"));
          build.attackRoutineForLevel(12, damageOnAnyHit("4d6"),
              attack("1d6 + 5 + 1d6"),
              attack("1d6 + 5 + 1d6"),
              attack("1d6 + 5 + 1d6"));
          build.attackRoutineForLevel(14, damageOnAnyHit("5d6"),
              attack("1d6 + 5 + 1d6"),
              attack("1d6 + 5 + 1d6"),
              attack("1d6 + 5 + 1d6"));
          build.attackRoutineForLevel(16, damageOnAnyHit("6d6"),
              attack("1d6 + 5 + 1d6"),
              attack("1d6 + 5 + 1d6"),
              attack("1d6 + 5 + 1d6"));
          build.attackRoutineForLevel(18, damageOnAnyHit("7d6"),
              attack("1d6 + 5 + 1d6"),
              attack("1d6 + 5 + 1d6"),
              attack("1d6 + 5 + 1d6"));
          build.attackRoutineForLevel(20, damageOnAnyHit("8d6"),
              attack("1d6 + 5 + 1d6"),
              attack("1d6 + 5 + 1d6"),
              attack("1d6 + 5 + 1d6"));
        });

    CharacterBuild archer =
        characterBuild("Fighter 12 / Rogue 8 Archer", build -> {
          build.attackRoutineForLevel(1,
              attack("1d8 + 3", a -> a.hitModifier(2)));
          build.attackRoutineForLevel(2,
              attack("1d10 + 3", a -> a.hitModifier(2)));
          build.attackRoutineForLevel(4,
              attack("1d10 + 13", a -> a.hitModifier(-4)));
          build.attackRoutineForLevel(5,
              attack("1d8 + 13", a -> a.hitModifier(-4)),
              attack("1d8 + 13", a -> a.hitModifier(-4)));
          build.attackRoutineForLevel(6,
              attack("1d6 + 13", a -> a.hitModifier(-4)),
              attack("1d6 + 13", a -> a.hitModifier(-4)),
              attack("1d6 + 13", a -> a.hitModifier(-4)));
          build.attackRoutineForLevel(8,
              attack("1d6 + 14", a -> a.hitModifier(-4)),
              attack("1d6 + 14", a -> a.hitModifier(-4)),
              attack("1d6 + 14", a -> a.hitModifier(-4)));
          build.attackRoutineForLevel(11,
              attack("1d6 + 14", a -> a.hitModifier(-4)),
              attack("1d6 + 14", a -> a.hitModifier(-4)),
              attack("1d6 + 14", a -> a.hitModifier(-4)),
              attack("1d6 + 14", a -> a.hitModifier(-4)));
          build.attackRoutineForLevel(12,
              attack("1d6 + 15", a -> a.hitModifier(-3)),
              attack("1d6 + 15", a -> a.hitModifier(-3)),
              attack("1d6 + 15", a -> a.hitModifier(-3)),
              attack("1d6 + 15", a -> a.hitModifier(-3)));
          build.attackRoutineForLevel(13, damageOnAnyHit("1d6"),
              attack("1d6 + 15", a -> a.hitModifier(-3)),
              attack("1d6 + 15", a -> a.hitModifier(-3)),
              attack("1d6 + 15", a -> a.hitModifier(-3)),
              attack("1d6 + 15", a -> a.hitModifier(-3)));
          build.attackRoutineForLevel(15, damageOnAnyHit("2d6"),
              attack("1d6 + 15", a -> a.hitModifier(-3)),
              attack("1d6 + 15", a -> a.hitModifier(-3)),
              attack("1d6 + 15", a -> a.hitModifier(-3)),
              attack("1d6 + 15", a -> a.hitModifier(-3)));
          build.attackRoutineForLevel(17, damageOnAnyHit("3d6"),
              attack("1d6 + 15", a -> a.hitModifier(-3)),
              attack("1d6 + 15", a -> a.hitModifier(-3)),
              attack("1d6 + 15", a -> a.hitModifier(-3)),
              attack("1d6 + 15", a -> a.hitModifier(-3)));
          build.attackRoutineForLevel(19, damageOnAnyHit("4d6"),
              attack("1d6 + 15", a -> a.hitModifier(-3)),
              attack("1d6 + 15", a -> a.hitModifier(-3)),
              attack("1d6 + 15", a -> a.hitModifier(-3)),
              attack("1d6 + 15", a -> a.hitModifier(-3)));
        });

    System.out.println(printDpr(warlock, critFisher, critFisher2));
    System.out.println();
    System.out.println(printDpr(warlock, rogue, rogue2, rangerRogue, archer));
    System.out.println();
    System.out.println(printDpr(critFisher2, rangerRogue));
    System.out.println(printDamageOnHit(warlock, rangerRogue));

    System.out.println(printDpr(rogue));
    System.out.println(printDpr(rogue2));

    CharacterBuild rogue3 =
        characterBuild("Rogue 20 TWF and Dual Wielder", build -> {
          build.attackRoutineForLevel(1, damageOnAnyHit("1d6"),
              attack("1d8 + 3"),
              attack("1d8"));
          build.attackRoutineForLevel(3, damageOnAnyHit("2d6"),
              attack("1d8 + 3"),
              attack("1d8"));
          build.attackRoutineForLevel(4, damageOnAnyHit("2d6"),
              attack("1d8 + 4"),
              attack("1d8"));
          build.attackRoutineForLevel(5, damageOnAnyHit("3d6"),
              attack("1d8 + 4"),
              attack("1d8"));
          build.attackRoutineForLevel(7, damageOnAnyHit("4d6"),
              attack("1d8 + 4"),
              attack("1d8"));
          build.attackRoutineForLevel(8, damageOnAnyHit("4d6"),
              attack("1d8 + 5"),
              attack("1d8"));
          build.attackRoutineForLevel(9, damageOnAnyHit("5d6"),
              attack("1d8 + 5"),
              attack("1d8"));
          build.attackRoutineForLevel(11, damageOnAnyHit("6d6"),
              attack("1d8 + 5"),
              attack("1d8"));
          build.attackRoutineForLevel(13, damageOnAnyHit("7d6"),
              attack("1d8 + 5"),
              attack("1d8"));
          build.attackRoutineForLevel(15, damageOnAnyHit("8d6"),
              attack("1d8 + 5"),
              attack("1d8"));
          build.attackRoutineForLevel(17, damageOnAnyHit("9d6"),
              attack("1d8 + 5"),
              attack("1d8"));
          build.attackRoutineForLevel(19, damageOnAnyHit("10d6"),
              attack("1d8 + 5"),
              attack("1d8"));
        });

    System.out.println(printDpr(rogue, rogue3));
    System.out.println(printDpr(true, rogue, rogue3));

  }

}
