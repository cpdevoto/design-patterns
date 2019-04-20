package com.resolute.dataset.cloner;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toSet;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public class FieldFilter implements Filter {
  private final String name;
  private final Set<Integer> values;

  static Builder builder() {
    return new Builder();
  }

  private FieldFilter(Builder builder) {
    this.name = builder.name;
    this.values = builder.values;
  }

  String getName() {
    return name;
  }

  Set<Integer> getValues() {
    return values;
  }

  @Override
  public String toSql() {
    return name + " IN " + values.stream()
        .map(String::valueOf)
        .collect(joining(", ", "(", ")"));
  }

  @Override
  public List<ForeignKeyFilter> getForeignKeyFilters() {
    return ImmutableList.of();
  }

  static class Builder {
    private String name;
    private Set<Integer> values;

    private Builder() {}

    Builder withName(String name) {
      requireNonNull(name, "name cannot be null");
      this.name = name;
      return this;
    }

    Builder withValues(int... values) {
      requireNonNull(values, "values cannot be null");
      this.values = Arrays.stream(values)
          .boxed().collect(collectingAndThen(toSet(), ImmutableSet::copyOf));
      return this;
    }

    FieldFilter build() {
      requireNonNull(name, "name cannot be null");
      requireNonNull(values, "values cannot be null");
      checkArgument(values.size() > 0, "values cannot be empty");
      return new FieldFilter(this);
    }
  }
}
