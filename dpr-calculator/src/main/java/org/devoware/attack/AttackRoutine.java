package org.devoware.attack;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.List;

import org.devoware.dice.Dice;
import org.devoware.dice.DieRollExpression;

import com.google.common.collect.ImmutableList;

public class AttackRoutine {
  private final List<Attack> attacks;
  private final DieRollExpression damageOnAnyHit;

  public static DieRollExpression damageOnAnyHit(String damageOnAnyHit) {
    requireNonNull(damageOnAnyHit, "damageOnAnyHit cannot be null");
    return Dice.parse(damageOnAnyHit);
  }

  public static AttackRoutine attackRoutine(Attack... attacks) {
    return new AttackRoutine(attacks);
  }

  public static AttackRoutine attackRoutine(DieRollExpression damageOnAnyHit, Attack... attacks) {
    return new AttackRoutine(damageOnAnyHit, attacks);
  }

  private AttackRoutine(Attack... attacks) {
    this(null, attacks);
  }

  private AttackRoutine(DieRollExpression damageOnAnyHit, Attack... attacks) {
    checkArgument(attacks.length > 0, "attacks must have at least one element");
    this.damageOnAnyHit = damageOnAnyHit;
    this.attacks = Arrays.stream(attacks)
        .collect(collectingAndThen(toList(), ImmutableList::copyOf));
  }

  public double damageOnHit() {
    return attacks.stream()
        .mapToDouble(Attack::damageOnHit)
        .sum() + (damageOnAnyHit != null ? damageOnAnyHit.dpr() : 0);
  }

  public double dpr() {
    return attacks.stream()
        .mapToDouble(Attack::dpr)
        .sum() + computeDamageOnAnyHit();
  }

  private double computeDamageOnAnyHit() {
    if (damageOnAnyHit == null) {
      return 0;
    }
    double hitProbability = 1 - attacks.stream()
        .mapToDouble(a -> a.getHitProbability())
        .map(prob -> 1 - prob)
        .reduce(1, (a, b) -> a * b);
    double critProbability = 1 - attacks.stream()
        .mapToDouble(a -> a.getCritProbability())
        .map(prob -> 1 - prob)
        .reduce(1, (a, b) -> a * b);
    double hitDpr = damageOnAnyHit.dpr();
    double critDpr = damageOnAnyHit.getDice().stream().mapToDouble(Dice::dpr).sum();
    double hitDamage = hitProbability * hitDpr;
    double critDamage = critProbability * critDpr;
    return hitDamage + critDamage;
  }


}
