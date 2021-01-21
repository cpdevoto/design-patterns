package org.dicegolem.attack;

import static java.util.Objects.requireNonNull;

import java.util.function.Consumer;

public interface Attack extends AttackStatGenerator {

  public static Attack attack(Attack attackToClone, Consumer<Attack.Builder> consumer) {
    if (attackToClone == null) {
      return attack(consumer);
    }
    requireNonNull(consumer, "consumer cannot be null");
    AttackImpl.Builder builder = AttackImpl.builder(attackToClone);
    consumer.accept(builder);
    Attack newAttack = builder.build();
    return newAttack;
  }

  public static Attack attack(Consumer<Attack.Builder> consumer) {
    requireNonNull(consumer, "consumer cannot be null");
    AttackImpl.Builder builder = AttackImpl.builder();
    consumer.accept(builder);
    Attack attack = builder.build();
    return attack;
  }

  static Builder builder() {
    return AttackImpl.builder();
  }

  public static interface Builder {

    Builder targetAc(int ac);

    Builder toHitModifier(int hitModifier);

    Builder toHitModifier(String hitModifier);

    Builder critOn(int critOn);

    Builder damage(String damage);

    Builder advantage();

    Builder elvenAccuracy();

    Builder disadvantage();

  }

}
