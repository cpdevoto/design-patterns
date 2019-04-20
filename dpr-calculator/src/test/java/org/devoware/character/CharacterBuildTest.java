package org.devoware.character;

import static org.devoware.attack.Attack.attack;
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

    CharacterBuild critFisher = characterBuild("Critfish Barb5 / Fghtr 11 / Barb 9 ", build -> {
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

    System.out.println(printDpr(warlock, critFisher));
  }

}
