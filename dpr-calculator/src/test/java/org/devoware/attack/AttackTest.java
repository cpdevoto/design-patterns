package org.devoware.attack;

import static org.devoware.attack.Attack.attack;
import static org.hamcrest.number.IsCloseTo.closeTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class AttackTest {


  @Test
  public void test() {
    Attack attack = attack("1d10 + 5");
    assertThat(attack.dpr(), closeTo(6.575, 0.001));

    attack = attack("1d10 + 5", a -> a.critOn(19));
    assertThat(attack.dpr(), closeTo(6.85, 0.001));

    attack = attack("1d10 + 5", a -> a.hitModifier(2));
    assertThat(attack.dpr(), closeTo(7.625, 0.001));

    attack = attack("1d10 + 5", a -> a.additionalCritDamage("1d10"));
    assertThat(attack.dpr(), closeTo(6.85, 0.001));

    attack = attack("1d10 + 5", a -> a.additionalCritDamage(attack("1d10 + 5")));
    assertThat(attack.dpr(), closeTo(6.904, 0.001));

    attack = attack("1d10 + 5", a -> a.advantage());
    assertThat(attack.dpr(), closeTo(9.356, 0.001));

    attack = attack("1d10 + 5", a -> a.advantage().critOn(19));
    assertThat(attack.dpr(), closeTo(9.865, 0.001));

    attack = attack("1d10 + 5", a -> a.advantage().hitModifier(2));
    assertThat(attack.dpr(), closeTo(10.091, 0.001));

    attack = attack("1d10 + 5", a -> a.advantage().elvenAccuracy());
    assertThat(attack.dpr(), closeTo(10.364, 0.001));

    attack = attack("1d10 + 5", a -> a.disadvantage());
    assertThat(attack.dpr(), closeTo(3.794, 0.001));

    attack = attack("1d10 + 5", a -> a.disadvantage().critOn(19));
    assertThat(attack.dpr(), closeTo(3.835, 0.001));

    attack = attack("1d10 + 5", a -> a.disadvantage().hitModifier(2));
    assertThat(attack.dpr(), closeTo(5.159, 0.001));

    attack = attack("2d6 + 5");
    assertThat(attack.dpr(), closeTo(7.550, 0.001));

    attack = attack("2d6 + 5", a -> a.advantage());
    assertThat(attack.dpr(), closeTo(10.763, 0.001));

    attack = attack("2d6ro<2 + 5", a -> a.advantage());
    assertThat(attack.dpr(), closeTo(12.0125, 0.001));

    attack = attack("1d8");
    assertThat(attack.dpr(), closeTo(2.925, 0.001));

    attack = attack("1d8nc");
    assertThat(attack.dpr(), closeTo(2.700, 0.001));

    attack = attack("0.5 * 1d8nc");
    assertThat(attack.dpr(), closeTo(1.350, 0.001));

  }

  @Test
  public void test_damage_on_hit() {
    Attack attack = attack("1d10 + 1d6");
    assertThat(attack.damageOnHit(), closeTo(9, 0.001));

    attack = attack("1d10 + 3 + 1d6");
    assertThat(attack.damageOnHit(), closeTo(12, 0.001));
  }
}
