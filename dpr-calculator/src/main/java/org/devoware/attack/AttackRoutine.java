package org.devoware.attack;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;
import static org.devoware.attack.Attack.attack;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.devoware.dice.Dice;
import org.devoware.dice.DieRollExpression;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class AttackRoutine {
  private final List<Attack> attacks;
  private final DieRollExpression damageOnAnyHit;
  private final Attack attackOnAnyCrit;

  public static DieRollExpression damageOnAnyHit(String damageOnAnyHit) {
    requireNonNull(damageOnAnyHit, "damageOnAnyHit cannot be null");
    return Dice.parse(damageOnAnyHit);
  }

  public static Builder builder(Attack... attacks) {
    return new Builder()
        .attacks(attacks);
  }

  public static AttackRoutine attackRoutine(Attack... attacks) {
    return new Builder()
        .attacks(attacks)
        .build();
  }

  public static AttackRoutine attackRoutine(DieRollExpression damageOnAnyHit, Attack... attacks) {
    return new Builder()
        .damageOnAnyHit(damageOnAnyHit)
        .attacks(attacks)
        .build();
  }

  public static AttackRoutine attackRoutine(AttackRoutine routine, Consumer<Builder> consumer) {
    Builder builder = new Builder(routine);
    consumer.accept(builder);
    return builder.build();
  }

  private AttackRoutine(Builder builder) {
    this.damageOnAnyHit = builder.damageOnAnyHit;
    this.attackOnAnyCrit = builder.attackOnAnyCrit;
    this.attacks = ImmutableList.copyOf(builder.attacks);
  }

  public List<Attack> getAttacks() {
    return attacks;
  }

  public double damageOnHit() {
    return attacks.stream()
        .mapToDouble(Attack::damageOnHit)
        .sum() + (damageOnAnyHit != null ? damageOnAnyHit.dpr() : 0);
  }

  public double dpr() {
    return attacks.stream()
        // .mapToDouble(Attack::dpr)
        .mapToDouble(a -> a.dpr())
        .sum() + computeDamageOnAnyHit()
        + computeDamageOnAnyCrit();
  }

  private double computeDamageOnAnyCrit() {
    if (attackOnAnyCrit == null) {
      return 0;
    }
    double critProbability = 1 - attacks.stream()
        .mapToDouble(Attack::getCritProbability)
        .map(x -> 1 - x)
        .reduce(1.0, (x, y) -> x * y);
    return critProbability * attackOnAnyCrit.dpr();
  }

  private double computeDamageOnAnyHit() {
    if (damageOnAnyHit == null) {
      return 0;
    }
    double hitProbability = 1 - attacks.stream()
        .mapToDouble(a -> a.getHitProbability())
        .map(prob -> 1 - prob)
        .reduce(1, (a, b) -> a * b);

    double critProbability = computeProbabilityOfAnyCrit();
    double hitDpr = damageOnAnyHit.dpr();
    double critDpr = damageOnAnyHit.getDice().stream().mapToDouble(Dice::dpr).sum();
    double hitDamage = hitProbability * hitDpr;
    double critDamage = critProbability * critDpr;
    return hitDamage + critDamage;
  }

  // Assumes that you will apply your extra damage the first time you hit, which reduces the
  // probability of a crit on any hit
  private double computeProbabilityOfAnyCrit() { //
    Iterator<Attack> it = attacks.iterator();
    Attack attack = it.next(); // There has to be at least one attack
    return computProbabilityOfAnyCrit(attack, it);
  }

  private double computProbabilityOfAnyCrit(Attack attack, Iterator<Attack> it) {
    double critProb = attack.getCritProbability();
    if (it.hasNext()) {
      critProb += (1 - attack.getHitProbability()) * computProbabilityOfAnyCrit(it.next(), it);
    }
    return critProb;
  }

  public static class Builder {
    private List<Attack> attacks = Lists.newArrayList();
    private DieRollExpression damageOnAnyHit;
    private Attack attackOnAnyCrit;


    private Builder() {}

    private Builder(AttackRoutine routine) {
      this.attacks = routine.attacks;
      this.damageOnAnyHit = routine.damageOnAnyHit;
      this.attackOnAnyCrit = routine.attackOnAnyCrit;
    }

    public Builder onAnyCrit(Attack attack) {
      requireNonNull(attack, "attack cannot be null");
      this.attackOnAnyCrit = attack;
      return this;
    }

    public Builder damageOnAnyHit(DieRollExpression damage) {
      requireNonNull(damage, "damage cannot be null");
      this.damageOnAnyHit = damage;
      return this;
    }

    public Builder damageOnAnyHit(String damage) {
      requireNonNull(damage, "damage cannot be null");
      this.damageOnAnyHit = Dice.parse(damage);
      return this;
    }

    public Builder addDamageOnAnyHit(String damage) {
      requireNonNull(damage, "damage");
      this.damageOnAnyHit = DieRollExpression.plus(this.damageOnAnyHit, Dice.parse(damage));
      return this;
    }

    public Builder subtractDamageOnAnyHit(String damage) {
      requireNonNull(damage, "damage");
      this.damageOnAnyHit = DieRollExpression.minus(this.damageOnAnyHit, Dice.parse(damage));
      return this;
    }

    public Builder attacks(Attack... attacks) {
      checkArgument(attacks.length > 0, "attacks must have at least one element");
      this.attacks = Arrays.stream(attacks)
          .collect(collectingAndThen(toList(), ImmutableList::copyOf));
      return this;
    }

    public Builder addAttack(Attack attack) {
      requireNonNull(attack, "attack cannot be null");
      this.attacks.add(attack);
      return this;
    }

    public Builder setAttack(int attackIndex, Attack attack) {
      requireNonNull(attack, "attack cannot be null");
      this.attacks.set(attackIndex, attack);
      return this;
    }


    public Builder removeAttack(int attackIndex) {
      this.attacks.remove(attackIndex);
      return this;
    }

    public Builder mutateAttacks(Consumer<Attack.Builder> mutator) {
      this.attacks = this.attacks.stream()
          .map(a -> attack(a, mutator))
          .collect(Collectors.toList());
      return this;
    }

    public Builder mutateAttack(int attackIndex, Consumer<Attack.Builder> mutator) {
      this.attacks.set(attackIndex, attack(this.attacks.get(attackIndex), mutator));
      return this;
    }

    public AttackRoutine build() {
      requireNonNull(attacks, "attacks cannot be null");
      checkArgument(attacks.size() > 0, "attacks must have at least one element");
      return new AttackRoutine(this);
    }
  }

}
