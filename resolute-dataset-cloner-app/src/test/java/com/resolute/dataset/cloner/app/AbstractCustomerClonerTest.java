package com.resolute.dataset.cloner.app;

import static com.resolute.dataset.cloner.app.testutils.InitTableSize.getAcTagTableSize;
import static com.resolute.dataset.cloner.app.testutils.InitTableSize.resetAcTagIdSequence;
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
import com.resolute.dataset.cloner.app.testutils.CustomerCloneAssertions;
import com.resolute.dataset.cloner.log.Logger;
import com.resolute.jdbc.simple.DaoUtils;
import com.resolute.utils.simple.ElapsedTimeUtils;

public abstract class AbstractCustomerClonerTest extends AbstractDatabaseTest {

  @TempDir
  File tempDir;

  private int acTags;

  @BeforeEach
  void seedTestTables() throws IOException, SQLException {
    DaoUtils.executeSqlScript(dataSource, IntegrationTestSuite.class,
        "base-resolute-data.sql");
    // The initial number of records in ac_tag_tbl will change as more global tags are added
    // so we have to retrieve it dynamically
    this.acTags = getAcTagTableSize(statementFactory);
  }

  @AfterEach
  void cleanupTestTables() throws IOException, SQLException {
    DaoUtils.executeSqlScript(dataSource, IntegrationTestSuite.class,
        "base-resolute-teardown.sql");
    resetAcTagIdSequence(statementFactory, acTags - 2 + 1);
  }

  @Test
  void test_execute() {
    File logFile = new File(tempDir, "dataset-cloner.log");

    Environment env = Environment.builder()
        .withSchemaGraph(schemaGraph)
        .withDataSource(dataSource)
        .withLogger(new Logger(logFile))
        .withDebug(false)
        .build();

    CustomerCloner cloner = CustomerCloner.builder(env)
        .withCustomerId(1)
        .withBeforeAllListener(() -> System.out.println("Starting customer clone operation..."))
        .withAfterAllListener((elapsed) -> System.out
            .println("Finished customer clone operation in " + ElapsedTimeUtils.format(elapsed)))
        .build();

    int prefix = cloner.getTableNamePrefix();

    assertThat(cloner.getOrphanedSuperclassNodes())
        .as("CustomerCloner contains the following orphaned superclass nodes: "
            + cloner.getOrphanedSuperclassNodes().keySet().stream().map(Node::getName)
                .collect(Collectors.joining(", ")))
        .isEmpty();

    try {
      cloner.execute();

      new CustomerCloneAssertions(this, acTags).assertStateAfterClone(prefix);

    } finally {
      cloner.rollback();
    }
  }

}
