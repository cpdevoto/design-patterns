package com.resolute.dataset.cloner.engine;

import static java.util.Objects.requireNonNull;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.resolute.dataset.cloner.utils.Key;

class InsertStatement {
  private final List<Key> insertedRecordKeys;
  private final String sql;

  static Builder builder() {
    return new Builder();
  }

  private InsertStatement(Builder builder) {
    this.insertedRecordKeys = builder.insertedRecordKeys;
    this.sql = builder.sql;
  }

  List<Key> getInsertedRecordKeys() {
    return insertedRecordKeys;
  }

  String getSql() {
    return sql;
  }


  static class Builder {
    private List<Key> insertedRecordKeys;
    private String sql;

    private Builder() {}

    Builder withInsertedRecordKeys(List<Key> insertedRecordKeys) {
      requireNonNull(insertedRecordKeys, "insertedRecordKeys cannot be null");
      this.insertedRecordKeys = ImmutableList.copyOf(insertedRecordKeys);
      return this;
    }

    Builder withSql(String sql) {
      requireNonNull(sql, "sql cannot be null");
      this.sql = sql;
      return this;
    }

    InsertStatement build() {
      requireNonNull(insertedRecordKeys, "insertedRecordKeys cannot be null");
      requireNonNull(sql, "sql cannot be null");
      return new InsertStatement(this);
    }
  }
}
