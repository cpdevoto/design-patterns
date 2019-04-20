package org.devoware.attack;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.List;

import com.google.common.collect.ImmutableList;

public class AttackRoutine {
  private final List<Attack> attacks;

  public static AttackRoutine attackRoutine(Attack... attacks) {
    return new AttackRoutine(attacks);
  }

  private AttackRoutine(Attack... attacks) {
    checkArgument(attacks.length > 0, "attacks must have at least one element");;
    this.attacks = Arrays.stream(attacks)
        .collect(collectingAndThen(toList(), ImmutableList::copyOf));
  }

  public double damageOnHit() {
    return attacks.stream()
        .mapToDouble(Attack::damageOnHit)
        .sum();
  }

  public double dpr() {
    return attacks.stream()
        .mapToDouble(Attack::dpr)
        .sum();
  }

}
