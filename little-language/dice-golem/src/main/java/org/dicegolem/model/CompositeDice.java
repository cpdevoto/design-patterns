package org.dicegolem.model;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Optional;

import com.google.common.collect.Lists;

public class CompositeDice implements DiceRollExpression {
  private final int numDice;
  private final Die die;
  private final DieRollAggregator aggregator;
  private final DieRollModifier modifier;

  public static Builder builder() {
    return new Builder();
  }

  private CompositeDice(Builder builder) {
    this.numDice = builder.numDice;
    this.die = builder.die;
    this.aggregator = builder.aggregator;
    this.modifier = builder.modifier;
  }


  @Override
  public int roll() {
    List<Integer> rolls = Lists.newArrayList();
    for (int i = 0; i < numDice; i++) {
      int roll = die.roll();
      if (modifier != null) {
        roll = modifier.modify(die, roll);
      }
      rolls.add(roll);
    }
    return aggregator.aggregate(rolls);
  }

  @Override
  public double average() {
    return aggregator.aggregateAverages(numDice, die, modifier);
  }

  @Override
  public double averageDiceOnly() {
    return average();
  }

  public int getNumDice() {
    return numDice;
  }

  public Die getDie() {
    return die;
  }

  public DieRollAggregator getAggregator() {
    return aggregator;
  }

  public Optional<DieRollModifier> getModifier() {
    return Optional.ofNullable(modifier);
  }

  public static class Builder {
    private Integer numDice;
    private Die die;
    private DieRollAggregator aggregator = new SumAggregator();
    private DieRollModifier modifier;

    private Builder() {}

    public Builder withNumDice(int numDice) {
      checkArgument(numDice > 0, "numDice must be greater than zero");
      this.numDice = numDice;
      return this;
    }

    public Builder withDie(int dieType) {
      return withDie(Die.get(dieType));
    }

    public Builder withDie(Die die) {
      this.die = requireNonNull(die, "die cannot be null");
      return this;
    }

    public Builder withAggregator(DieRollAggregator aggregator) {
      this.aggregator = requireNonNull(aggregator, "aggregator cannot be null");
      return this;
    }

    public Builder withModifier(DieRollModifier modifier) {
      this.modifier = requireNonNull(modifier, "modifier cannot be null");
      return this;
    }

    public CompositeDice build() {
      requireNonNull(numDice, "numDice cannot be null");
      requireNonNull(die, "die cannot be null");
      return new CompositeDice(this);
    }

  }
}
