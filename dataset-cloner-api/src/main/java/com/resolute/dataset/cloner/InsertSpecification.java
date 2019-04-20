package com.resolute.dataset.cloner;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.UnaryOperator;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

public class InsertSpecification {
  private final String pkColumnName;
  private final Set<String> skippedFields;
  private final Map<String, List<KeyMap>> foreignKeys;
  private final Map<String, FieldMutator> mutators;
  private final String sql;

  static Builder builder(KeyMaps keyMaps, String table) {
    return new Builder(keyMaps, table);
  }

  private InsertSpecification(Builder builder) {
    this.pkColumnName = builder.pkColumnName;
    this.skippedFields = builder.skippedFields;
    this.foreignKeys =
        builder.foreignKeys.isEmpty() ? null : ImmutableMap.copyOf(builder.foreignKeys);
    this.mutators = builder.mutators.isEmpty() ? null : ImmutableMap.copyOf(builder.mutators);
    this.sql = builder.sql;
  }

  Optional<String> getPkColumnName() {
    return Optional.ofNullable(pkColumnName);
  }

  Optional<Set<String>> getSkippedFields() {
    return Optional.ofNullable(skippedFields);
  }

  Optional<Map<String, List<KeyMap>>> getForeignKeys() {
    return Optional.ofNullable(foreignKeys);
  }

  Optional<Map<String, FieldMutator>> getMutators() {
    return Optional.ofNullable(mutators);
  }

  Optional<String> getSql() {
    return Optional.ofNullable(sql);
  }

  public static class Builder {
    private final KeyMaps keyMaps;
    private final String table;
    private String pkColumnName;
    private Set<String> skippedFields;
    private Map<String, List<KeyMap>> foreignKeys = Maps.newHashMap();
    private Map<String, FieldMutator> mutators = Maps.newHashMap();
    private String sql;


    private Builder(KeyMaps keyMaps, String table) {
      this.keyMaps = requireNonNull(keyMaps, "keyMaps cannot be null");
      this.table = requireNonNull(table, "table cannot be null");
    }

    public Builder updateKeyMap() {
      return updateKeyMap("id");
    }

    public Builder updateKeyMap(String pkColumnName) {
      this.pkColumnName = requireNonNull(pkColumnName, "pkColumnName cannot be null");
      checkArgument(keyMaps.getKeyMaps().containsKey(table),
          "There is no key map for table " + table + " in the key map registry");
      return this;
    }

    public Builder skipFields(String... columnNames) {
      this.skippedFields = Arrays.stream(columnNames)
          .collect(collectingAndThen(toSet(), ImmutableSet::copyOf));
      return this;

    }

    public Builder foreignKeyRef(String fkColumnName, String... tables) {
      requireNonNull(fkColumnName, "fkColumnName cannot be null");
      List<KeyMap> kms = Arrays.stream(tables)
          .filter(Objects::nonNull)
          .map(table -> {
            KeyMap keyMap = keyMaps.get(table);
            Preconditions.checkArgument(keyMap != null,
                "table " + table + " is not referenced in the key map registry");
            return keyMap;
          })
          .collect(collectingAndThen(toList(), ImmutableList::copyOf));
      this.foreignKeys.put(fkColumnName, kms);
      return this;
    }

    public Builder mutateField(String columnName, UnaryOperator<Object> mutator) {
      requireNonNull(columnName, "name cannot be null");
      requireNonNull(mutator, "mutator cannot be null");
      FieldMutator m = new SimpleFieldMutator(columnName, mutator);
      this.mutators.put(columnName, m);
      return this;
    }

    public Builder sql(String sql) {
      this.sql = requireNonNull(sql, "sql cannot be null");
      return this;
    }

    InsertSpecification build() {
      if (pkColumnName != null) {
        if (this.skippedFields == null) {
          this.skippedFields = ImmutableSet.of(pkColumnName);
        } else {
          this.skippedFields = ImmutableSet.<String>builder()
              .add(pkColumnName)
              .addAll(skippedFields)
              .build();
        }
      }
      return new InsertSpecification(this);
    }
  }

}
