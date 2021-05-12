package com.resolute.database.crawler.integration;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeAll;

import com.resolute.jdbc.simple.JdbcStatementFactory;

public abstract class AbstractPostgresDaoTest {

  protected static DataSource dataSource;
  protected static JdbcStatementFactory statementFactory;

  @BeforeAll
  public static void setupSqlTest() {
    dataSource = IntegrationTestSuite.getDataSource();
    statementFactory = JdbcStatementFactory.getInstance(dataSource);
  }

}
