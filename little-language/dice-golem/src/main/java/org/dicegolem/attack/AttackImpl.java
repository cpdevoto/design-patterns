package org.dicegolem.attack;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static java.util.Objects.requireNonNull;

import org.dicegolem.Dice;
import org.dicegolem.model.DiceRollExpression;
import org.dicegolem.model.NumericLiteral;

class AttackImpl implements Attack {

  private final Integer targetAc;
  private final DiceRollExpression hitModifierExpression;
  private final int critOn;
  private final boolean advantage;
  private final boolean disadvantage;
  private final boolean elvenAccuracy;
  private final DiceRollExpression damage;


  public static Builder builder(Attack attack) {
    return new Builder(attack);
  }

  public static Builder builder() {
    return new Builder();
  }

  private AttackImpl(Builder builder) {
    this.targetAc = builder.targetAc;
    this.hitModifierExpression = builder.hitModifierExpression;
    this.critOn = builder.critOn;
    this.advantage = builder.advantage;
    this.disadvantage = builder.disadvantage;
    this.elvenAccuracy = builder.elvenAccuracy;
    this.damage = builder.damage;
  }

  @Override
  public double hitProbability() {
    return computeAdjustedHitProbability();
  }

  @Override
  public double critProbability() {
    return computeAdjustedCritProbability();
  }

  @Override
  public double dpr() {
    double hitProbability = hitProbability();
    double critProbability = critProbability();
    double averageDamage = damage.average();
    double averageDamageDiceOnly = damage.averageDiceOnly();
    double hitDpr = hitProbability * averageDamage;
    double critDpr = critProbability * averageDamageDiceOnly;
    return hitDpr + critDpr;
  }

  int getTargetAc() {
    return computeTargetAc();
  }

  double getHitModifier() {
    return computeHitModifier();
  }

  int getCritOn() {
    return critOn;
  }

  boolean getAdvantage() {
    return advantage;
  }

  boolean getDisadvantage() {
    return disadvantage;
  }

  boolean getElvenAccuracy() {
    return elvenAccuracy;
  }

  DiceRollExpression getDamage() {
    return damage;
  }

  private double computeAdjustedHitProbability() {
    double baseHitProbability = computeBaseHitProbability();
    double adjustedHitProbability = baseHitProbability;
    if (advantage) {
      double baseMissProbability = 1 - baseHitProbability;
      double adjustedMissProbability;
      if (elvenAccuracy) {
        adjustedMissProbability = Math.pow(baseMissProbability, 3);
      } else {
        adjustedMissProbability = Math.pow(baseMissProbability, 2);
      }
      adjustedHitProbability = 1 - adjustedMissProbability;
    } else if (disadvantage) {
      adjustedHitProbability = Math.pow(baseHitProbability, 2);
    }
    return adjustedHitProbability;
  }

  private double computeAdjustedCritProbability() {
    double baseHitProbability = computeBaseHitProbability();
    double baseCritProbability = computeBaseCritProbability(baseHitProbability);
    double adjustedCritProbability = baseCritProbability;
    if (advantage) {
      double baseNonCritProbability = 1 - baseCritProbability;
      double adjustedNonCritProbability;
      if (elvenAccuracy) {
        adjustedNonCritProbability = Math.pow(baseNonCritProbability, 3);
      } else {
        adjustedNonCritProbability = Math.pow(baseNonCritProbability, 2);
      }
      adjustedCritProbability = 1 - adjustedNonCritProbability;
    } else if (disadvantage) {
      adjustedCritProbability = Math.pow(baseCritProbability, 2);
    }
    return adjustedCritProbability;
  }

  private double computeBaseHitProbability() {
    return Math.min(0.95, (21 - computeTargetAc() + computeHitModifier()) * 0.05);
  }

  private double computeBaseCritProbability(double baseHitProbability) {
    return Math.min(baseHitProbability, (21 - critOn) * 0.05);
  }

  private double computeHitModifier() {
    double modifier = 0;
    if (hitModifierExpression != null) {
      // Default the target AC such that the hit probability is x
      modifier += hitModifierExpression.average();
    }
    return modifier;
  }

  private int computeTargetAc() {
    Integer ac = this.targetAc;
    if (ac == null) {
      // Default the target AC such that the hit probability is 60%
      ac = Math.max(1, 9 + (int) Math.round(computeHitModifier()));
    }
    return ac;
  }

  static class Builder implements Attack.Builder {
    private Integer targetAc;
    private DiceRollExpression hitModifierExpression;
    private int critOn = 20;
    private boolean advantage = false;
    private boolean disadvantage = false;
    private boolean elvenAccuracy = false;
    private DiceRollExpression damage;

    private Builder(Attack attack) {
      AttackImpl attackImpl =
          AttackImpl.class.cast(requireNonNull(attack, "attack cannot be null"));
      this.targetAc = attackImpl.targetAc;
      this.hitModifierExpression = attackImpl.hitModifierExpression;
      this.critOn = attackImpl.critOn;
      this.advantage = attackImpl.advantage;
      this.disadvantage = attackImpl.disadvantage;
      this.elvenAccuracy = attackImpl.elvenAccuracy;
      this.damage = attackImpl.damage;
    }

    private Builder() {}

    @Override
    public Builder targetAc(int ac) {
      checkArgument(ac > 0, "expected a positive value");
      targetAc = ac;
      return this;
    }

    @Override
    public Builder toHitModifier(int hitModifier) {
      this.hitModifierExpression = new NumericLiteral(hitModifier);
      return this;
    }

    @Override
    public Builder toHitModifier(String hitModifier) {
      requireNonNull(hitModifier, "hitModifier cannot be null");
      this.hitModifierExpression = Dice.parse(hitModifier);
      return this;
    }

    @Override
    public Builder critOn(int critOn) {
      checkArgument(critOn >= 2 && critOn <= 20, "expected a value between 2 and 20");
      this.critOn = critOn;
      return this;
    }

    @Override
    public Builder damage(String damage) {
      requireNonNull(damage, "damage cannot be null");
      this.damage = Dice.parse(damage);
      return this;
    }

    @Override
    public Builder advantage() {
      this.advantage = true;
      return this;
    }

    @Override
    public Builder elvenAccuracy() {
      this.elvenAccuracy = true;
      return this;
    }

    @Override
    public Builder disadvantage() {
      this.disadvantage = true;
      return this;
    }

    AttackImpl build() {
      checkState(damage != null, "damage must be specified");
      if (advantage && disadvantage) {
        advantage = false;
        disadvantage = false;
      }
      return new AttackImpl(this);
    }

  }

}
