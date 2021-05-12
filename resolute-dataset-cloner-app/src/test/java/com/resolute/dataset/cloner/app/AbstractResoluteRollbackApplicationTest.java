package com.resolute.dataset.cloner.app;

import static com.resolute.dataset.cloner.app.testutils.RecordAssertion.assertRecord;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.resolute.dataset.cloner.Environment;
import com.resolute.dataset.cloner.app.integration.AbstractDatabaseTest;
import com.resolute.dataset.cloner.app.integration.IntegrationTestSuite;
import com.resolute.dataset.cloner.app.testutils.Records;
import com.resolute.dataset.cloner.log.Logger;
import com.resolute.dataset.cloner.utils.Key;
import com.resolute.jdbc.simple.DaoUtils;
import com.resolute.utils.simple.ElapsedTimeUtils;

// TODO: Move the BuildingCloner into a separate resolute-dataset-cloner project.
public abstract class AbstractResoluteRollbackApplicationTest extends AbstractDatabaseTest {

  @TempDir
  File tempDir;

  private File confFile;

  @BeforeEach
  void setup() throws IOException, SQLException {
    this.confFile = createConfFile();
    DaoUtils.executeSqlScript(dataSource, IntegrationTestSuite.class,
        "base-resolute-data.sql");
  }

  @AfterEach
  void cleanupTestTables() throws IOException, SQLException {
    DaoUtils.executeSqlScript(dataSource, IntegrationTestSuite.class,
        "base-resolute-teardown.sql");
  }

  @Test
  void test_run() throws Exception {
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

    try {
      cloner.execute();

      assertStateBeforeRollback(prefix);

      // SETUP
      String[] args = new String[] {confFile.getAbsolutePath()};

      // EXECUTE
      ResoluteRollbackApplication.main(args);

      assertStateAfterRollback();



    } finally {
      cloner.rollback();
    }
  }

  private void assertStateBeforeRollback(int prefix) {
    String table;
    Records records;
    Map<String, String> record;
    // assert contents of node_tbl
    table = "node_tbl";
    records = select(table);
    assertAdditionalRecs(table, records, 4);

    record = assertPresent(records.findRecord(Key.of("id", 16)));
    assertRecord(record)
        .hasFieldValue("id", 16)
        .hasFieldValue("node_type_id", 3)
        .hasFieldValue("name", "Building1_" + prefix + "_1")
        .hasFieldValue("display_name", "Building1_" + prefix + "_1");

    record = assertPresent(records.findRecord(Key.of("id", 17)));
    assertRecord(record)
        .hasFieldValue("id", 17)
        .hasFieldValue("node_type_id", 5)
        .hasFieldValue("name", "Floor1")
        .hasFieldValue("display_name", "Floor1");

    record = assertPresent(records.findRecord(Key.of("id", 18)));
    assertRecord(record)
        .hasFieldValue("id", 18)
        .hasFieldValue("node_type_id", 8)
        .hasFieldValue("name", "HVAC1")
        .hasFieldValue("display_name", "HVAC1");

    record = assertPresent(records.findRecord(Key.of("id", 19)));
    assertRecord(record)
        .hasFieldValue("id", 19)
        .hasFieldValue("node_type_id", 9)
        .hasFieldValue("name", "ZoneTempDev1")
        .hasFieldValue("display_name", "ZoneTempDev1");

    // assert contents of billing_location_tbl
    table = "billing_location_tbl";
    records = select(table);
    assertAdditionalRecs(table, records, 1);

    record = assertPresent(records.findRecord(Key.of("id", 16)));
    assertRecord(record)
        .hasFieldValue("id", 16);

    // assert contents of building_tbl
    table = "building_tbl";
    records = select(table);
    assertAdditionalRecs(table, records, 1);

    record = assertPresent(records.findRecord(Key.of("id", 16)));
    assertRecord(record)
        .hasFieldValue("id", 16)
        .hasFieldValue("ruby_timezone_id", 43)
        .hasFieldValue("status", "PENDING_ACTIVATION")
        .hasFieldValue("payment_type", "ONLINE");

    // assert contents of floor_tbl
    table = "floor_tbl";
    records = select(table);
    assertAdditionalRecs(table, records, 1);

    record = assertPresent(records.findRecord(Key.of("id", 17)));
    assertRecord(record)
        .hasFieldValue("id", 17);

    // assert contents of equipment_tbl
    table = "equipment_tbl";
    records = select(table);
    assertAdditionalRecs(table, records, 1);

    record = assertPresent(records.findRecord(Key.of("id", 18)));
    assertRecord(record)
        .hasFieldValue("id", 18);

    // assert contents of raw_point_tbl
    table = "raw_point_tbl";
    records = select(table);
    assertAdditionalRecs(table, records, 1);

    record = assertPresent(records.findRecord(Key.of("id", 11)));
    assertRecord(record)
        .hasFieldValue("id", 11)
        .hasFieldValue("metric_id",
            "__.Bldg" + prefix + "1.__Drivers/NiagaraNetwork/Building1/Floor1/HVAC1/ZoneTempDev");

    // assert contents of point_tbl
    table = "point_tbl";
    records = select(table);
    assertAdditionalRecs(table, records, 1);

    record = assertPresent(records.findRecord(Key.of("id", 19)));
    assertRecord(record)
        .hasFieldValue("id", 19);

    // assert contents of mappable_point_tbl
    table = "mappable_point_tbl";
    records = select(table);
    assertAdditionalRecs(table, records, 1);

    record = assertPresent(records.findRecord(Key.of("id", 19)));
    assertRecord(record)
        .hasFieldValue("id", 19)
        .hasFieldValue("raw_point_id", 11);

    // assert contents of w_kpi_tbl (there should be no change!)
    table = "w_kpi_tbl";
    records = select(table);
    assertAdditionalRecs(table, records, 0);

    // assert contents of w_gauge_point_tbl (there should be no change!)
    table = "w_gauge_point_tbl";
    records = select(table);
    assertAdditionalRecs(table, records, 0);

    // assert contents of email_notification_tbl
    table = "email_notification_tbl";
    records = select(table);
    assertAdditionalRecs(table, records, 1);

    record = assertPresent(records.findRecord(Key.of("id", 5)));
    assertRecord(record)
        .hasFieldValue("id", 5);

    // assert contents of building_email_notification_tbl
    table = "building_email_notification_tbl";
    records = select(table);
    assertAdditionalRecs(table, records, 1);

    record = assertPresent(records.findRecord(Key.of("id", 5)));
    assertRecord(record)
        .hasFieldValue("id", 5)
        .hasFieldValue("building_id", 16);

    // assert contents of node_closure_tbl
    table = "node_closure_tbl";
    records = select(table);
    assertAdditionalRecs(table, records, 14);

    record = assertPresent(records.findRecord(Key.of("parent_id", 1, "child_id", 16)));
    assertRecord(record)
        .hasFieldValue("parent_id", 1)
        .hasFieldValue("child_id", 16)
        .hasFieldValue("depth", 1);

    record = assertPresent(records.findRecord(Key.of("parent_id", 1, "child_id", 17)));
    assertRecord(record)
        .hasFieldValue("parent_id", 1)
        .hasFieldValue("child_id", 17)
        .hasFieldValue("depth", 2);

    record = assertPresent(records.findRecord(Key.of("parent_id", 1, "child_id", 18)));
    assertRecord(record)
        .hasFieldValue("parent_id", 1)
        .hasFieldValue("child_id", 18)
        .hasFieldValue("depth", 3);

    record = assertPresent(records.findRecord(Key.of("parent_id", 1, "child_id", 19)));
    assertRecord(record)
        .hasFieldValue("parent_id", 1)
        .hasFieldValue("child_id", 19)
        .hasFieldValue("depth", 4);

    assertRecord(assertPresent(records.findRecord(Key.of("parent_id", 16, "child_id", 16))))
        .hasFieldValue("depth", 0);
    assertRecord(assertPresent(records.findRecord(Key.of("parent_id", 16, "child_id", 17))))
        .hasFieldValue("depth", 1);
    assertRecord(assertPresent(records.findRecord(Key.of("parent_id", 16, "child_id", 18))))
        .hasFieldValue("depth", 2);
    assertRecord(assertPresent(records.findRecord(Key.of("parent_id", 16, "child_id", 19))))
        .hasFieldValue("depth", 3);

    assertRecord(assertPresent(records.findRecord(Key.of("parent_id", 17, "child_id", 17))))
        .hasFieldValue("depth", 0);
    assertRecord(assertPresent(records.findRecord(Key.of("parent_id", 17, "child_id", 18))))
        .hasFieldValue("depth", 1);
    assertRecord(assertPresent(records.findRecord(Key.of("parent_id", 17, "child_id", 19))))
        .hasFieldValue("depth", 2);

    assertRecord(assertPresent(records.findRecord(Key.of("parent_id", 18, "child_id", 18))))
        .hasFieldValue("depth", 0);
    assertRecord(assertPresent(records.findRecord(Key.of("parent_id", 18, "child_id", 19))))
        .hasFieldValue("depth", 1);

    assertRecord(assertPresent(records.findRecord(Key.of("parent_id", 19, "child_id", 19))))
        .hasFieldValue("depth", 0);
  }

  private void assertStateAfterRollback() {
    String table;
    Records records;

    // assert contents of node_tbl
    table = "node_tbl";
    records = select(table);
    assertAdditionalRecs(table, records, 0);


    // assert contents of billing_location_tbl
    table = "billing_location_tbl";
    records = select(table);
    assertAdditionalRecs(table, records, 0);

    // assert contents of building_tbl
    table = "building_tbl";
    records = select(table);
    assertAdditionalRecs(table, records, 0);

    // assert contents of floor_tbl
    table = "floor_tbl";
    records = select(table);
    assertAdditionalRecs(table, records, 0);

    // assert contents of equipment_tbl
    table = "equipment_tbl";
    records = select(table);
    assertAdditionalRecs(table, records, 0);

    // assert contents of raw_point_tbl
    table = "raw_point_tbl";
    records = select(table);
    assertAdditionalRecs(table, records, 0);

    // assert contents of point_tbl
    table = "point_tbl";
    records = select(table);
    assertAdditionalRecs(table, records, 0);

    // assert contents of mappable_point_tbl
    table = "mappable_point_tbl";
    records = select(table);
    assertAdditionalRecs(table, records, 0);

    // assert contents of w_kpi_tbl
    table = "w_kpi_tbl";
    records = select(table);
    assertAdditionalRecs(table, records, 0);

    // assert contents of w_gauge_point_tbl
    table = "w_gauge_point_tbl";
    records = select(table);
    assertAdditionalRecs(table, records, 0);

    // assert contents of email_notification_tbl
    table = "email_notification_tbl";
    records = select(table);
    assertAdditionalRecs(table, records, 0);

    // assert contents of building_email_notification_tbl
    table = "building_email_notification_tbl";
    records = select(table);
    assertAdditionalRecs(table, records, 0);

    // assert contents of node_closure_tbl
    table = "node_closure_tbl";
    records = select(table);
    assertAdditionalRecs(table, records, 0);
  }

  private File createConfFile() throws IOException {
    File confFile = new File(tempDir, "dataset-cloner.conf");
    File logFile = new File(tempDir, "dataset-cloner.log");
    int databasePort = IntegrationTestSuite.getDatabasePort();

    String contents =
        "host=localhost\n" +
            "port=" + databasePort + "\n" +
            "database=resolute_cloud_dev\n" +
            "user=postgres\n" +
            "password=\n" +
            "logFile=" + logFile.getAbsolutePath() + "\n" +
            "numCopies=1\n" +
            "debug=false\n" +
            "entityType=BUILDING\n" +
            "entityId=2";

    try (PrintWriter out = new PrintWriter(new FileWriter(confFile))) {
      out.print(contents);
    }
    return confFile;
  }

}
