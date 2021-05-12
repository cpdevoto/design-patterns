package com.resolute.dataset.cloner.app.integration;

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

import com.resolute.database.crawler.DatabaseCrawler;
import com.resolute.database.crawler.model.Graph;
import com.resolute.dataset.cloner.app.AbstractBuildingClonerTest;
import com.resolute.dataset.cloner.app.AbstractCustomerClonerTest;
import com.resolute.dataset.cloner.app.AbstractDistributorClonerTest;
import com.resolute.dataset.cloner.app.AbstractResoluteCloneApplicationTest;
import com.resolute.dataset.cloner.app.AbstractResoluteRollbackApplicationTest;
import com.resolute.dataset.cloner.app.AbstractResoluteScriptApplicationTest;
import com.resolute.jdbc.simple.DaoUtils;
import com.resolute.testutils.postgres.DataSourceBuilder;
import com.resolute.utils.docker.DockerUtils;
import com.resolute.utils.simple.ElapsedTimeUtils;

@Testcontainers
public class IntegrationTestSuite {

  @Container
  private static GenericContainer<?> db = new GenericContainer<>(
      DockerImageName.parse(DockerUtils.resolveHost("postgres-schema")))
          .withExposedPorts(5432)
          .withImagePullPolicy(PullPolicy.alwaysPull());

  private static DataSource dataSource;

  private static Graph schemaGraph;

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

    DatabaseCrawler dao = DatabaseCrawler.create(dataSource);
    long start = System.currentTimeMillis();
    System.out.println("Starting database graph generation...");
    schemaGraph = dao.getSchemaGraph();
    long elapsed = System.currentTimeMillis() - start;
    System.out
        .println(
            "Database graph generation completed in " + ElapsedTimeUtils.format(elapsed)
                + "\n");

  }

  @AfterAll
  static void teardownDatabase() throws IOException, SQLException {

    DaoUtils.executeSqlScript(dataSource, IntegrationTestSuite.class,
        "bootstrap-teardown.sql");
  }

  public static DataSource getDataSource() {
    return dataSource;
  }

  public static int getDatabasePort() {
    return db.getFirstMappedPort();
  }

  public static Graph getSchemaGraph() {
    return schemaGraph;
  }

  // ----------------------
  // Nested Test Classes
  // ----------------------

  @Nested
  class BuildingClonerTest extends AbstractBuildingClonerTest {
  }

  @Nested
  class CustomerClonerTest extends AbstractCustomerClonerTest {
  }

  @Nested
  class DistributorClonerTest extends AbstractDistributorClonerTest {
  }

  @Nested
  class ResoluteCloneApplicationTest extends AbstractResoluteCloneApplicationTest {
  }

  @Nested
  class ResoluteRollbackApplicationTest extends AbstractResoluteRollbackApplicationTest {
  }

  @Nested
  class ResoluteScriptApplicationTest extends AbstractResoluteScriptApplicationTest {
  }

}
