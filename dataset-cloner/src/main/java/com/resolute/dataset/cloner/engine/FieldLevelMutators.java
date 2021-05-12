package com.resolute.dataset.cloner.engine;

import static java.util.Objects.requireNonNull;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

class FieldLevelMutators {

  static final FieldLevelMutators NONE = FieldLevelMutators.builder().build();

  private final Map<String, FieldLevelMutator> fieldLevelMutators;

  static Builder builder() {
    return new Builder();
  }

  private FieldLevelMutators(Builder builder) {
    this.fieldLevelMutators = ImmutableMap.copyOf(builder.fieldLevelMutators);
  }

  Set<String> getFieldsWithMutators() {
    return ImmutableSet.copyOf(fieldLevelMutators.keySet());

  }

  Optional<FieldLevelMutator> get(String fieldName) {
    requireNonNull(fieldName, "fieldName cannot be null");
    return Optional.ofNullable(fieldLevelMutators.get(fieldName));
  }

  FieldLevelMutator get(String fieldName, FieldLevelMutator defaultMutator) {
    requireNonNull(fieldName, "fieldName cannot be null");
    return get(fieldName).orElse(defaultMutator);
  }

  static class Builder {
    private final Map<String, FieldLevelMutator> fieldLevelMutators = Maps.newHashMap();

    private Builder() {}

    Builder withFieldLevelMutator(String fieldName,
        FieldLevelMutator mutator) {
      requireNonNull(fieldName, "fieldName cannot be null");
      requireNonNull(mutator, "mutator cannot be null");
      fieldLevelMutators.put(fieldName, mutator);
      return this;
    }

    FieldLevelMutators build() {
      return new FieldLevelMutators(this);
    }

  }

}
