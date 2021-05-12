package com.resolute.dataset.cloner.engine;

import static com.resolute.dataset.cloner.testutils.RecordAssertion.assertRecord;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.google.common.base.Charsets;
import com.resolute.database.crawler.model.Graph;
import com.resolute.dataset.cloner.integration.AbstractDatabaseTest;
import com.resolute.dataset.cloner.integration.IntegrationTestSuite;
import com.resolute.dataset.cloner.testutils.Records;
import com.resolute.dataset.cloner.utils.Key;
import com.resolute.jdbc.simple.DaoUtils;

public abstract class AbstractDatasetClonerHelperTest extends AbstractDatabaseTest {

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
  void test_compute_data_to_be_cloned_and_cleanup() {
    Graph subgraph = schemaGraph.getSubgraphReachableFrom("test1_tbl");
    File logFile = new File(tempDir, "dataset-cloner.log");
    Records records;

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
      helper.computeDataToBeCloned();

      // assert that temp tables exist
      assertThat(tableExists(getTempTableName(prefix, "test1_tbl"))).isTrue();
      assertThat(tableExists(getTempTableName(prefix, "test3_tbl"))).isTrue();
      assertThat(tableExists(getTempTableName(prefix, "test4_tbl"))).isTrue();
      assertThat(tableExists(getTempUnaryTableName(prefix, "test4_tbl"))).isTrue();
      assertThat(tableExists(getTempTableName(prefix, "test4_closure_tbl"))).isTrue();
      assertThat(tableExists(getTempTableName(prefix, "test10_tbl"))).isTrue();

      // assert contents of temp_test1_tbl
      records = select(getTempTableName(prefix, "test1_tbl"));
      assertThat(records.size()).isEqualTo(1);
      assertThat(records.findRecord(Key.of("id", 1))).isPresent();

      // assert contents of temp_test3_tbl
      records = select(getTempTableName(prefix, "test3_tbl"));
      assertThat(records.size()).isEqualTo(2);
      assertThat(records.findRecord(Key.of("test1_id", 1, "test2_id", 1))).isPresent();
      assertThat(records.findRecord(Key.of("test1_id", 1, "test2_id", 2))).isPresent();

      // assert contents of temp_test4_tbl
      records = select(getTempTableName(prefix, "test4_tbl"));
      assertThat(records.size()).isEqualTo(4);
      assertThat(records.findRecord(Key.of("id", 1))).isPresent();
      assertThat(records.findRecord(Key.of("id", 2))).isPresent();
      assertThat(records.findRecord(Key.of("id", 3))).isPresent();
      assertThat(records.findRecord(Key.of("id", 6))).isPresent();

      // assert contents of temp_test4_closure_tbl
      records = select(getTempTableName(prefix, "test4_closure_tbl"));
      assertThat(records.size()).isEqualTo(9);
      assertThat(records.findRecord(Key.of("parent_id", 1, "child_id", 1))).isPresent();
      assertThat(records.findRecord(Key.of("parent_id", 1, "child_id", 2))).isPresent();
      assertThat(records.findRecord(Key.of("parent_id", 1, "child_id", 3))).isPresent();
      assertThat(records.findRecord(Key.of("parent_id", 2, "child_id", 2))).isPresent();
      assertThat(records.findRecord(Key.of("parent_id", 2, "child_id", 3))).isPresent();
      assertThat(records.findRecord(Key.of("parent_id", 3, "child_id", 3))).isPresent();
      assertThat(records.findRecord(Key.of("parent_id", 4, "child_id", 6))).isPresent();
      assertThat(records.findRecord(Key.of("parent_id", 5, "child_id", 6))).isPresent();
      assertThat(records.findRecord(Key.of("parent_id", 6, "child_id", 6))).isPresent();

      // assert contents of temp_test10_tbl
      records = select(getTempTableName(prefix, "test10_tbl"));
      assertThat(records.size()).isEqualTo(0);

    } finally {
      helper.cleanup();
      assertThat(tableExists(getTempTableName(prefix, "test1_tbl"))).isFalse();
      assertThat(tableExists(getTempTableName(prefix, "test3_tbl"))).isFalse();
      assertThat(tableExists(getTempTableName(prefix, "test4_tbl"))).isFalse();
      assertThat(tableExists(getTempUnaryTableName(prefix, "test4_tbl"))).isFalse();
      assertThat(tableExists(getTempTableName(prefix, "test4_closure_tbl"))).isFalse();
      assertThat(tableExists(getTempTableName(prefix, "test10_tbl"))).isFalse();
    }
  }

  @Test
  void test_compute_data_to_be_cloned_where_root_node_has_unary_association() {
    File logFile = new File(tempDir, "dataset-cloner.log");
    Records records;

    DatasetClonerHelper helper = DatasetClonerHelper.builder(schemaGraph)
        .withDataSource(dataSource)
        .withSourceSet(sourceSetBuilder -> {
          sourceSetBuilder
              .withRootSelectStatement("test4_tbl", Key.of("id", 1));
        })
        .withLogFile(logFile)
        .withDebug(false)

        .build();

    int prefix = helper.getTableNamePrefix();

    try {
      helper.computeDataToBeCloned();

      // assert that temp tables exist
      assertThat(tableExists(getTempTableName(prefix, "test4_tbl"))).isTrue();
      assertThat(tableExists(getTempUnaryTableName(prefix, "test4_tbl"))).isTrue();
      assertThat(tableExists(getTempTableName(prefix, "test4_closure_tbl"))).isTrue();

      // assert contents of temp_test4_tbl
      records = select(getTempTableName(prefix, "test4_tbl"));
      assertThat(records.size()).isEqualTo(3);
      assertThat(records.findRecord(Key.of("id", 1))).isPresent();
      assertThat(records.findRecord(Key.of("id", 2))).isPresent();
      assertThat(records.findRecord(Key.of("id", 3))).isPresent();

      // assert contents of temp_test4_closure_tbl
      records = select(getTempTableName(prefix, "test4_closure_tbl"));
      assertThat(records.size()).isEqualTo(6);
      assertThat(records.findRecord(Key.of("parent_id", 1, "child_id", 1))).isPresent();
      assertThat(records.findRecord(Key.of("parent_id", 1, "child_id", 2))).isPresent();
      assertThat(records.findRecord(Key.of("parent_id", 1, "child_id", 3))).isPresent();
      assertThat(records.findRecord(Key.of("parent_id", 2, "child_id", 2))).isPresent();
      assertThat(records.findRecord(Key.of("parent_id", 2, "child_id", 3))).isPresent();
      assertThat(records.findRecord(Key.of("parent_id", 3, "child_id", 3))).isPresent();

      // assert contents of temp_test10_tbl
      records = select(getTempTableName(prefix, "test10_tbl"));
      assertThat(records.size()).isEqualTo(0);

    } finally {
      helper.cleanup();
      assertThat(tableExists(getTempTableName(prefix, "test4_tbl"))).isFalse();
      assertThat(tableExists(getTempUnaryTableName(prefix, "test4_tbl"))).isFalse();
      assertThat(tableExists(getTempTableName(prefix, "test4_closure_tbl"))).isFalse();
      assertThat(tableExists(getTempTableName(prefix, "test10_tbl"))).isFalse();
    }
  }

  @Test
  void test_compute_data_to_be_cloned_and_cleanup_with_multiple_source_sets() {
    Graph subgraph = schemaGraph.getSubgraphReachableFrom("test1_tbl");
    File logFile = new File(tempDir, "dataset-cloner.log");
    Records records;

    DatasetClonerHelper helper = DatasetClonerHelper.builder(schemaGraph)
        .withDataSource(dataSource)
        .withSourceSet(sourceSetBuilder -> {
          sourceSetBuilder
              .withGraph(subgraph)
              .withRootSelectStatement("test1_tbl", "SELECT id FROM test1_tbl WHERE id = 1");
        })
        .withSourceSet(sourceSetBuilder -> {
          sourceSetBuilder
              .withRootSelectStatement("test2_tbl",
                  tableNamePrefix -> "SELECT DISTINCT test2_id AS id "
                      + "FROM " + getTempTableName(tableNamePrefix, "test3_tbl"));
        })
        .withLogFile(logFile)
        .withDebug(false)
        .build();

    int prefix = helper.getTableNamePrefix();

    try {
      helper.computeDataToBeCloned();

      // assert that temp tables exist
      assertThat(tableExists(getTempTableName(prefix, "test1_tbl"))).isTrue();
      assertThat(tableExists(getTempTableName(prefix, "test3_tbl"))).isTrue();
      assertThat(tableExists(getTempTableName(prefix, "test4_tbl"))).isTrue();
      assertThat(tableExists(getTempUnaryTableName(prefix, "test4_tbl"))).isTrue();
      assertThat(tableExists(getTempTableName(prefix, "test4_closure_tbl"))).isTrue();
      assertThat(tableExists(getTempTableName(prefix, "test10_tbl"))).isTrue();
      assertThat(tableExists(getTempTableName(prefix, "test2_tbl"))).isTrue();

      // assert contents of temp_test1_tbl
      records = select(getTempTableName(prefix, "test1_tbl"));
      assertThat(records.size()).isEqualTo(1);
      assertThat(records.findRecord(Key.of("id", 1))).isPresent();

      // assert contents of temp_test3_tbl
      records = select(getTempTableName(prefix, "test3_tbl"));
      assertThat(records.size()).isEqualTo(2);
      assertThat(records.findRecord(Key.of("test1_id", 1, "test2_id", 1))).isPresent();
      assertThat(records.findRecord(Key.of("test1_id", 1, "test2_id", 2))).isPresent();

      // assert contents of temp_test4_tbl
      records = select(getTempTableName(prefix, "test4_tbl"));
      assertThat(records.size()).isEqualTo(4);
      assertThat(records.findRecord(Key.of("id", 1))).isPresent();
      assertThat(records.findRecord(Key.of("id", 2))).isPresent();
      assertThat(records.findRecord(Key.of("id", 3))).isPresent();
      assertThat(records.findRecord(Key.of("id", 6))).isPresent();

      // assert contents of temp_test4_closure_tbl
      records = select(getTempTableName(prefix, "test4_closure_tbl"));
      assertThat(records.size()).isEqualTo(9);
      assertThat(records.findRecord(Key.of("parent_id", 1, "child_id", 1))).isPresent();
      assertThat(records.findRecord(Key.of("parent_id", 1, "child_id", 2))).isPresent();
      assertThat(records.findRecord(Key.of("parent_id", 1, "child_id", 3))).isPresent();
      assertThat(records.findRecord(Key.of("parent_id", 2, "child_id", 2))).isPresent();
      assertThat(records.findRecord(Key.of("parent_id", 2, "child_id", 3))).isPresent();
      assertThat(records.findRecord(Key.of("parent_id", 3, "child_id", 3))).isPresent();
      assertThat(records.findRecord(Key.of("parent_id", 4, "child_id", 6))).isPresent();
      assertThat(records.findRecord(Key.of("parent_id", 5, "child_id", 6))).isPresent();
      assertThat(records.findRecord(Key.of("parent_id", 6, "child_id", 6))).isPresent();

      // assert contents of temp_test10_tbl
      records = select(getTempTableName(prefix, "test10_tbl"));
      assertThat(records.size()).isEqualTo(0);

      // assert contents of temp_test2_tbl
      records = select(getTempTableName(prefix, "test2_tbl"));
      assertThat(records.size()).isEqualTo(2);
      assertThat(records.findRecord(Key.of("id", 1))).isPresent();
      assertThat(records.findRecord(Key.of("id", 2))).isPresent();



    } finally {
      helper.cleanup();
      assertThat(tableExists(getTempTableName(prefix, "test1_tbl"))).isFalse();
      assertThat(tableExists(getTempTableName(prefix, "test3_tbl"))).isFalse();
      assertThat(tableExists(getTempTableName(prefix, "test4_tbl"))).isFalse();
      assertThat(tableExists(getTempUnaryTableName(prefix, "test4_tbl"))).isFalse();
      assertThat(tableExists(getTempTableName(prefix, "test4_closure_tbl"))).isFalse();
      assertThat(tableExists(getTempTableName(prefix, "test10_tbl"))).isFalse();
      assertThat(tableExists(getTempTableName(prefix, "test2_tbl"))).isFalse();
    }
  }

  @Test
  void test_execute() throws IOException, SQLException {
    Graph subgraph = schemaGraph.getSubgraphReachableFrom("test1_tbl");
    File logFile = new File(tempDir, "dataset-cloner.log");
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
        helper.rollback();

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

  @Test
  void test_execute_with_multiple_copies() throws IOException, SQLException {
    Graph subgraph = schemaGraph.getSubgraphReachableFrom("test4_tbl");
    File logFile = new File(tempDir, "dataset-cloner.log");
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
        helper.rollback(); // NOTE rollback only rolls back the most recent copy!

        assertThat(tableExists(getTempTableName(prefix, "test1_tbl"))).isFalse();
        assertThat(tableExists(getTempTableName(prefix, "test3_tbl"))).isFalse();
        assertThat(tableExists(getTempTableName(prefix, "test4_tbl"))).isFalse();
        assertThat(tableExists(getTempUnaryTableName(prefix, "test4_tbl"))).isFalse();
        assertThat(tableExists(getTempTableName(prefix, "test4_closure_tbl"))).isFalse();
        assertThat(tableExists(getTempTableName(prefix, "test10_tbl"))).isFalse();

        // assert contents of test4_tbl
        records = select("test4_tbl");
        assertThat(records.size()).isEqualTo(8);
        assertThat(records.findRecord(Key.of("id", 9))).isEmpty();

        // assert contents of test4_closure_tbl
        records = select("test4_closure_tbl");
        assertThat(records.size()).isEqualTo(18);
        assertThat(records.findRecord(Key.of("parent_id", 4, "child_id", 9))).isEmpty();
        assertThat(records.findRecord(Key.of("parent_id", 5, "child_id", 9))).isEmpty();
        assertThat(records.findRecord(Key.of("parent_id", 9, "child_id", 9))).isEmpty();

      } finally {
        cleanupAfterClone();
      }
    }
  }

  @Test
  void test_execute_with_partitioned_inserts_and_deletes() throws IOException, SQLException {
    Graph subgraph = schemaGraph.getSubgraphReachableFrom("test1_tbl");
    File logFile = new File(tempDir, "dataset-cloner.log");
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
        .withMaxRecsPerInsert(1)
        .withMaxRecsPerDelete(1)
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
          .hasFieldValue("test1_id", 2)
          .hasFieldValue("parent_id", 7)
          .hasFieldValue("name", "Two - Two_" + prefix + "_1");

      record = assertPresent(records.findRecord(Key.of("id", 9)));
      assertRecord(record)
          .hasFieldValue("id", 9)
          .hasFieldValue("test1_id", 2)
          .hasFieldValue("parent_id", 8)
          .hasFieldValue("name", "Three - Two_" + prefix + "_1");

      record = assertPresent(records.findRecord(Key.of("id", 10)));
      assertRecord(record)
          .hasFieldValue("id", 10)
          .hasFieldValue("test1_id", 3)
          .hasFieldValue("parent_id", 5)
          .hasFieldValue("name", "Six - One_" + prefix + "_1");


      // assert contents of test4_closure_tbl
      records = select("test4_closure_tbl");
      assertThat(records.size()).isEqualTo(21);

      record = assertPresent(records.findRecord(Key.of("parent_id", 7, "child_id", 7)));
      assertRecord(record)
          .hasFieldValue("parent_id", 7)
          .hasFieldValue("child_id", 7)
          .hasFieldValue("depth", 0);

      record = assertPresent(records.findRecord(Key.of("parent_id", 7, "child_id", 8)));
      assertRecord(record)
          .hasFieldValue("parent_id", 7)
          .hasFieldValue("child_id", 8)
          .hasFieldValue("depth", 1);

      record = assertPresent(records.findRecord(Key.of("parent_id", 7, "child_id", 9)));
      assertRecord(record)
          .hasFieldValue("parent_id", 7)
          .hasFieldValue("child_id", 9)
          .hasFieldValue("depth", 2);

      record = assertPresent(records.findRecord(Key.of("parent_id", 8, "child_id", 8)));
      assertRecord(record)
          .hasFieldValue("parent_id", 8)
          .hasFieldValue("child_id", 8)
          .hasFieldValue("depth", 0);

      record = assertPresent(records.findRecord(Key.of("parent_id", 8, "child_id", 9)));
      assertRecord(record)
          .hasFieldValue("parent_id", 8)
          .hasFieldValue("child_id", 9)
          .hasFieldValue("depth", 1);

      record = assertPresent(records.findRecord(Key.of("parent_id", 10, "child_id", 10)));
      assertRecord(record)
          .hasFieldValue("parent_id", 10)
          .hasFieldValue("child_id", 10)
          .hasFieldValue("depth", 0);

      record = assertPresent(records.findRecord(Key.of("parent_id", 4, "child_id", 10)));
      assertRecord(record)
          .hasFieldValue("parent_id", 4)
          .hasFieldValue("child_id", 10)
          .hasFieldValue("depth", 2);

      record = assertPresent(records.findRecord(Key.of("parent_id", 5, "child_id", 10)));
      assertRecord(record)
          .hasFieldValue("parent_id", 5)
          .hasFieldValue("child_id", 10)
          .hasFieldValue("depth", 1);

      record = assertPresent(records.findRecord(Key.of("parent_id", 10, "child_id", 10)));
      assertRecord(record)
          .hasFieldValue("parent_id", 10)
          .hasFieldValue("child_id", 10)
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
        helper.rollback();

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

  @Test
  void test_execute_where_root_table_has_compound_primary_key() throws IOException, SQLException {
    Graph subgraph = schemaGraph.getSubgraphReachableFrom("test11_tbl");
    File logFile = new File(tempDir, "dataset-cloner.log");
    Records records;
    Map<String, String> record;

    DatasetClonerHelper helper = DatasetClonerHelper.builder(schemaGraph)
        .withDataSource(dataSource)
        .withSourceSet(sourceSetBuilder -> {
          sourceSetBuilder
              .withGraph(subgraph)
              .withRootSelectStatement("test11_tbl", Key.of("id1", 3905, "id2", 7))
              // Below, we test to see if a mutator applied to a non-index field is honored!
              .withFieldLevelMutator("test11_tbl", "description", FieldLevelMutator.DEFAULT);
        })
        .withLogFile(logFile)
        .withDebug(false)
        .withNumberOfCopies(1)
        .build();

    int prefix = helper.getTableNamePrefix();

    try {
      helper.execute();

      // assert contents of test11_tbl
      records = select("test11_tbl");
      assertThat(records.size()).isEqualTo(5);

      record = assertPresent(records.findRecord(Key.of("id1", 3912, "id2", 8)));
      assertRecord(record)
          .hasFieldValue("id1", 3912)
          .hasFieldValue("id2", 8)
          .hasFieldValue("name", "Record 1_" + prefix + "_1")
          .hasFieldValue("description", "Description 1_" + prefix + "_1");

      // assert contents of test12_tbl
      records = select("test12_tbl");
      assertThat(records.size()).isEqualTo(5);

      record = assertPresent(records.findRecord(Key.of("test11_id1", 3912, "test11_id2", 8)));
      assertRecord(record)
          .hasFieldValue("test11_id1", 3912)
          .hasFieldValue("test11_id2", 8)
          .hasFieldValue("name", "Record 1_" + prefix + "_1");

    } finally {
      try {
        helper.rollback();
      } finally {
        cleanupAfterClone();
      }
    }
  }

  @Test
  void test_execute_with_pure_copy_mode() throws IOException, SQLException {
    Graph subgraph = schemaGraph.getSubgraphReachableFrom("test11_tbl");
    File logFile = new File(tempDir, "dataset-cloner.log");
    File outputFile = new File(tempDir, "dataset-cloner.sql");
    Records records;

    DatasetClonerHelper helper = DatasetClonerHelper.builder(schemaGraph)
        .withDataSource(dataSource)
        .withSourceSet(sourceSetBuilder -> {
          sourceSetBuilder
              .withGraph(subgraph)
              .withRootSelectStatement("test11_tbl", Key.of("id1", 3905, "id2", 7));
        })
        .withLogFile(logFile)
        .withDebug(false)
        .withNumberOfCopies(1)
        .withPureCopyMode(true)
        .withOutputFile(outputFile)
        .build();

    try {
      helper.execute();

      // assert contents of test11_tbl
      records = select("test11_tbl");
      assertThat(records.size()).isEqualTo(4);

      // assert contents of test12_tbl
      records = select("test12_tbl");
      assertThat(records.size()).isEqualTo(4);

      assertThat(outputFile).exists();
      String actual = new String(Files.readAllBytes(outputFile.toPath()), Charsets.UTF_8).trim();

      // @formatter:off
      String expected = 
          "SET session_replication_role = replica;\n" + 
          "\n" + 
          "-- Inserting records into table test11_tbl\n" + 
          "\n" + 
          "INSERT INTO test11_tbl (\"id1\", \"id2\", \"name\", \"description\") VALUES\n" + 
          "      (3905, 7, 'Record 1', 'Description 1') RETURNING id1, id2;\n" + 
          "\n" + 
          "-- Inserting records into table test12_tbl\n" + 
          "\n" + 
          "INSERT INTO test12_tbl (\"test11_id1\", \"test11_id2\", \"name\") VALUES\n" + 
          "      (3905, 7, 'Record 1') RETURNING test11_id1, test11_id2;";
      // @formatter:on

      assertThat(actual).isEqualTo(expected);


    } finally {
      cleanupAfterClone();
    }
  }

  @Test
  void test_log_file_contents_after_execute() throws IOException, SQLException {
    Graph subgraph = schemaGraph.getSubgraphReachableFrom("test1_tbl");
    File logFile = new File(tempDir, "dataset-cloner.log");

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

      // Assert that the log file has the following contents
      assertThat(logFile).hasContent(">>>>TABLE NAME PREFIX: " + prefix + "\n" +
          ">>>>INSERT INTO TABLE: test1_tbl\n" +
          "3\n" +
          ">>>>INSERT INTO TABLE: test4_tbl\n" +
          "7\n" +
          "8\n" +
          "9\n" +
          "10\n" +
          ">>>>INSERT INTO TABLE: test4_closure_tbl\n" +
          "7,7\n" +
          "7,9\n" +
          "7,10\n" +
          "9,9\n" +
          "9,10\n" +
          "10,10\n" +
          "4,8\n" +
          "5,8\n" +
          "8,8\n" +
          ">>>>INSERT INTO TABLE: test3_tbl\n" +
          "3,1\n" +
          "3,2\n" +
          ">>>>INSERT INTO TABLE: test10_tbl");
    } finally {
      try {
        helper.rollback();
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
