package com.resolute.dataset.cloner.utils;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

public class Key {
  private final List<String> fieldNames;
  private final Map<String, String> fieldValues;
  private final String invalidFieldNameMessage;

  public static Builder builder() {
    return new Builder();
  }

  public static Key of(String fieldName1, Object fieldValue1) {
    requireNonNull(fieldName1, "fieldName1 cannot be null");
    requireNonNull(fieldValue1, "fieldValue1 cannot be null");
    return builder()
        .withFieldValue(fieldName1, fieldValue1)
        .build();
  }

  public static Key of(String fieldName1, Object fieldValue1, String fieldName2,
      Object fieldValue2) {
    requireNonNull(fieldName1, "fieldName1 cannot be null");
    requireNonNull(fieldValue1, "fieldValue1 cannot be null");
    requireNonNull(fieldName2, "fieldName2 cannot be null");
    requireNonNull(fieldValue2, "fieldValue2 cannot be null");
    return builder()
        .withFieldValue(fieldName1, fieldValue1)
        .withFieldValue(fieldName2, fieldValue2)
        .build();
  }

  public static Key of(String fieldName1, Object fieldValue1, String fieldName2, Object fieldValue2,
      String fieldName3, Object fieldValue3) {
    requireNonNull(fieldName1, "fieldName1 cannot be null");
    requireNonNull(fieldValue1, "fieldValue1 cannot be null");
    requireNonNull(fieldName2, "fieldName2 cannot be null");
    requireNonNull(fieldValue2, "fieldValue2 cannot be null");
    requireNonNull(fieldName3, "fieldName3 cannot be null");
    requireNonNull(fieldValue3, "fieldValue3 cannot be null");
    return builder()
        .withFieldValue(fieldName1, fieldValue1)
        .withFieldValue(fieldName2, fieldValue2)
        .withFieldValue(fieldName3, fieldValue3)
        .build();
  }

  public static Key of(String fieldName1, Object fieldValue1, String fieldName2, Object fieldValue2,
      String fieldName3, Object fieldValue3, String fieldName4, Object fieldValue4) {
    requireNonNull(fieldName1, "fieldName1 cannot be null");
    requireNonNull(fieldValue1, "fieldValue1 cannot be null");
    requireNonNull(fieldName2, "fieldName2 cannot be null");
    requireNonNull(fieldValue2, "fieldValue2 cannot be null");
    requireNonNull(fieldName3, "fieldName3 cannot be null");
    requireNonNull(fieldValue3, "fieldValue3 cannot be null");
    requireNonNull(fieldName4, "fieldName4 cannot be null");
    requireNonNull(fieldValue4, "fieldValue4 cannot be null");
    return builder()
        .withFieldValue(fieldName1, fieldValue1)
        .withFieldValue(fieldName2, fieldValue2)
        .withFieldValue(fieldName3, fieldValue3)
        .withFieldValue(fieldName4, fieldValue4)
        .build();
  }

  public static Key of(String fieldName1, Object fieldValue1, String fieldName2, Object fieldValue2,
      String fieldName3, Object fieldValue3, String fieldName4, Object fieldValue4,
      String fieldName5, Object fieldValue5) {
    requireNonNull(fieldName1, "fieldName1 cannot be null");
    requireNonNull(fieldValue1, "fieldValue1 cannot be null");
    requireNonNull(fieldName2, "fieldName2 cannot be null");
    requireNonNull(fieldValue2, "fieldValue2 cannot be null");
    requireNonNull(fieldName3, "fieldName3 cannot be null");
    requireNonNull(fieldValue3, "fieldValue3 cannot be null");
    requireNonNull(fieldName4, "fieldName4 cannot be null");
    requireNonNull(fieldValue4, "fieldValue4 cannot be null");
    requireNonNull(fieldName5, "fieldName5 cannot be null");
    requireNonNull(fieldValue5, "fieldValue5 cannot be null");
    return builder()
        .withFieldValue(fieldName1, fieldValue1)
        .withFieldValue(fieldName2, fieldValue2)
        .withFieldValue(fieldName3, fieldValue3)
        .withFieldValue(fieldName4, fieldValue4)
        .withFieldValue(fieldName5, fieldValue5)
        .build();
  }

  private Key(Builder builder) {
    this.fieldValues = ImmutableMap.copyOf(builder.fieldValues);
    this.fieldNames = fieldValues.keySet().stream()
        .collect(collectingAndThen(toList(), ImmutableList::copyOf));
    this.invalidFieldNameMessage =
        String.format("fieldName must be one of %s", fieldNames.stream().collect(joining(", ")));
  }

  public List<String> getFieldNames() {
    return fieldNames;
  }

  public String getFieldValue(String fieldName) {
    requireNonNull(fieldName, "fieldName cannot be null");
    checkArgument(fieldValues.containsKey(fieldName), invalidFieldNameMessage);
    return fieldValues.get(fieldName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(fieldValues);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Key other = (Key) obj;
    return Objects.equals(fieldValues, other.fieldValues);
  }

  @Override
  public String toString() {
    return "Key " + fieldValues;
  }

  public boolean hasSameFields(Key other) {
    requireNonNull(other, "other cannot be null");
    return getFieldNameSet().equals(other.getFieldNameSet());
  }

  private Set<String> getFieldNameSet() {
    return fieldNames.stream().collect(collectingAndThen(toSet(), ImmutableSet::copyOf));
  }

  public static class Builder {
    private final Map<String, String> fieldValues = Maps.newLinkedHashMap();

    private Builder() {}

    public Builder withFieldValue(String fieldName, Object fieldValue) {
      requireNonNull(fieldName, "fieldName cannot be null");
      requireNonNull(fieldValue, "fieldValue cannot be null");
      this.fieldValues.put(fieldName, String.valueOf(fieldValue));
      return this;
    }

    public Key build() {
      checkState(fieldValues.size() > 0, "expected at least one field value");
      return new Key(this);
    }
  }
}
