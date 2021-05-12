package com.resolute.dataset.cloner.engine;

import static com.resolute.dataset.cloner.testutils.RecordAssertion.assertRecord;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.resolute.database.crawler.model.Graph;
import com.resolute.dataset.cloner.integration.AbstractDatabaseTest;
import com.resolute.dataset.cloner.integration.IntegrationTestSuite;
import com.resolute.dataset.cloner.testutils.Records;
import com.resolute.dataset.cloner.utils.Key;
import com.resolute.jdbc.simple.DaoUtils;

public abstract class AbstractRollbackOperationTest extends AbstractDatabaseTest {

  @TempDir
  File tempDir;

  @BeforeAll
  static void seedTestTables() throws IOException, SQLException {
    DaoUtils.executeSqlScript(dataSource, IntegrationTestSuite.class,
        "base-test-data.sql");
    DaoUtils.executeSqlScript(dataSource, IntegrationTestSuite.class,
        "clone-operation-data.sql");
  }

  @AfterAll
  static void cleanupTestTables() throws IOException, SQLException {
    DaoUtils.executeSqlScript(dataSource, IntegrationTestSuite.class,
        "clone-operation-teardown.sql");
    DaoUtils.executeSqlScript(dataSource, IntegrationTestSuite.class,
        "base-test-teardown.sql");
  }

  @Test
  void test_rollback_from_log_file() throws IOException, SQLException {
    Graph subgraph = schemaGraph.getSubgraphReachableFrom("test1_tbl");
    File logFile = new File(tempDir, "dataset-cloner.log");

    assertResults1(subgraph, logFile, () -> {
      RollbackOperation.forGraph(schemaGraph)
          .withDataSource(dataSource)
          .executeFromLogFile(logFile, 1);
    });
  }

  @Test
  void test_rollback_from_log_file_containing_multiple_copies() throws IOException, SQLException {
    Graph subgraph = schemaGraph.getSubgraphReachableFrom("test4_tbl");
    File logFile = new File(tempDir, "dataset-cloner.log");

    assertResults2(subgraph, logFile, () -> {
      RollbackOperation.forGraph(schemaGraph)
          .withDataSource(dataSource)
          .executeFromLogFile(logFile, 2);
    });
  }

  private void assertResults1(Graph subgraph, File logFile, Runnable testCode)
      throws IOException, SQLException {
    Records records;
    Map<String, String> record;

    DatasetClonerHelper helper = DatasetClonerHelper.builder(schemaGraph)
        .withDataSource(dataSource)
        .withSourceSet(sourceSetBuilder -> {
          sourceSetBuilder
              .withGraph(subgraph)
              .withRootSelectStatement("test1_tbl", "SELECT id FROM test1_tbl WHERE id = 1");
        })
        .withLogFile(logFile)
        .withDebug(false)
        .build();

    int prefix = helper.getTableNamePrefix();

    try {
      helper.execute();

      // assert contents of test1_tbl
      records = select("test1_tbl");
      assertThat(records.size()).isEqualTo(3);

      record = assertPresent(records.findRecord(Key.of("id", 3)));
      assertRecord(record)
          .hasFieldValue("id", 3)
          .hasFieldValue("name", "One_" + prefix + "_1");

      // assert contents of test3_tbl
      records = select("test3_tbl");
      assertThat(records.size()).isEqualTo(6);

      record = assertPresent(records.findRecord(Key.of("test1_id", 3, "test2_id", 1)));
      assertRecord(record)
          .hasFieldValue("test1_id", 3)
          .hasFieldValue("test2_id", 1);


      record = assertPresent(records.findRecord(Key.of("test1_id", 3, "test2_id", 2)));
      assertRecord(record)
          .hasFieldValue("test1_id", 3)
          .hasFieldValue("test2_id", 2);


      // assert contents of test4_tbl
      records = select("test4_tbl");
      assertThat(records.size()).isEqualTo(10);

      record = assertPresent(records.findRecord(Key.of("id", 7)));
      assertRecord(record)
          .hasFieldValue("id", 7)
          .hasFieldValue("test1_id", 3)
          .hasFieldValue("parent_id", null)
          .hasFieldValue("name", "One - One_" + prefix + "_1");

      record = assertPresent(records.findRecord(Key.of("id", 8)));
      assertRecord(record)
          .hasFieldValue("id", 8)
          .hasFieldValue("test1_id", 3)
          .hasFieldValue("parent_id", 5)
          .hasFieldValue("name", "Six - One_" + prefix + "_1");

      record = assertPresent(records.findRecord(Key.of("id", 9)));
      assertRecord(record)
          .hasFieldValue("id", 9)
          .hasFieldValue("test1_id", 2)
          .hasFieldValue("parent_id", 7)
          .hasFieldValue("name", "Two - Two_" + prefix + "_1");

      record = assertPresent(records.findRecord(Key.of("id", 10)));
      assertRecord(record)
          .hasFieldValue("id", 10)
          .hasFieldValue("test1_id", 2)
          .hasFieldValue("parent_id", 9)
          .hasFieldValue("name", "Three - Two_" + prefix + "_1");

      // assert contents of test4_closure_tbl
      records = select("test4_closure_tbl");
      assertThat(records.size()).isEqualTo(21);

      record = assertPresent(records.findRecord(Key.of("parent_id", 7, "child_id", 7)));
      assertRecord(record)
          .hasFieldValue("parent_id", 7)
          .hasFieldValue("child_id", 7)
          .hasFieldValue("depth", 0);

      record = assertPresent(records.findRecord(Key.of("parent_id", 7, "child_id", 9)));
      assertRecord(record)
          .hasFieldValue("parent_id", 7)
          .hasFieldValue("child_id", 9)
          .hasFieldValue("depth", 1);

      record = assertPresent(records.findRecord(Key.of("parent_id", 7, "child_id", 10)));
      assertRecord(record)
          .hasFieldValue("parent_id", 7)
          .hasFieldValue("child_id", 10)
          .hasFieldValue("depth", 2);

      record = assertPresent(records.findRecord(Key.of("parent_id", 9, "child_id", 9)));
      assertRecord(record)
          .hasFieldValue("parent_id", 9)
          .hasFieldValue("child_id", 9)
          .hasFieldValue("depth", 0);

      record = assertPresent(records.findRecord(Key.of("parent_id", 9, "child_id", 10)));
      assertRecord(record)
          .hasFieldValue("parent_id", 9)
          .hasFieldValue("child_id", 10)
          .hasFieldValue("depth", 1);

      record = assertPresent(records.findRecord(Key.of("parent_id", 10, "child_id", 10)));
      assertRecord(record)
          .hasFieldValue("parent_id", 10)
          .hasFieldValue("child_id", 10)
          .hasFieldValue("depth", 0);

      record = assertPresent(records.findRecord(Key.of("parent_id", 4, "child_id", 8)));
      assertRecord(record)
          .hasFieldValue("parent_id", 4)
          .hasFieldValue("child_id", 8)
          .hasFieldValue("depth", 2);

      record = assertPresent(records.findRecord(Key.of("parent_id", 5, "child_id", 8)));
      assertRecord(record)
          .hasFieldValue("parent_id", 5)
          .hasFieldValue("child_id", 8)
          .hasFieldValue("depth", 1);

      record = assertPresent(records.findRecord(Key.of("parent_id", 8, "child_id", 8)));
      assertRecord(record)
          .hasFieldValue("parent_id", 8)
          .hasFieldValue("child_id", 8)
          .hasFieldValue("depth", 0);


      // assert contents of test5_tbl (should not have changed!)
      records = select("test5_tbl");
      assertThat(records.size()).isEqualTo(2);

      // assert contents of test6_tbl (should not have changed!)
      records = select("test6_tbl");
      assertThat(records.size()).isEqualTo(2);

      // assert contents of test7_tbl (should not have changed!)
      records = select("test7_tbl");
      assertThat(records.size()).isEqualTo(2);

      // assert contents of test8_tbl (should not have changed!)
      records = select("test8_tbl");
      assertThat(records.size()).isEqualTo(3);

      // assert contents of test9_tbl (should not have changed!)
      records = select("test9_tbl");
      assertThat(records.size()).isEqualTo(3);

      // assert contents of test10_tbl (should not have changed!)
      records = select("test10_tbl");
      assertThat(records.size()).isEqualTo(0);

    } finally {
      try {

        // Here is the real thing we are trying to test!
        testCode.run();

        assertThat(tableExists(getTempTableName(prefix, "test1_tbl"))).isFalse();
        assertThat(tableExists(getTempTableName(prefix, "test3_tbl"))).isFalse();
        assertThat(tableExists(getTempTableName(prefix, "test4_tbl"))).isFalse();
        assertThat(tableExists(getTempUnaryTableName(prefix, "test4_tbl"))).isFalse();
        assertThat(tableExists(getTempTableName(prefix, "test4_closure_tbl"))).isFalse();
        assertThat(tableExists(getTempTableName(prefix, "test10_tbl"))).isFalse();

        records = select("test1_tbl");
        assertThat(records.size()).isEqualTo(2);
        assertThat(records.findRecord(Key.of("id", 3))).isEmpty();

        records = select("test3_tbl");
        assertThat(records.size()).isEqualTo(4);
        assertThat(records.findRecord(Key.of("test1_id", 3, "test2_id", 1))).isEmpty();
        assertThat(records.findRecord(Key.of("test1_id", 3, "test2_id", 2))).isEmpty();

        records = select("test4_tbl");
        assertThat(records.size()).isEqualTo(6);
        assertThat(records.findRecord(Key.of("id", 7))).isEmpty();
        assertThat(records.findRecord(Key.of("id", 8))).isEmpty();
        assertThat(records.findRecord(Key.of("id", 9))).isEmpty();
        assertThat(records.findRecord(Key.of("id", 10))).isEmpty();

        records = select("test4_closure_tbl");
        assertThat(records.size()).isEqualTo(12);
        assertThat(records.findRecord(Key.of("parent_id", 7, "child_id", 7))).isEmpty();
        assertThat(records.findRecord(Key.of("parent_id", 7, "child_id", 9))).isEmpty();
        assertThat(records.findRecord(Key.of("parent_id", 7, "child_id", 10))).isEmpty();
        assertThat(records.findRecord(Key.of("parent_id", 9, "child_id", 9))).isEmpty();
        assertThat(records.findRecord(Key.of("parent_id", 9, "child_id", 10))).isEmpty();
        assertThat(records.findRecord(Key.of("parent_id", 10, "child_id", 10))).isEmpty();
        assertThat(records.findRecord(Key.of("parent_id", 4, "child_id", 8))).isEmpty();
        assertThat(records.findRecord(Key.of("parent_id", 5, "child_id", 8))).isEmpty();
        assertThat(records.findRecord(Key.of("parent_id", 8, "child_id", 8))).isEmpty();

      } finally {
        cleanupAfterClone();
      }
    }
  }

  private void assertResults2(Graph subgraph, File logFile, Runnable testCode)
      throws IOException, SQLException {
    Records records;
    Map<String, String> record;

    DatasetClonerHelper helper = DatasetClonerHelper.builder(schemaGraph)
        .withDataSource(dataSource)
        .withSourceSet(sourceSetBuilder -> {
          sourceSetBuilder
              .withGraph(subgraph)
              .withRootSelectStatement("test4_tbl", "SELECT id FROM test4_tbl WHERE id = 6");
        })
        .withLogFile(logFile)
        .withDebug(false)
        .withNumberOfCopies(3)
        .build();

    int prefix = helper.getTableNamePrefix();

    try {
      helper.execute();

      // assert contents of test1_tbl (should not have changed!)
      records = select("test1_tbl");
      assertThat(records.size()).isEqualTo(2);

      // assert contents of test3_tbl (should not have changed!)
      records = select("test3_tbl");
      assertThat(records.size()).isEqualTo(4);

      // assert contents of test4_tbl
      records = select("test4_tbl");
      assertThat(records.size()).isEqualTo(9);

      record = assertPresent(records.findRecord(Key.of("id", 7)));
      assertRecord(record)
          .hasFieldValue("id", 7)
          .hasFieldValue("test1_id", 1)
          .hasFieldValue("parent_id", 5)
          .hasFieldValue("name", "Six - One_" + prefix + "_1");

      record = assertPresent(records.findRecord(Key.of("id", 8)));
      assertRecord(record)
          .hasFieldValue("id", 8)
          .hasFieldValue("test1_id", 1)
          .hasFieldValue("parent_id", 5)
          .hasFieldValue("name", "Six - One_" + prefix + "_2");

      record = assertPresent(records.findRecord(Key.of("id", 9)));
      assertRecord(record)
          .hasFieldValue("id", 9)
          .hasFieldValue("test1_id", 1)
          .hasFieldValue("parent_id", 5)
          .hasFieldValue("name", "Six - One_" + prefix + "_3");

      // assert contents of test4_closure_tbl
      records = select("test4_closure_tbl");
      assertThat(records.size()).isEqualTo(21);

      record = assertPresent(records.findRecord(Key.of("parent_id", 4, "child_id", 7)));
      assertRecord(record)
          .hasFieldValue("parent_id", 4)
          .hasFieldValue("child_id", 7)
          .hasFieldValue("depth", 2);

      record = assertPresent(records.findRecord(Key.of("parent_id", 5, "child_id", 7)));
      assertRecord(record)
          .hasFieldValue("parent_id", 5)
          .hasFieldValue("child_id", 7)
          .hasFieldValue("depth", 1);

      record = assertPresent(records.findRecord(Key.of("parent_id", 7, "child_id", 7)));
      assertRecord(record)
          .hasFieldValue("parent_id", 7)
          .hasFieldValue("child_id", 7)
          .hasFieldValue("depth", 0);

      record = assertPresent(records.findRecord(Key.of("parent_id", 4, "child_id", 8)));
      assertRecord(record)
          .hasFieldValue("parent_id", 4)
          .hasFieldValue("child_id", 8)
          .hasFieldValue("depth", 2);

      record = assertPresent(records.findRecord(Key.of("parent_id", 5, "child_id", 8)));
      assertRecord(record)
          .hasFieldValue("parent_id", 5)
          .hasFieldValue("child_id", 8)
          .hasFieldValue("depth", 1);

      record = assertPresent(records.findRecord(Key.of("parent_id", 8, "child_id", 8)));
      assertRecord(record)
          .hasFieldValue("parent_id", 8)
          .hasFieldValue("child_id", 8)
          .hasFieldValue("depth", 0);

      record = assertPresent(records.findRecord(Key.of("parent_id", 4, "child_id", 9)));
      assertRecord(record)
          .hasFieldValue("parent_id", 4)
          .hasFieldValue("child_id", 9)
          .hasFieldValue("depth", 2);

      record = assertPresent(records.findRecord(Key.of("parent_id", 5, "child_id", 9)));
      assertRecord(record)
          .hasFieldValue("parent_id", 5)
          .hasFieldValue("child_id", 9)
          .hasFieldValue("depth", 1);

      record = assertPresent(records.findRecord(Key.of("parent_id", 9, "child_id", 9)));
      assertRecord(record)
          .hasFieldValue("parent_id", 9)
          .hasFieldValue("child_id", 9)
          .hasFieldValue("depth", 0);

      // assert contents of test5_tbl (should not have changed!)
      records = select("test5_tbl");
      assertThat(records.size()).isEqualTo(2);

      // assert contents of test6_tbl (should not have changed!)
      records = select("test6_tbl");
      assertThat(records.size()).isEqualTo(2);

      // assert contents of test7_tbl (should not have changed!)
      records = select("test7_tbl");
      assertThat(records.size()).isEqualTo(2);

      // assert contents of test8_tbl (should not have changed!)
      records = select("test8_tbl");
      assertThat(records.size()).isEqualTo(3);

      // assert contents of test9_tbl (should not have changed!)
      records = select("test9_tbl");
      assertThat(records.size()).isEqualTo(3);

      // assert contents of test10_tbl (should not have changed!)
      records = select("test10_tbl");
      assertThat(records.size()).isEqualTo(0);

    } finally {
      try {
        // Here is the real thing we are trying to test!
        testCode.run();

        assertThat(tableExists(getTempTableName(prefix, "test1_tbl"))).isFalse();
        assertThat(tableExists(getTempTableName(prefix, "test3_tbl"))).isFalse();
        assertThat(tableExists(getTempTableName(prefix, "test4_tbl"))).isFalse();
        assertThat(tableExists(getTempUnaryTableName(prefix, "test4_tbl"))).isFalse();
        assertThat(tableExists(getTempTableName(prefix, "test4_closure_tbl"))).isFalse();
        assertThat(tableExists(getTempTableName(prefix, "test10_tbl"))).isFalse();

        // assert contents of test4_tbl
        records = select("test4_tbl");
        assertThat(records.size()).isEqualTo(6);
        assertThat(records.findRecord(Key.of("id", 7))).isEmpty();
        assertThat(records.findRecord(Key.of("id", 8))).isEmpty();
        assertThat(records.findRecord(Key.of("id", 9))).isEmpty();

        // assert contents of test4_closure_tbl
        records = select("test4_closure_tbl");
        assertThat(records.size()).isEqualTo(12);
        assertThat(records.findRecord(Key.of("parent_id", 4, "child_id", 7))).isEmpty();
        assertThat(records.findRecord(Key.of("parent_id", 5, "child_id", 7))).isEmpty();
        assertThat(records.findRecord(Key.of("parent_id", 7, "child_id", 7))).isEmpty();
        assertThat(records.findRecord(Key.of("parent_id", 4, "child_id", 8))).isEmpty();
        assertThat(records.findRecord(Key.of("parent_id", 5, "child_id", 8))).isEmpty();
        assertThat(records.findRecord(Key.of("parent_id", 8, "child_id", 8))).isEmpty();
        assertThat(records.findRecord(Key.of("parent_id", 4, "child_id", 9))).isEmpty();
        assertThat(records.findRecord(Key.of("parent_id", 5, "child_id", 9))).isEmpty();
        assertThat(records.findRecord(Key.of("parent_id", 9, "child_id", 9))).isEmpty();

      } finally {
        cleanupAfterClone();
      }
    }
  }


  private void cleanupAfterClone() throws IOException, SQLException {
    DaoUtils.executeSqlScript(dataSource, IntegrationTestSuite.class,
        "clone-operation-teardown.sql");
    DaoUtils.executeSqlScript(dataSource, IntegrationTestSuite.class,
        "base-test-teardown.sql");
    DaoUtils.executeSqlScript(dataSource, IntegrationTestSuite.class,
        "base-test-data.sql");
    DaoUtils.executeSqlScript(dataSource, IntegrationTestSuite.class,
        "clone-operation-data.sql");
  }

}
