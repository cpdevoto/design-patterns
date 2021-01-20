package org.dicegolem.attack;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;
import static org.dicegolem.attack.Attack.attack;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class AttackTest {

  @Nested
  @DisplayName("Construction")
  class Construction {

    @Test
    public void test_damage_is_only_required_input() {
      Attack attack = attack(a -> a.damage("1d6"));
      assertThat(attack).isNotNull();

      assertThatThrownBy(() -> {
        attack(a -> {
          // Don't set any attributes!
        });
      }).isInstanceOf(IllegalStateException.class)
          .hasMessageContaining("damage must be specified");
    }

    @Test
    public void test_setters() {
      AttackImpl attack = AttackImpl.class.cast(attack(a -> a.damage("1d6")
          .toHitModifier(5)
          .targetAc(18)
          .critOn(19)
          .advantage()
          .elvenAccuracy()));

      assertThat(attack).isNotNull();
      assertThat(attack.getDamage()).isNotNull();
      assertThat(attack.getHitModifier()).isEqualTo(5);
      assertThat(attack.getCritOn()).isEqualTo(19);
      assertThat(attack.getTargetAc()).isEqualTo(18);
      assertThat(attack.getAdvantage()).isEqualTo(true);
      assertThat(attack.getElvenAccuracy()).isEqualTo(true);

      attack = AttackImpl.class.cast(attack(a -> a.damage("1d6")
          .disadvantage()));

      assertThat(attack.getDisadvantage()).isEqualTo(true);
    }

    @Test
    public void test_invalid_crit_on() {
      assertThatThrownBy(() -> {
        attack(a -> a.damage("1d6")
            .critOn(21));
      }).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("expected a value between 2 and 20");

      assertThatThrownBy(() -> {
        attack(a -> a.damage("1d6")
            .critOn(1));
      }).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("expected a value between 2 and 20");

    }

    @Test
    public void test_invalid_target_ac() {
      assertThatThrownBy(() -> {
        attack(a -> a.damage("1d6")
            .targetAc(0));
      }).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("expected a positive value");

    }

    @Test
    public void test_target_ac_defaults() {
      // Target AC for attack with no hit modifier is 9
      AttackImpl attack = AttackImpl.class.cast(attack(a -> {
        a.damage("1d6");
      }));

      assertThat(attack.getTargetAc()).isEqualTo(9);

      // Target AC for attack with a hit modifier of 5 is 14
      attack = AttackImpl.class.cast(attack(attack, a -> {
        a.toHitModifier(5);
      }));

      assertThat(attack.getTargetAc()).isEqualTo(14);

      // Target AC for attack with a hit modifier of -5 is 4
      attack = AttackImpl.class.cast(attack(attack, a -> {
        a.toHitModifier(-5);
      }));

      assertThat(attack.getTargetAc()).isEqualTo(4);

      // Target AC for attack with a hit modifier of -20 is 1, since AC cannot go below 1!
      attack = AttackImpl.class.cast(attack(attack, a -> {
        a.toHitModifier(-20);
      }));

      assertThat(attack.getTargetAc()).isEqualTo(1);

    }

    @Test
    public void test_advantage_defaults() {
      AttackImpl attack = AttackImpl.class.cast(attack(a -> {
        a.damage("1d6");
      }));

      assertThat(attack.getAdvantage()).isEqualTo(false);
    }

    @Test
    public void test_disadvantage_defaults() {
      AttackImpl attack = AttackImpl.class.cast(attack(a -> {
        a.damage("1d6");
      }));

      assertThat(attack.getDisadvantage()).isEqualTo(false);
    }

    @Test
    public void test_elven_accuracy_defaults() {
      AttackImpl attack = AttackImpl.class.cast(attack(a -> {
        a.damage("1d6");
      }));

      assertThat(attack.getElvenAccuracy()).isEqualTo(false);
    }

    @Test
    public void test_to_hit_modifier_defaults() {
      AttackImpl attack = AttackImpl.class.cast(attack(a -> {
        a.damage("1d6");
      }));

      assertThat(attack.getHitModifier()).isEqualTo(0);

    }

    @Test
    public void test_crit_on_modifier_defaults() {
      AttackImpl attack = AttackImpl.class.cast(attack(a -> {
        a.damage("1d6");
      }));

      assertThat(attack.getCritOn()).isEqualTo(20);
    }

    @Test
    public void test_advantage_and_disadvantage_cancel_each_other() {
      // Target AC for attack with no hit modifier is 9
      AttackImpl attack = AttackImpl.class.cast(attack(a -> {
        a.damage("1d6")
            .advantage()
            .disadvantage();
      }));

      assertThat(attack.getAdvantage()).isEqualTo(false);
      assertThat(attack.getDisadvantage()).isEqualTo(false);
    }
  }


  @Nested
  @DisplayName("hitProbability")
  class HitProbability {

    @Test
    public void test_default_hit_probability() {
      double hitProbability = attack(a -> a.damage("1d6"))
          .hitProbability();
      assertThat(hitProbability).isCloseTo(0.60, within(0.01));
    }

    @Test
    public void test_simple_hit_probability() {
      double hitProbability = attack(a -> a.damage("1d6")
          .toHitModifier(5)
          .targetAc(18))
              .hitProbability();
      assertThat(hitProbability).isCloseTo(0.40, within(0.01));
    }

    @Test
    public void test_hit_probability_with_advantage() {
      double hitProbability = attack(a -> a.damage("1d6")
          .toHitModifier(5)
          .targetAc(18)
          .advantage())
              .hitProbability();
      assertThat(hitProbability).isCloseTo(0.64, within(0.01));
    }

    @Test
    public void test_hit_probability_with_elven_accuracy() {
      double hitProbability = attack(a -> a.damage("1d6")
          .toHitModifier(5)
          .targetAc(18)
          .advantage()
          .elvenAccuracy())
              .hitProbability();
      assertThat(hitProbability).isCloseTo(0.784, within(0.001));
    }

    @Test
    public void test_hit_probability_with_disadvantage() {
      double hitProbability = attack(a -> a.damage("1d6")
          .toHitModifier(5)
          .targetAc(18)
          .disadvantage())
              .hitProbability();
      assertThat(hitProbability).isCloseTo(0.16, within(0.01));
    }
  }

  @Nested
  @DisplayName("critProbability")
  class CritProbability {

    @Test
    public void test_default_crit_probability() {
      double critProbability = attack(a -> a.damage("1d6"))
          .critProbability();
      assertThat(critProbability).isCloseTo(0.05, within(0.01));
    }

    @Test
    public void test_simple_crit_probability() {
      double critProbability = attack(a -> a.damage("1d6")
          .critOn(19)
          .toHitModifier(5)
          .targetAc(18))
              .critProbability();
      assertThat(critProbability).isCloseTo(0.10, within(0.01));
    }

    @Test
    public void test_crit_probability_with_advantage() {
      double critProbability = attack(a -> a.damage("1d6")
          .critOn(19)
          .toHitModifier(5)
          .targetAc(18)
          .advantage())
              .critProbability();
      assertThat(critProbability).isCloseTo(0.19, within(0.01));
    }

    @Test
    public void test_crit_probability_with_elven_accuracy() {
      double critProbability = attack(a -> a.damage("1d6")
          .critOn(19)
          .toHitModifier(5)
          .targetAc(18)
          .advantage()
          .elvenAccuracy())
              .critProbability();
      assertThat(critProbability).isCloseTo(0.271, within(0.001));
    }

    @Test
    public void test_crit_probability_with_disadvantage() {
      double critProbability = attack(a -> a.damage("1d6")
          .critOn(19)
          .toHitModifier(5)
          .targetAc(18)
          .disadvantage())
              .critProbability();
      assertThat(critProbability).isCloseTo(0.01, within(0.001));
    }

    @Test
    public void test_crit_probability_greater_than_or_equal_to_hit_probability() {
      double critProbability = attack(a -> a.damage("1d6")
          .critOn(2)
          .toHitModifier(5)
          .targetAc(18))
              .critProbability();
      assertThat(critProbability).isCloseTo(0.40, within(0.01));

    }
  }

  @Nested
  @DisplayName("dpr")
  class Dpr {
    @Test
    public void test_dpr() {
      double expectedDpr = 0.4 * 6.5 + 0.05 * 3.5;

      double actualDpr = attack(a -> a.damage("1d6 + 3")
          .toHitModifier(5)
          .targetAc(18))
              .dpr();
      assertThat(actualDpr).isCloseTo(expectedDpr, within(0.001));

    }
  }
}
