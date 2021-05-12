package com.resolute.database.crawler.integration;

import java.io.IOException;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.images.PullPolicy;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import com.resolute.database.crawler.AbstractDatabaseCrawlerTest;
import com.resolute.jdbc.simple.DaoUtils;
import com.resolute.testutils.postgres.DataSourceBuilder;
import com.resolute.utils.docker.DockerUtils;

@Testcontainers
public class IntegrationTestSuite {

  @Container
  private static GenericContainer<?> db = new GenericContainer<>(
      DockerImageName.parse(DockerUtils.resolveHost("postgres-schema")))
          .withExposedPorts(5432)
          .withImagePullPolicy(PullPolicy.alwaysPull());

  private static DataSource dataSource;

  @BeforeAll
  static void initializeDatabase() throws IOException, SQLException {

    dataSource = DataSourceBuilder.newInstance()
        .withHost(db.getHost())
        .withPort(db.getFirstMappedPort())
        .withDatabase("resolute_cloud_dev")
        .withUsername("postgres")
        .withPassword("")
        .build();

    DaoUtils.executeSqlScript(dataSource, IntegrationTestSuite.class,
        "bootstrap-data.sql");
  }

  @AfterAll
  static void teardownDatabase() throws IOException, SQLException {

    DaoUtils.executeSqlScript(dataSource, IntegrationTestSuite.class,
        "bootstrap-teardown.sql");
  }

  public static DataSource getDataSource() {
    return dataSource;
  }

  // ----------------------
  // Nested Test Classes
  // ----------------------

  @Nested
  class DatabaseCrawlerTest extends AbstractDatabaseCrawlerTest {
  }

}
