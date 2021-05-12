package com.resolute.dataset.cloner.app;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.resolute.database.crawler.model.Node;
import com.resolute.dataset.cloner.Environment;
import com.resolute.dataset.cloner.app.integration.AbstractDatabaseTest;
import com.resolute.dataset.cloner.app.integration.IntegrationTestSuite;
import com.resolute.dataset.cloner.app.testutils.BuildingCloneAssertions;
import com.resolute.dataset.cloner.log.Logger;
import com.resolute.jdbc.simple.DaoUtils;
import com.resolute.utils.simple.ElapsedTimeUtils;

public abstract class AbstractBuildingClonerTest extends AbstractDatabaseTest {

  @TempDir
  File tempDir;

  @BeforeEach
  void seedTestTables() throws IOException, SQLException {
    DaoUtils.executeSqlScript(dataSource, IntegrationTestSuite.class,
        "base-resolute-data.sql");
  }

  @AfterEach
  void cleanupTestTables() throws IOException, SQLException {
    DaoUtils.executeSqlScript(dataSource, IntegrationTestSuite.class,
        "base-resolute-teardown.sql");
  }

  @Test
  void test_execute() {
    File logFile = new File(tempDir, "dataset-cloner.log");

    Environment env = Environment.builder()
        .withSchemaGraph(schemaGraph)
        .withDataSource(dataSource)
        .withLogger(new Logger(logFile))
        .build();

    BuildingCloner cloner = BuildingCloner.builder(env)
        .withBuildingId(2)
        .withBeforeAllListener(() -> System.out.println("Starting building clone operation..."))
        .withAfterAllListener((elapsed) -> System.out
            .println("Finished building clone operation in " + ElapsedTimeUtils.format(elapsed)))
        .build();

    int prefix = cloner.getTableNamePrefix();

    assertThat(cloner.getOrphanedSuperclassNodes())
        .as("BuildingCloner contains the following orphaned superclass nodes: "
            + cloner.getOrphanedSuperclassNodes().keySet().stream().map(Node::getName)
                .collect(Collectors.joining(", ")))
        .isEmpty();

    try {
      cloner.execute();

      new BuildingCloneAssertions(this).assertStateAfterClone(prefix);;

    } finally {
      cloner.rollback();
    }
  }

  @Test
  void test_execute2() {
    // In this test, we just want to confirm that no exception will be thrown if there
    // are no raw points to clone!
    File logFile = new File(tempDir, "dataset-cloner.log");

    Environment env = Environment.builder()
        .withSchemaGraph(schemaGraph)
        .withDataSource(dataSource)
        .withLogger(new Logger(logFile))
        .build();

    BuildingCloner cloner = BuildingCloner.builder(env)
        .withBuildingId(1012)
        .withBeforeAllListener(() -> System.out.println("Starting building clone operation..."))
        .withAfterAllListener((elapsed) -> System.out
            .println("Finished building clone operation in " + ElapsedTimeUtils.format(elapsed)))
        .build();

    try {
      cloner.execute();
    } finally {
      cloner.rollback();
    }
  }

}
