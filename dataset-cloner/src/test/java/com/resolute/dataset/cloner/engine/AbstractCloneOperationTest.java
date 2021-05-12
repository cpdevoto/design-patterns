package com.resolute.dataset.cloner.engine;

import static com.resolute.dataset.cloner.testutils.RecordAssertion.assertRecord;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.resolute.database.crawler.model.ForeignKey;
import com.resolute.database.crawler.model.ForeignKeyField;
import com.resolute.database.crawler.model.Graph;
import com.resolute.database.crawler.model.IgnoredEdge;
import com.resolute.database.crawler.model.Node;
import com.resolute.dataset.cloner.integration.AbstractDatabaseTest;
import com.resolute.dataset.cloner.integration.IntegrationTestSuite;
import com.resolute.dataset.cloner.log.Logger;
import com.resolute.dataset.cloner.testutils.Records;
import com.resolute.dataset.cloner.utils.Key;
import com.resolute.dataset.cloner.utils.KeyMaps;
import com.resolute.jdbc.simple.DaoUtils;

public abstract class AbstractCloneOperationTest extends AbstractDatabaseTest {

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
  void test_execute() {
    int tableNamePrefix = 1234;
    String tableName = "node_tbl2";

    Graph subgraph = schemaGraph.getSubgraphReachableFrom(tableName);
    Node node = subgraph.getNode(tableName).get();
    KeyMaps keyMaps = new KeyMaps();

    assertStateBefore();

    CloneOperation operation = CloneOperation.forNode(node)
        .withDataSource(dataSource)
        .withTableNamePrefix(tableNamePrefix)
        .withKeyMaps(keyMaps)
        .withFieldLevelMutators(FieldLevelMutators.builder()
            .withFieldLevelMutator("name",
                (prefix, copyNumber, value) -> value + " " + prefix + "_" + copyNumber)
            .build())
        .withTupleLevelMutator(c -> c.setValue("display_name", c.getValue("name")))
        .withDebug(false)
        .build();

    operation.execute();

    assertStateAfter();
  }

  @Test
  void test_execute_with_pure_copy_mode() throws IOException {
    int tableNamePrefix = 1234;
    String tableName = "node_tbl2";

    Graph subgraph = schemaGraph.getSubgraphReachableFrom(tableName);
    Node node = subgraph.getNode(tableName).get();
    KeyMaps keyMaps = new KeyMaps();
    File outputFile = new File(tempDir, "dataset-cloner.sql");

    assertStateBefore();

    CloneOperation operation = CloneOperation.forNode(node)
        .withDataSource(dataSource)
        .withTableNamePrefix(tableNamePrefix)
        .withKeyMaps(keyMaps)
        .withFieldLevelMutators(FieldLevelMutators.builder()
            .withFieldLevelMutator("name",
                (prefix, copyNumber, value) -> value + " " + prefix + "_" + copyNumber)
            .build())
        .withTupleLevelMutator(c -> c.setValue("display_name", c.getValue("name")))
        .withDebug(false)
        .withPureCopyMode(true) // configure pure copy mode
        .withOutputFile(new Logger(outputFile)) // configure output file
        .build();

    operation.execute();

    assertStateBefore();

    assertThat(outputFile).exists();
    String actual = new String(Files.readAllBytes(outputFile.toPath()), Charsets.UTF_8).trim();

    // @formatter:off
    String expected = 
        "-- Inserting records into table node_tbl2\n" + 
        "\n" + 
        "INSERT INTO node_tbl2 (\"id\", \"uuid\", \"customer_id\", \"node_type_id\", \"parent_id\", \"name\", \"display_name\", \"created_at\", \"updated_at\") VALUES\n" + 
        "      (2, '4b36afc8-5205-49c1-af16-4dc6f96db983', 1, 3, 1, 'Building 1', 'Building 1', '2021-03-03 02:03:04.0', '2021-03-03 02:03:04.0'),\n" + 
        "      (4, '4b36afc8-5205-49c1-af16-4dc6f96db985', 1, 5, 2, 'Building 1 - Floor 1', 'Building 1 - Floor 1', '2021-03-03 02:03:04.0', '2021-03-03 02:03:04.0'),\n" + 
        "      (5, '4b36afc8-5205-49c1-af16-4dc6f96db986', 1, 5, 2, 'Building 1 - Floor 2', 'Building 1 - Floor 2', '2021-03-03 02:03:04.0', '2021-03-03 02:03:04.0'),\n" + 
        "      (8, '4b36afc8-5205-49c1-af16-4dc6f96db989', 1, 8, 4, 'Building 1 - Floor 1 - VAV 1', 'Building 1 - Floor 1 - VAV 1', '2021-03-03 02:03:04.0', '2021-03-03 02:03:04.0'),\n" + 
        "      (9, '4b36afc8-5205-49c1-af16-4dc6f96db98a', 1, 8, 4, 'Building 1 - Floor 1 - VAV 2', 'Building 1 - Floor 1 - VAV 2', '2021-03-03 02:03:04.0', '2021-03-03 02:03:04.0'),\n" + 
        "      (10, '4b36afc8-5205-49c1-af16-4dc6f96db98b', 1, 8, 5, 'Building 1 - Floor 2 - VAV 1', 'Building 1 - Floor 2 - VAV 1', '2021-03-03 02:03:04.0', '2021-03-03 02:03:04.0'),\n" + 
        "      (11, '4b36afc8-5205-49c1-af16-4dc6f96db98c', 1, 8, 5, 'Building 1 - Floor 2 - VAV 2', 'Building 1 - Floor 2 - VAV 2', '2021-03-03 02:03:04.0', '2021-03-03 02:03:04.0') RETURNING id;";
    // @formatter:on

    assertThat(actual).isEqualTo(expected);

  }

  @Test
  void test_execute2() {
    int tableNamePrefix = 1234;
    String tableName = "test1_tbl";

    Set<IgnoredEdge> ignored = ImmutableSet.of(
        new IgnoredEdge("test1_tbl", "test3_tbl",
            new ForeignKey(ImmutableList.of(new ForeignKeyField("id", "test1_id")))),
        new IgnoredEdge("test1_tbl", "test4_tbl",
            new ForeignKey(ImmutableList.of(new ForeignKeyField("id", "test1_id")))));

    Graph subgraph = schemaGraph.getSubgraphReachableFrom(tableName, ignored);
    Node node = subgraph.getNode(tableName).get();
    KeyMaps keyMaps = new KeyMaps();

    assertStateBefore2();

    CloneOperation operation = CloneOperation.forNode(node)
        .withDataSource(dataSource)
        .withTableNamePrefix(tableNamePrefix)
        .withKeyMaps(keyMaps)
        .withFieldLevelMutators(FieldLevelMutators.builder()
            .withFieldLevelMutator("name",
                (prefix, copyNumber, value) -> value + " " + prefix + "_" + copyNumber)
            .build())
        .withDebug(false)
        .build();

    operation.execute();

    assertStateAfter2();
  }

  private void assertStateBefore() {

    Records records = select("node_tbl2");

    assertThat(records.size()).isEqualTo(15);

  }

  private void assertStateBefore2() {

    Records records = select("test1_tbl");

    assertThat(records.size()).isEqualTo(2);
  }

  private void assertStateAfter() {
    Records records;
    Map<String, String> record;

    records = select("node_tbl2");

    assertThat(records.size()).isEqualTo(22);

    record = assertPresent(records.findRecord(Key.of("id", 16)));
    assertRecord(record)
        .hasFieldValue("id", 16)
        .hasNonNullFieldValue("uuid")
        .hasFieldValue("customer_id", 1)
        .hasFieldValue("node_type_id", 3)
        .hasFieldValue("parent_id", 1)
        .hasFieldValue("name", "Building 1 1234_1")
        .hasFieldValue("display_name", "Building 1 1234_1")
        .hasFieldValue("created_at", "2021-03-03 02:03:04.0")
        .hasFieldValue("updated_at", "2021-03-03 02:03:04.0");

    record = assertPresent(records.findRecord(Key.of("id", 17)));
    assertRecord(record)
        .hasFieldValue("id", 17)
        .hasNonNullFieldValue("uuid")
        .hasFieldValue("customer_id", 1)
        .hasFieldValue("node_type_id", 5)
        .hasFieldValue("parent_id", 16)
        .hasFieldValue("name", "Building 1 - Floor 1")
        .hasFieldValue("display_name", "Building 1 - Floor 1")
        .hasFieldValue("created_at", "2021-03-03 02:03:04.0")
        .hasFieldValue("updated_at", "2021-03-03 02:03:04.0");

    record = assertPresent(records.findRecord(Key.of("id", 18)));
    assertRecord(record)
        .hasFieldValue("id", 18)
        .hasNonNullFieldValue("uuid")
        .hasFieldValue("customer_id", 1)
        .hasFieldValue("node_type_id", 5)
        .hasFieldValue("parent_id", 16)
        .hasFieldValue("name", "Building 1 - Floor 2")
        .hasFieldValue("display_name", "Building 1 - Floor 2")
        .hasFieldValue("created_at", "2021-03-03 02:03:04.0")
        .hasFieldValue("updated_at", "2021-03-03 02:03:04.0");

    record = assertPresent(records.findRecord(Key.of("id", 19)));
    assertRecord(record)
        .hasFieldValue("id", 19)
        .hasNonNullFieldValue("uuid")
        .hasFieldValue("customer_id", 1)
        .hasFieldValue("node_type_id", 8)
        .hasFieldValue("parent_id", 17)
        .hasFieldValue("name", "Building 1 - Floor 1 - VAV 1")
        .hasFieldValue("display_name", "Building 1 - Floor 1 - VAV 1")
        .hasFieldValue("created_at", "2021-03-03 02:03:04.0")
        .hasFieldValue("updated_at", "2021-03-03 02:03:04.0");

    record = assertPresent(records.findRecord(Key.of("id", 20)));
    assertRecord(record)
        .hasFieldValue("id", 20)
        .hasNonNullFieldValue("uuid")
        .hasFieldValue("customer_id", 1)
        .hasFieldValue("node_type_id", 8)
        .hasFieldValue("parent_id", 17)
        .hasFieldValue("name", "Building 1 - Floor 1 - VAV 2")
        .hasFieldValue("display_name", "Building 1 - Floor 1 - VAV 2")
        .hasFieldValue("created_at", "2021-03-03 02:03:04.0")
        .hasFieldValue("updated_at", "2021-03-03 02:03:04.0");

    record = assertPresent(records.findRecord(Key.of("id", 21)));
    assertRecord(record)
        .hasFieldValue("id", 21)
        .hasNonNullFieldValue("uuid")
        .hasFieldValue("customer_id", 1)
        .hasFieldValue("node_type_id", 8)
        .hasFieldValue("parent_id", 18)
        .hasFieldValue("name", "Building 1 - Floor 2 - VAV 1")
        .hasFieldValue("display_name", "Building 1 - Floor 2 - VAV 1")
        .hasFieldValue("created_at", "2021-03-03 02:03:04.0")
        .hasFieldValue("updated_at", "2021-03-03 02:03:04.0");

    record = assertPresent(records.findRecord(Key.of("id", 22)));
    assertRecord(record)
        .hasFieldValue("id", 22)
        .hasNonNullFieldValue("uuid")
        .hasFieldValue("customer_id", 1)
        .hasFieldValue("node_type_id", 8)
        .hasFieldValue("parent_id", 18)
        .hasFieldValue("name", "Building 1 - Floor 2 - VAV 2")
        .hasFieldValue("display_name", "Building 1 - Floor 2 - VAV 2")
        .hasFieldValue("created_at", "2021-03-03 02:03:04.0")
        .hasFieldValue("updated_at", "2021-03-03 02:03:04.0");
  }

  private void assertStateAfter2() {
    Records records;
    Map<String, String> record;

    records = select("test1_tbl");

    assertThat(records.size()).isEqualTo(3);

    record = assertPresent(records.findRecord(Key.of("id", 3)));

    assertRecord(record)
        .hasFieldValue("id", 3)
        .hasFieldValue("name", "One 1234_1");
  }

}
