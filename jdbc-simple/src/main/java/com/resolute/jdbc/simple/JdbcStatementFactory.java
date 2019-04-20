package com.resolute.jdbc.simple;

import javax.sql.DataSource;

public class JdbcStatementFactory {

  private DataSource dataSource;

  public static JdbcStatementFactory getInstance(DataSource dataSource) {
    return new JdbcStatementFactory(dataSource);
  }

  private JdbcStatementFactory(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  public JdbcStatement newStatement() {
    return new JdbcStatement(dataSource);
  }

}
