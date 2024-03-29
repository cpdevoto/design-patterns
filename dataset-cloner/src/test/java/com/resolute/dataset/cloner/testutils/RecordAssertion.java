package com.resolute.dataset.cloner.testutils;

import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

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
