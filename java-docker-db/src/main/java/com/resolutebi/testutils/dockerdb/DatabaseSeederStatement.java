package com.resolutebi.testutils.dockerdb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Objects.requireNonNull;


class DatabaseSeederStatement extends Statement {
  Logger log = LoggerFactory.getLogger(DatabaseSeederStatement.class);

  private final Statement base;
  private final DataSource dataSource;
  private final List<ClassResource> seedScripts;
  private final List<ClassResource> tearDownScripts;


  DatabaseSeederStatement(Statement base, DatabaseSeeder databaseSeeder) {
    requireNonNull(base, "base cannot be null");
    requireNonNull(databaseSeeder, "databaseSeeder cannot be null");
    this.base = base;
    this.dataSource = databaseSeeder.getDataSource();
    this.seedScripts = databaseSeeder.getSeedScripts();
    this.tearDownScripts = databaseSeeder.getTearDownScripts();
  }

  @Override
  public void evaluate() throws Throwable {
    seedDatabase();
    try {
      executeTests();
    } finally {
      tearDownDatabase();
    }
  }

  private void seedDatabase() throws Exception, IOException, SQLException {
    log.info("Starting to seed the database");
    executeScripts(seedScripts);
    log.info("Finished seeding the database");
  }

  private void executeTests() throws Throwable {
    base.evaluate();
  }

  private void tearDownDatabase() throws IOException, SQLException, Exception {
    log.info("Starting to tear down the database");
    executeScripts(tearDownScripts);
    log.info("Finished tearing down the database");
  }

  private void executeScripts(List<ClassResource> scripts) throws IOException, SQLException {
    for (ClassResource resource : scripts) {
      log.info("Executing script " + resource.getFileName());
      seedDatabaseWithSQL(resource.getFileLocatorClass(),
          resource.getFileName());
    }
  }

  private void seedDatabaseWithSQL(Optional<Class<?>> fileLocatorClass,
      String fileName) throws IOException, SQLException {
    try (Connection connection = dataSource.getConnection();
        java.sql.Statement statement = connection.createStatement();) {

      String sqlfileContent = null;
      if (fileLocatorClass.isPresent()) {
        try (InputStream inputStream = fileLocatorClass.get().getResourceAsStream(fileName)) {
          sqlfileContent = new BufferedReader(new InputStreamReader(inputStream)).lines()
              .collect(Collectors.joining("\n"));
        }
      } else {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName)) {
          sqlfileContent = new BufferedReader(new InputStreamReader(inputStream)).lines()
              .collect(Collectors.joining("\n"));
        }
      }

      if (sqlfileContent != null) {
        statement.execute(sqlfileContent);
      }

    }

  }
}
