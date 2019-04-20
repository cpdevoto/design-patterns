package com.resolute.dataset.cloner;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public class CompositeFilter implements Filter {
  private final Set<Filter> filters;

  static Builder builder() {
    return new Builder();
  }

  private CompositeFilter(Builder builder) {
    this.filters = ImmutableSet.copyOf(builder.filters);
  }

  Set<Filter> getFilters() {
    return filters;
  }

  @Override
  public List<ForeignKeyFilter> getForeignKeyFilters() {
    return filters.stream()
        .flatMap(filter -> filter.getForeignKeyFilters().stream())
        .collect(Collectors.toList());
  }

  @Override
  public String toSql() {
    return filters.stream()
        .map(Filter::toSql)
        .collect(joining(" AND "));
  }

  static class Builder {
    private Set<Filter> filters = Sets.newLinkedHashSet();

    private Builder() {}

    Builder with(Consumer<Builder> consumer) {
      requireNonNull(consumer, "consumer cannot be null");
      consumer.accept(this);
      return this;
    }

    Builder add(Filter filter) {
      requireNonNull(filter, "filter cannot be null");
      this.filters.add(filter);
      return this;
    }

    CompositeFilter build() {
      requireNonNull(filters, "filters cannot be null");
      return new CompositeFilter(this);
    }
  }
}
