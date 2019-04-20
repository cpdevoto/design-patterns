package com.resolute.dataset.cloner;

import static java.util.Objects.requireNonNull;

import javax.sql.DataSource;

import com.resolute.jdbc.simple.JdbcStatementFactory;

public class Cloner {

  private final JdbcStatementFactory statementFactory;
  private final KeyMaps keyMaps;

  public static Cloner getInstance(DataSource dataSource, KeyMaps keyMaps) {
    return new Cloner(dataSource, keyMaps);
  }

  private Cloner(DataSource dataSource, KeyMaps keyMaps) {
    requireNonNull(dataSource, "dataSource cannot be null");
    this.statementFactory = JdbcStatementFactory.getInstance(dataSource);
    this.keyMaps = requireNonNull(keyMaps, "keyMaps cannot be null");
  }

  public CloneOperation.Builder clone(String table) {
    return CloneOperation.builder(statementFactory, keyMaps, table);
  }
}
