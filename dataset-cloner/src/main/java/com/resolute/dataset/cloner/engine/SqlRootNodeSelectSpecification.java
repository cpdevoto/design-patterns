package com.resolute.dataset.cloner.engine;

import static java.util.Objects.requireNonNull;

class SqlRootNodeSelectSpecification implements RootNodeSelectSpecification {

  private final String sql;

  SqlRootNodeSelectSpecification(String sql) {
    this.sql = requireNonNull(sql, "sql cannot be null");
  }

  @Override
  public String getRootSelectStatement(int tableNamePrefix) {
    return sql;
  }

}
