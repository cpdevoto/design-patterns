package org.devoware.character;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static java.util.Objects.requireNonNull;
import static org.devoware.attack.AttackRoutine.attackRoutine;

import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import org.devoware.attack.Attack;
import org.devoware.attack.AttackRoutine;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableRangeMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.TreeRangeMap;

public class CharacterBuild {
  private final String name;
  private final RangeMap<Integer, AttackRoutine> attackRoutines;

  public static CharacterBuild characterBuild(String name, Consumer<Builder> consumer) {
    requireNonNull(consumer, "consumer cannot be null");
    Builder builder = new Builder(name);
    consumer.accept(builder);
    return builder.build();
  }

  private CharacterBuild(Builder builder) {
    this.name = builder.name;
    RangeMap<Integer, AttackRoutine> attackRoutines = TreeRangeMap.create();
    int lastLowerBound = -1;
    AttackRoutine attackRoutine = null;
    for (Entry<Integer, AttackRoutine> entry : builder.attackRoutines.entrySet()) {
      if (lastLowerBound > -1) {
        Range<Integer> r = Range.closedOpen(lastLowerBound, entry.getKey());
        attackRoutines.put(r, attackRoutine);
      }
      lastLowerBound = entry.getKey();
      attackRoutine = entry.getValue();
    }
    Range<Integer> r = Range.closed(lastLowerBound, 20);
    attackRoutines.put(r, attackRoutine);
    this.attackRoutines = ImmutableRangeMap.copyOf(attackRoutines);
  }

  public String getName() {
    return name;
  }

  public Map<Integer, Double> dprByLevel() {
    Map<Integer, Double> result = Maps.newLinkedHashMap();
    IntStream.rangeClosed(1, 20)
        .forEach(i -> result.put(i, attackRoutines.get(i).dpr()));
    return ImmutableMap.copyOf(result);
  }

  public Map<Integer, Double> damageOnHitByLevel() {
    Map<Integer, Double> result = Maps.newLinkedHashMap();
    IntStream.rangeClosed(1, 20)
        .forEach(i -> result.put(i, attackRoutines.get(i).damageOnHit()));
    return ImmutableMap.copyOf(result);
  }

  public static class Builder {
    private final String name;
    private Map<Integer, AttackRoutine> attackRoutines = Maps.newTreeMap();

    private Builder(String name) {
      this.name = name;
    }

    public Builder attackRoutineForLevel(int level, Attack... attacks) {
      checkArgument(level >= 1 && level <= 20, "invalid level");
      attackRoutines.put(level, attackRoutine(attacks));
      return this;
    }

    CharacterBuild build() {
      checkState(attackRoutines.containsKey(1),
          "must include an attackRoutine for level 1");
      return new CharacterBuild(this);
    }

  }

}
