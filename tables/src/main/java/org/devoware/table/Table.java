package org.devoware.table;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import com.google.common.collect.ImmutableRangeMap;
import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.TreeRangeMap;

public class Table {

  private final String name;
  private final RangeMap<Integer, String> entries;
  private int lowerEndpoint;
  private int upperEndpoint;

  public static Builder builder() {
    return new Builder();
  }

  private Table(Builder builder) {
    this.name = builder.name;
    this.entries = ImmutableRangeMap.copyOf(builder.entries);
    this.lowerEndpoint = builder.lowerEndpoint;
    this.upperEndpoint = builder.upperEndpoint;
  }

  public String getName() {
    return name;
  }

  public String roll() {
    int roll = (int) (Math.random() * (upperEndpoint - lowerEndpoint + 1) + lowerEndpoint);
    return entries.get(roll);
  }

  public String get(int roll) {
    checkArgument(roll >= lowerEndpoint && roll <= upperEndpoint,
        String.format("roll must be between %d and %d", lowerEndpoint, upperEndpoint));
    return entries.get(roll);
  }

  public int getLowerEndpoint() {
    return lowerEndpoint;
  }

  public int getUpperEndpoint() {
    return upperEndpoint;
  }

  public int size() {
    return entries.asMapOfRanges().size();
  }

  public static class Builder {
    private boolean tableEmpty = true;
    private String name;
    private RangeMap<Integer, String> entries = TreeRangeMap.create();
    private int lowerEndpoint;
    private int upperEndpoint;

    private Builder() {}

    public Builder withName(String name) {
      this.name = requireNonNull(name, "name cannot be null");
      return this;
    }

    public Builder withEntry(int lower, int upper, String value) {
      checkArgument(lower > 0, "expected a positive integer lower");
      checkArgument(upper > 0, "expected a positive integer upper");
      checkArgument(upper >= lower, "expected upper to be greater than or equal to lower");
      requireNonNull(value, "value cannot be null");
      Range<Integer> range = Range.closed(lower, upper);
      entries.put(range, value);
      tableEmpty = false;
      return this;
    }

    public Table build() {
      requireNonNull(name, "name cannot be null");
      checkArgument(!tableEmpty, "expected at least one entry");
      Range<Integer> span = entries.span();
      this.lowerEndpoint = span.lowerEndpoint();
      this.upperEndpoint = span.upperEndpoint();
      validateEntries();
      return new Table(this);
    }

    private void validateEntries() {
      for (int i = lowerEndpoint; i <= upperEndpoint; i++) {
        checkArgument(entries.get(i) != null,
            "table must be fully connected (i.e. cannot contain any gaps)");
      }
    }

  }

}
