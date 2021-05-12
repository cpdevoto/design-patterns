package com.resolute.dataset.cloner.app.testutils;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.resolute.dataset.cloner.utils.Key;

public class Records {

  private final List<Map<String, String>> records;

  public Records(List<Map<String, String>> records) {
    this.records = requireNonNull(records, "records cannot be null");
  }

  public int size() {
    return records.size();
  }

  public Optional<Map<String, String>> findRecord(Key key) {
    return records.stream()
        .filter(rec -> {
          for (String fieldName : key.getFieldNames()) {
            String fieldValue = rec.get(fieldName);
            if (fieldValue == null
                || !fieldValue.equals(key.getFieldValue(fieldName))) {
              return false;
            }
          }
          return true;
        })
        .findAny();
  }

  protected void mapOf(List<String> primaryKeyFields,
      List<Map<String, String>> records) {
    requireNonNull(primaryKeyFields, "primaryKeyFields cannot be null");
    requireNonNull(records, "records cannot be null");
  }


}
