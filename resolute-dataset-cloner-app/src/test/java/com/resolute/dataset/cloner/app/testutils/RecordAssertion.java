package com.resolute.dataset.cloner.app.testutils;

import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class RecordAssertion {

  private final Map<String, String> record;

  public static RecordAssertion assertRecord(Map<String, String> record) {
    return new RecordAssertion(record);
  }

  private RecordAssertion(Map<String, String> record) {
    assertThat(record).isNotNull();
    this.record = record;
  }

  public RecordAssertion hasFieldValue(String fieldName, Object value) {
    requireNonNull(fieldName, "fieldName cannot be null");
    if (value == null) {
      assertThat(record.get(fieldName)).isEqualTo("null");
    } else {
      assertThat(record.get(fieldName)).isEqualTo(String.valueOf(value));
    }
    return this;
  }

  public RecordAssertion hasMutatedFieldValue(String fieldName, Optional<Integer> prefix,
      Function<String, String> valueGenerator) {
    requireNonNull(fieldName, "fieldName cannot be null");
    requireNonNull(prefix, "prefix cannot be null");
    requireNonNull(valueGenerator, "valueGenerator cannot be null");

    String computedPrefix = prefix.map(String::valueOf).orElse("\\d+");
    String value = valueGenerator.apply(computedPrefix);
    if (value == null) {
      assertThat(record.get(fieldName)).isEqualTo("null");
    } else if (prefix.isPresent()) {
      assertThat(record.get(fieldName)).isEqualTo(String.valueOf(value));
    } else {
      assertThat(record.get(fieldName)).matches("^" + value + "$");
    }
    return this;
  }

  public RecordAssertion hasFieldValueMatching(String fieldName, String regex) {
    requireNonNull(fieldName, "fieldName cannot be null");
    assertThat(record.get(fieldName)).matches(regex);
    return this;
  }

  public RecordAssertion hasNullFieldValue(String fieldName) {
    requireNonNull(fieldName, "fieldName cannot be null");
    assertThat(record.get(fieldName)).isNull();
    return this;
  }

  public RecordAssertion hasNonNullFieldValue(String fieldName) {
    requireNonNull(fieldName, "fieldName cannot be null");
    assertThat(record.get(fieldName)).isNotNull();
    return this;
  }
}
