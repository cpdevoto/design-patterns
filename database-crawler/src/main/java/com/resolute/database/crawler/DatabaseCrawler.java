package com.resolute.database.crawler;

import javax.sql.DataSource;

import com.resolute.database.crawler.model.Graph;
import com.resolute.jdbc.simple.JdbcStatementFactory;

public interface DatabaseCrawler {

  public static DatabaseCrawler create(DataSource dataSource) {
    return DatabaseCrawlerImpl.create(dataSource);
  }

  public JdbcStatementFactory getStatementFactory();

  public Graph getSchemaGraph();

  public boolean testConnection();

}
