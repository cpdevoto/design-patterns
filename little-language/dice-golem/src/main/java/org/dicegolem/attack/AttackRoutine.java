package org.dicegolem.attack;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static java.util.Objects.requireNonNull;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import org.dicegolem.Dice;
import org.dicegolem.model.DiceRollExpression;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class AttackRoutine implements AttackStatGenerator {

  private final DiceRollExpression extraDamageOnHit;
  private final DiceRollExpression extraDamageOnCrit;
  private final Attack extraAttackOnHit;
  private final Attack extraAttackOnCrit;
  private final List<Attack> attacks;

  public static Builder newAttackRoutine() {
    return new Builder();
  }

  public static Attack attack(Attack attackToClone, Consumer<Attack.Builder> consumer) {
    return Attack.attack(attackToClone, consumer);
  }

  public static Attack attack(Consumer<Attack.Builder> consumer) {
    return Attack.attack(consumer);
  }

  private AttackRoutine(Builder builder) {
    this.extraAttackOnHit = builder.extraAttackOnHit;
    this.extraAttackOnCrit = builder.extraAttackOnCrit;
    this.extraDamageOnHit = builder.extraDamageOnHit;
    this.extraDamageOnCrit = builder.extraDamageOnCrit;
    this.attacks = ImmutableList.copyOf(builder.attacks);
  }

  @Override
  public double dpr() {
    double dpr = 0;
    for (Attack attack : attacks) {
      dpr += attack.dpr();
    }

    if (extraAttackOnHit != null) {
      dpr += hitProbability() * extraAttackOnHit.dpr();
    }
    if (extraAttackOnCrit != null) {
      dpr += critProbability() * extraAttackOnCrit.dpr();
    }
    if (extraDamageOnHit != null) {
      dpr += hitProbability() * extraDamageOnHit.average();
    }
    if (extraDamageOnCrit != null) {
      dpr += critProbability() * extraDamageOnCrit.average();
    }

    return dpr;
  }

  @Override
  public double hitProbability() {
    return computeHitProbability();
  }

  @Override
  public double critProbability() {
    return computeCritProbability();
  }

  private double computeHitProbability() {
    double missProbability = 0;
    boolean firstLoop = true;
    for (Attack attack : attacks) {
      if (firstLoop) {
        firstLoop = false;
        missProbability = 1 - attack.hitProbability();
      } else {
        missProbability *= 1 - attack.hitProbability();
      }
    }
    return 1 - missProbability;
  }

  private double computeCritProbability() {
    double nonCritProbability = 0;
    boolean firstLoop = true;
    for (Attack attack : attacks) {
      if (firstLoop) {
        firstLoop = false;
        nonCritProbability = 1 - attack.critProbability();
      } else {
        nonCritProbability *= 1 - attack.critProbability();
      }
    }
    return 1 - nonCritProbability;
  }

  public static class Builder {
    private DiceRollExpression extraDamageOnHit;
    private DiceRollExpression extraDamageOnCrit;
    private Attack extraAttackOnHit;
    private Attack extraAttackOnCrit;
    private List<Attack> attacks = Lists.newArrayList();

    public Builder extraDamageOnHit(String damage) {
      requireNonNull(damage, "damage cannot be null");
      this.extraDamageOnHit = Dice.parse(damage);
      return this;
    }

    public Builder extraDamageOnCrit(String damage) {
      requireNonNull(damage, "damage cannot be null");
      this.extraDamageOnCrit = Dice.parse(damage);
      return this;
    }

    public Builder extraAttackOnHit(Attack attack) {
      this.extraAttackOnHit = requireNonNull(attack, "attack cannot be null");
      return this;
    }

    public Builder extraAttackOnCrit(Attack attack) {
      this.extraAttackOnCrit = requireNonNull(attack, "attack cannot be null");
      return this;
    }

    public Builder attacks(Attack... attacks) {
      checkArgument(attacks.length > 0, "expected at least one attack");
      Arrays.stream(attacks)
          .forEach(this.attacks::add);
      return this;
    }

    public AttackRoutine create() {
      checkState(attacks.size() > 0, "expected at least one attack");
      return new AttackRoutine(this);
    }
  }
}
