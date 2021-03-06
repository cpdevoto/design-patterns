package org.devoware.table;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.ImmutableRangeMap;
import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.TreeRangeMap;

public class Table {

  private static final Pattern DIE_ROLL = Pattern.compile("([1-9][0-9]*)[dD]([1-9][0-9]*)");

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
    String result = entries.get(roll);
    result = parseDieRolls(result);
    return result;
  }

  public String get(int roll) {
    checkArgument(roll >= lowerEndpoint && roll <= upperEndpoint,
        String.format("roll must be between %d and %d", lowerEndpoint, upperEndpoint));
    String result = entries.get(roll);
    return result;
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

  private String parseDieRolls(String s) {
    Matcher m = DIE_ROLL.matcher(s);
    int start = 0;
    StringBuilder buf = new StringBuilder();
    while (m.find(start)) {
      int mStart = m.start();
      int mEnd = m.end();
      int numDice = Integer.parseInt(m.group(1));
      int dieType = Integer.parseInt(m.group(2));
      int roll = roll(numDice, dieType);
      if (start < mStart) {
        buf.append(s.substring(start, mStart));
      }
      buf.append(roll);
      start = mEnd;
    }
    if (start < s.length()) {
      buf.append(s.substring(start));
    }
    return buf.toString();
  }

  private int roll(int numDice, int dieType) {
    int total = 0;
    for (int i = 0; i < numDice; i++) {
      total += (int) (Math.random() * dieType + 1);
    }
    return total;
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
