package org.devoware.dao;

import static java.util.Objects.requireNonNull;

import java.util.function.Consumer;

import javax.sql.DataSource;

import org.postgresql.ds.PGSimpleDataSource;

public class DataSourceBuilder {
  private String host;
  private Integer port;
  private String database;
  private String username;
  private String password;

  public static DataSourceBuilder newInstance() {
    return new DataSourceBuilder();
  }

  private DataSourceBuilder() {}

  public DataSourceBuilder with(Consumer<DataSourceBuilder> consumer) {
    requireNonNull(consumer, "consumer cannot be null");
    consumer.accept(this);
    return this;
  }

  public DataSourceBuilder withHost(String host) {
    requireNonNull(host, "host cannot be null");
    this.host = host;
    return this;
  }

  public DataSourceBuilder withPort(int port) {
    this.port = port;
    return this;
  }

  public DataSourceBuilder withDatabase(String database) {
    requireNonNull(database, "database cannot be null");
    this.database = database;
    return this;
  }

  public DataSourceBuilder withUsername(String username) {
    requireNonNull(username, "username cannot be null");
    this.username = username;
    return this;
  }

  public DataSourceBuilder withPassword(String password) {
    requireNonNull(password, "password cannot be null");
    this.password = password;
    return this;
  }

  public DataSource build() {
    requireNonNull(host, "host cannot be null");
    requireNonNull(port, "port cannot be null");
    requireNonNull(database, "database cannot be null");
    requireNonNull(username, "username cannot be null");
    requireNonNull(password, "password cannot be null");
    PGSimpleDataSource dataSource = new PGSimpleDataSource();
    dataSource.setServerName(host);
    dataSource.setPortNumber(port);
    dataSource.setDatabaseName(database);
    dataSource.setUser(username);
    dataSource.setPassword(password);
    return dataSource;
  }
}

