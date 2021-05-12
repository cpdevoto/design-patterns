package com.resolute.dataset.cloner.app.testutils;

import static com.resolute.dataset.cloner.app.testutils.RecordAssertion.assertRecord;
import static java.util.Objects.requireNonNull;

import java.util.Map;
import java.util.Optional;

import com.resolute.dataset.cloner.app.integration.AbstractDatabaseTest;
import com.resolute.dataset.cloner.utils.Key;

public class BuildingCloneAssertions {

  private final AbstractDatabaseTest test;

  public BuildingCloneAssertions(AbstractDatabaseTest test) {
    this.test = requireNonNull(test, "test cannot be null");
  }

  public void assertStateAfterClone() {
    assertStateAfterClone(Optional.empty());
  }

  public void assertStateAfterClone(int prefix) {
    assertStateAfterClone(Optional.of(prefix));
  }

  private void assertStateAfterClone(Optional<Integer> prefix) {
    String table;
    Records records;
    Map<String, String> record;
    // assert contents of node_tbl
    table = "node_tbl";
    records = test.select(table);
    test.assertAdditionalRecs(table, records, 4);

    record = test.assertPresent(records.findRecord(Key.of("id", 16)));
    assertRecord(record)
        .hasFieldValue("id", 16)
        .hasFieldValue("node_type_id", 3)
        .hasMutatedFieldValue("name", prefix, p -> "Building1_" + p + "_1")
        .hasMutatedFieldValue("display_name", prefix, p -> "Building1_" + p + "_1");

    record = test.assertPresent(records.findRecord(Key.of("id", 17)));
    assertRecord(record)
        .hasFieldValue("id", 17)
        .hasFieldValue("node_type_id", 5)
        .hasFieldValue("name", "Floor1")
        .hasFieldValue("display_name", "Floor1");

    record = test.assertPresent(records.findRecord(Key.of("id", 18)));
    assertRecord(record)
        .hasFieldValue("id", 18)
        .hasFieldValue("node_type_id", 8)
        .hasFieldValue("name", "HVAC1")
        .hasFieldValue("display_name", "HVAC1");

    record = test.assertPresent(records.findRecord(Key.of("id", 19)));
    assertRecord(record)
        .hasFieldValue("id", 19)
        .hasFieldValue("node_type_id", 9)
        .hasFieldValue("name", "ZoneTempDev1")
        .hasFieldValue("display_name", "ZoneTempDev1");

    // assert contents of billing_location_tbl
    table = "billing_location_tbl";
    records = test.select(table);
    test.assertAdditionalRecs(table, records, 1);

    record = test.assertPresent(records.findRecord(Key.of("id", 16)));
    assertRecord(record)
        .hasFieldValue("id", 16);

    // assert contents of building_tbl
    table = "building_tbl";
    records = test.select(table);
    test.assertAdditionalRecs(table, records, 1);

    record = test.assertPresent(records.findRecord(Key.of("id", 16)));
    assertRecord(record)
        .hasFieldValue("id", 16)
        .hasFieldValue("ruby_timezone_id", 43)
        .hasFieldValue("status", "PENDING_ACTIVATION")
        .hasFieldValue("payment_type", "ONLINE");

    // assert contents of floor_tbl
    table = "floor_tbl";
    records = test.select(table);
    test.assertAdditionalRecs(table, records, 1);

    record = test.assertPresent(records.findRecord(Key.of("id", 17)));
    assertRecord(record)
        .hasFieldValue("id", 17);

    // assert contents of equipment_tbl
    table = "equipment_tbl";
    records = test.select(table);
    test.assertAdditionalRecs(table, records, 1);

    record = test.assertPresent(records.findRecord(Key.of("id", 18)));
    assertRecord(record)
        .hasFieldValue("id", 18);

    // assert contents of raw_point_tbl
    table = "raw_point_tbl";
    records = test.select(table);
    test.assertAdditionalRecs(table, records, 1);

    record = test.assertPresent(records.findRecord(Key.of("id", 11)));
    assertRecord(record)
        .hasFieldValue("id", 11)
        .hasMutatedFieldValue("metric_id", prefix,
            p -> "__.Bldg" + p + "1.__Drivers/NiagaraNetwork/Building1/Floor1/HVAC1/ZoneTempDev");

    // assert contents of point_tbl
    table = "point_tbl";
    records = test.select(table);
    test.assertAdditionalRecs(table, records, 1);

    record = test.assertPresent(records.findRecord(Key.of("id", 19)));
    assertRecord(record)
        .hasFieldValue("id", 19)
        .hasMutatedFieldValue("metric_id", prefix,
            p -> "__.Bldg" + p + "1.__Drivers/NiagaraNetwork/Building1/Floor1/HVAC1/ZoneTempDev");

    // assert contents of mappable_point_tbl
    table = "mappable_point_tbl";
    records = test.select(table);
    test.assertAdditionalRecs(table, records, 1);

    record = test.assertPresent(records.findRecord(Key.of("id", 19)));
    assertRecord(record)
        .hasFieldValue("id", 19)
        .hasFieldValue("raw_point_id", 11);

    // assert contents of w_kpi_tbl (there should be no change!)
    table = "w_kpi_tbl";
    records = test.select(table);
    test.assertAdditionalRecs(table, records, 0);

    // assert contents of w_gauge_point_tbl (there should be no change!)
    table = "w_gauge_point_tbl";
    records = test.select(table);
    test.assertAdditionalRecs(table, records, 0);

    // assert contents of email_notification_tbl
    table = "email_notification_tbl";
    records = test.select(table);
    test.assertAdditionalRecs(table, records, 1);

    record = test.assertPresent(records.findRecord(Key.of("id", 5)));
    assertRecord(record)
        .hasFieldValue("id", 5);

    // assert contents of building_email_notification_tbl
    table = "building_email_notification_tbl";
    records = test.select(table);
    test.assertAdditionalRecs(table, records, 1);

    record = test.assertPresent(records.findRecord(Key.of("id", 5)));
    assertRecord(record)
        .hasFieldValue("id", 5)
        .hasFieldValue("building_id", 16);

    // assert contents of node_closure_tbl
    table = "node_closure_tbl";
    records = test.select(table);
    test.assertAdditionalRecs(table, records, 14);

    record = test.assertPresent(records.findRecord(Key.of("parent_id", 1, "child_id", 16)));
    assertRecord(record)
        .hasFieldValue("parent_id", 1)
        .hasFieldValue("child_id", 16)
        .hasFieldValue("depth", 1);

    record = test.assertPresent(records.findRecord(Key.of("parent_id", 1, "child_id", 17)));
    assertRecord(record)
        .hasFieldValue("parent_id", 1)
        .hasFieldValue("child_id", 17)
        .hasFieldValue("depth", 2);

    record = test.assertPresent(records.findRecord(Key.of("parent_id", 1, "child_id", 18)));
    assertRecord(record)
        .hasFieldValue("parent_id", 1)
        .hasFieldValue("child_id", 18)
        .hasFieldValue("depth", 3);

    record = test.assertPresent(records.findRecord(Key.of("parent_id", 1, "child_id", 19)));
    assertRecord(record)
        .hasFieldValue("parent_id", 1)
        .hasFieldValue("child_id", 19)
        .hasFieldValue("depth", 4);

    assertRecord(test.assertPresent(records.findRecord(Key.of("parent_id", 16, "child_id", 16))))
        .hasFieldValue("depth", 0);
    assertRecord(test.assertPresent(records.findRecord(Key.of("parent_id", 16, "child_id", 17))))
        .hasFieldValue("depth", 1);
    assertRecord(test.assertPresent(records.findRecord(Key.of("parent_id", 16, "child_id", 18))))
        .hasFieldValue("depth", 2);
    assertRecord(test.assertPresent(records.findRecord(Key.of("parent_id", 16, "child_id", 19))))
        .hasFieldValue("depth", 3);

    assertRecord(test.assertPresent(records.findRecord(Key.of("parent_id", 17, "child_id", 17))))
        .hasFieldValue("depth", 0);
    assertRecord(test.assertPresent(records.findRecord(Key.of("parent_id", 17, "child_id", 18))))
        .hasFieldValue("depth", 1);
    assertRecord(test.assertPresent(records.findRecord(Key.of("parent_id", 17, "child_id", 19))))
        .hasFieldValue("depth", 2);

    assertRecord(test.assertPresent(records.findRecord(Key.of("parent_id", 18, "child_id", 18))))
        .hasFieldValue("depth", 0);
    assertRecord(test.assertPresent(records.findRecord(Key.of("parent_id", 18, "child_id", 19))))
        .hasFieldValue("depth", 1);

    assertRecord(test.assertPresent(records.findRecord(Key.of("parent_id", 19, "child_id", 19))))
        .hasFieldValue("depth", 0);
  }


}
