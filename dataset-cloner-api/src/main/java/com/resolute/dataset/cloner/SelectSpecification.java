package com.resolute.dataset.cloner;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import com.google.common.collect.Lists;

public class SelectSpecification {

  private final Filter filter;
  private final String sql;


  static Builder builder(KeyMaps keyMaps) {
    return new Builder(keyMaps);
  }

  Optional<Filter> getFilter() {
    return Optional.ofNullable(filter);
  }

  Optional<String> getSql() {
    return Optional.ofNullable(sql);
  }

  private SelectSpecification(Builder builder) {
    this.filter = builder.filter;
    this.sql = builder.sql;
  }

  public static class Builder {
    private final KeyMaps keyMaps;
    private Filter filter;
    private String sql;

    private Builder(KeyMaps keyMaps) {
      this.keyMaps = requireNonNull(keyMaps, "keyMaps cannot be null");
    }

    public Builder fieldFilter(String columnName, int... values) {
      this.filter = FieldFilter.builder()
          .withName(columnName)
          .withValues(values)
          .build();
      return this;
    }

    public Builder foreignKeyFilter(String columnName, String... tables) {
      this.filter = ForeignKeyFilter.builder(keyMaps)
          .withName(columnName)
          .withTables(tables)
          .build();
      return this;
    }

    public Builder compositeFilter(Consumer<CompositeFilterBuilder> consumer) {
      CompositeFilterBuilder builder = new CompositeFilterBuilder();
      consumer.accept(builder);
      this.filter = builder.build();
      return this;
    }

    public Builder sql(String sql) {
      this.sql = requireNonNull(sql);
      return this;
    }

    SelectSpecification build() {
      return new SelectSpecification(this);
    }

    public class CompositeFilterBuilder {
      List<Filter> filters = Lists.newArrayList();

      public CompositeFilterBuilder add(Filter filter) {
        requireNonNull(filter, "filter cannot be null");
        this.filters.add(filter);
        return this;
      }

      public Filter fieldFilter(String columnName, int... values) {
        return FieldFilter.builder()
            .withName(columnName)
            .withValues(values)
            .build();
      }

      public Filter foreignKeyFilter(String columnName, String... tables) {
        return ForeignKeyFilter.builder(keyMaps)
            .withName(columnName)
            .withTables(tables)
            .build();
      }

      public CompositeFilter build() {
        checkArgument(filters.size() > 0, "filters must include at least one element");
        return CompositeFilter.builder()
            .with(builder -> {
              filters.stream().forEach(builder::add);
            })
            .build();
      }

    }
  }

}
