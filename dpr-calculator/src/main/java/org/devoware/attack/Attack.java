package org.devoware.attack;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.util.function.Consumer;

import org.devoware.dice.Dice;
import org.devoware.dice.DieRollExpression;

public class Attack {
  private static enum Type {
    ADVANTAGE, DISADVANTAGE, NORMAL
  }

  private final DieRollExpression expression;
  private final Type type;
  private final double hitProbability;
  private final double critProbability;

  public static Attack attack(String expression) {
    return new Builder(expression).build();
  }

  public static Attack attack(String expression, Consumer<Attack.Builder> consumer) {
    Builder builder = new Builder(expression);
    consumer.accept(builder);
    return builder.build();
  }

  private Attack(Builder builder) {
    this.expression = builder.expression;
    this.type = builder.type;
    double hitProbability = builder.baseHitProbability;
    double missProbability = 1 - hitProbability;
    switch (this.type) {
      case ADVANTAGE:
        this.hitProbability = 1 - (missProbability * missProbability *
            (builder.elvenAccuracy ? missProbability : 1));
        break;
      case DISADVANTAGE:
        this.hitProbability = hitProbability * hitProbability;
        break;
      default:
        this.hitProbability = hitProbability;
    }
    double critProbability = Math.min(this.hitProbability, (21 - builder.critOn) * 0.05);
    double nonCritProbability = 1 - critProbability;
    switch (this.type) {
      case ADVANTAGE:
        this.critProbability = 1 - (nonCritProbability * nonCritProbability);
        break;
      case DISADVANTAGE:
        this.critProbability = critProbability * critProbability;
        break;
      default:
        this.critProbability = critProbability;
    }
  }

  public double damageOnHit() {
    return expression.dpr();
  }

  public double dpr() {
    double hitDpr = expression.dpr();
    double critDpr = expression.getDice().stream().mapToDouble(Dice::dpr).sum();
    double hitDamage = hitProbability * hitDpr;
    double critDamage = critProbability * critDpr;
    return hitDamage + critDamage;
  }

  public static class Builder {
    private final DieRollExpression expression;
    private Type type = Type.NORMAL;
    private int critOn = 20;
    private double baseHitProbability = 0.60;
    private boolean elvenAccuracy = false;

    private Builder(String expression) {
      requireNonNull(expression, "expression cannot be null");
      this.expression = Dice.parse(expression);
    }

    public Builder advantage() {
      this.type = Type.ADVANTAGE;
      return this;
    }

    public Builder disadvantage() {
      this.type = Type.DISADVANTAGE;
      return this;
    }

    public Builder normal() {
      this.type = Type.NORMAL;
      return this;
    }

    public Builder elvenAccuracy() {
      this.elvenAccuracy = true;
      return this;
    }

    public Builder hitModifier(int modifier) {
      this.baseHitProbability =
          Math.min(0.95, Math.max(0.0, this.baseHitProbability + (modifier * 0.05)));
      return this;
    }

    public Builder critOn(int value) {
      checkArgument(value > 1 && value < 21, "value must be between 2 and 20");
      this.critOn = value;
      return this;
    }

    Attack build() {
      return new Attack(this);
    }

  }

}
