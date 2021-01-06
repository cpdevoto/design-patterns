package com.resolute.jdbc.simple.fixtures;

import static java.util.Objects.requireNonNull;

import java.util.function.Consumer;

import javax.sql.DataSource;

import org.postgresql.ds.PGSimpleDataSource;

/**
 * Creates a javax.sql.DataSource object from the following properties:
 * <ul>
 * <li>url (alternatively, you can specify a host, port, and database)
 * <li>username
 * <li>password
 * </ul>
 * 
 * @author cdevoto
 *
 */
public class DataSourceBuilder {
  private String url;
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

  public DataSourceBuilder withUrl(String url) {
    requireNonNull(url, "url cannot be null");
    this.url = url;
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
    if (url == null) {
      requireNonNull(host, "host cannot be null unless you specify a url instead");
      requireNonNull(port, "port cannot be null unless you specify a url instead");
      requireNonNull(database, "database cannot be null unless you specify a url instead");
    }
    requireNonNull(username, "username cannot be null");
    requireNonNull(password, "password cannot be null");
    PGSimpleDataSource dataSource = new PGSimpleDataSource();
    if (url != null) {
      dataSource.setUrl(url);
    } else {
      dataSource.setServerName(host);
      dataSource.setPortNumber(port);
      dataSource.setDatabaseName(database);
    }
    dataSource.setUser(username);
    dataSource.setPassword(password);
    return dataSource;
  }
}

