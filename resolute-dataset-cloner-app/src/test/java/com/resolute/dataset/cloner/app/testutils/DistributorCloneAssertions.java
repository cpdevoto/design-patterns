package com.resolute.dataset.cloner.app.testutils;

import static com.resolute.dataset.cloner.app.testutils.RecordAssertion.assertRecord;
import static java.util.Objects.requireNonNull;

import java.util.Map;
import java.util.Optional;

import com.resolute.dataset.cloner.app.integration.AbstractDatabaseTest;
import com.resolute.dataset.cloner.utils.Key;

public class DistributorCloneAssertions {

  private final AbstractDatabaseTest test;
  private int acTags;

  public DistributorCloneAssertions(AbstractDatabaseTest test, int acTags) {
    this.test = requireNonNull(test, "test cannot be null");
    this.acTags = acTags;
  }

  public void assertStateAfterClone() {
    assertStateAfterClone(Optional.empty());
  }

  public void assertStateAfterClone(int prefix) {
    assertStateAfterClone(Optional.of(prefix));
  }

  public void assertStateAfterClone(Optional<Integer> prefix) {
    String table;
    Records records;
    Map<String, String> record;

    // assert contents of distributor_tbl
    table = "distributor_tbl";
    records = test.select(table);
    test.assertAdditionalRecs(table, records, 1);

    record = test.assertPresent(records.findRecord(Key.of("id", 3)));
    assertRecord(record)
        .hasFieldValue("id", 3)
        .hasFieldValue("parent_id", 1)
        .hasMutatedFieldValue("name", prefix, p -> "Online Distributor_" + p + "_1")
        .hasFieldValue("referral_agent_id", 1);

    // assert contents of customer_tbl
    table = "customer_tbl";
    records = test.select(table);
    test.assertAdditionalRecs(table, records, 1);

    record = test.assertPresent(records.findRecord(Key.of("id", 2)));
    assertRecord(record)
        .hasFieldValue("id", 2)
        .hasFieldValue("distributor_id", 3)
        .hasFieldValue("name", "Studebaker");

    // assert contents of component_tbl
    table = "component_tbl";
    records = test.select(table);
    test.assertAdditionalRecs(table, records, 1);

    record = test.assertPresent(records.findRecord(Key.of("id", 2)));
    assertRecord(record)
        .hasFieldValue("id", 2)
        .hasFieldValue("customer_id", 2)
        .hasFieldValue("component_type_id", 1);

    // assert contents of user_tbl
    table = "user_tbl";
    records = test.select(table);
    test.assertAdditionalRecs(table, records, 2);

    record = test.assertPresent(records.findRecord(Key.of("id", 3)));
    assertRecord(record)
        .hasFieldValue("id", 3)
        .hasMutatedFieldValue("email", prefix, p -> "kdmiller@mclaren.com_" + p + "_1");

    record = test.assertPresent(records.findRecord(Key.of("id", 4)));
    assertRecord(record)
        .hasFieldValue("id", 4)
        .hasMutatedFieldValue("email", prefix, p -> "cdevoto@maddogtehcnology.com_" + p + "_1");

    // assert contents of customer_user_tbl
    table = "customer_user_tbl";
    records = test.select(table);
    test.assertAdditionalRecs(table, records, 1);

    record = test.assertPresent(records.findRecord(Key.of("id", 3)));
    assertRecord(record)
        .hasFieldValue("id", 3)
        .hasFieldValue("customer_id", 2);

    // assert contents of distributor_user_tbl
    table = "distributor_user_tbl";
    records = test.select(table);
    test.assertAdditionalRecs(table, records, 1);

    record = test.assertPresent(records.findRecord(Key.of("id", 4)));
    assertRecord(record)
        .hasFieldValue("id", 4)
        .hasFieldValue("distributor_id", 3);

    // assert contents of user_setting_tbl
    table = "user_setting_tbl";
    records = test.select(table);
    test.assertAdditionalRecs(table, records, 2);

    record = test.assertPresent(records.findRecord(Key.of("user_id", 3)));
    assertRecord(record)
        .hasFieldValue("user_id", 3)
        .hasFieldValue("ruby_timezone_id", 43);

    record = test.assertPresent(records.findRecord(Key.of("user_id", 4)));
    assertRecord(record)
        .hasFieldValue("user_id", 4)
        .hasFieldValue("ruby_timezone_id", 43);

    // assert contents of user_role_tbl
    table = "user_role_tbl";
    records = test.select(table);
    test.assertAdditionalRecs(table, records, 3);

    test.assertPresent(records.findRecord(Key.of("user_id", 3, "role_id", 1)));
    test.assertPresent(records.findRecord(Key.of("user_id", 4, "role_id", 1)));
    test.assertPresent(records.findRecord(Key.of("user_id", 4, "role_id", 5)));

    // assert contents of email_notification_tbl
    table = "email_notification_tbl";
    records = test.select(table);
    test.assertAdditionalRecs(table, records, 4);

    record = test.assertPresent(records.findRecord(Key.of("id", 5)));
    assertRecord(record)
        .hasFieldValue("id", 5)
        .hasFieldValue("level", "DISTRIBUTOR");

    record = test.assertPresent(records.findRecord(Key.of("id", 6)));
    assertRecord(record)
        .hasFieldValue("id", 6)
        .hasFieldValue("level", "CUSTOMER");

    record = test.assertPresent(records.findRecord(Key.of("id", 7)));
    assertRecord(record)
        .hasFieldValue("id", 7)
        .hasFieldValue("level", "BUILDING");

    record = test.assertPresent(records.findRecord(Key.of("id", 8)));
    assertRecord(record)
        .hasFieldValue("id", 8)
        .hasFieldValue("level", "BUILDING");

    // assert contents of distributor_email_notification_tbl
    table = "distributor_email_notification_tbl";
    records = test.select(table);
    test.assertAdditionalRecs(table, records, 1);

    record = test.assertPresent(records.findRecord(Key.of("id", 5)));
    assertRecord(record)
        .hasFieldValue("id", 5)
        .hasFieldValue("distributor_id", 3);

    // assert contents of customer_email_notification_tbl
    table = "customer_email_notification_tbl";
    records = test.select(table);
    test.assertAdditionalRecs(table, records, 1);

    record = test.assertPresent(records.findRecord(Key.of("id", 6)));
    assertRecord(record)
        .hasFieldValue("id", 6)
        .hasFieldValue("customer_id", 2);

    // assert contents of building_email_notification_tbl
    table = "building_email_notification_tbl";
    records = test.select(table);
    test.assertAdditionalRecs(table, records, 2);

    record = test.assertPresent(records.findRecord(Key.of("id", 7)));
    assertRecord(record)
        .hasFieldValue("id", 7)
        .hasFieldValue("building_id", 17);

    record = test.assertPresent(records.findRecord(Key.of("id", 8)));
    assertRecord(record)
        .hasFieldValue("id", 8)
        .hasFieldValue("building_id", 18);


    // assert contents of node_tbl
    table = "node_tbl";
    records = test.select(table);
    test.assertAdditionalRecs(table, records, 10);

    record = test.assertPresent(records.findRecord(Key.of("id", 16)));
    assertRecord(record)
        .hasFieldValue("id", 16)
        .hasFieldValue("node_type_id", 1)
        .hasMutatedFieldValue("name", prefix, p -> "Studebaker_" + p + "_1")
        .hasMutatedFieldValue("display_name", prefix, p -> "Studebaker_" + p + "_1");

    record = test.assertPresent(records.findRecord(Key.of("id", 17)));
    assertRecord(record)
        .hasFieldValue("id", 17)
        .hasFieldValue("node_type_id", 3)
        .hasFieldValue("name", "Building1")
        .hasFieldValue("display_name", "Building1");

    record = test.assertPresent(records.findRecord(Key.of("id", 18)));
    assertRecord(record)
        .hasFieldValue("id", 18)
        .hasFieldValue("node_type_id", 3)
        .hasFieldValue("name", "Building2")
        .hasFieldValue("display_name", "Building2");

    record = test.assertPresent(records.findRecord(Key.of("id", 19)));
    assertRecord(record)
        .hasFieldValue("id", 19)
        .hasFieldValue("node_type_id", 3)
        .hasFieldValue("name", "Building3")
        .hasFieldValue("display_name", "Building3");

    record = test.assertPresent(records.findRecord(Key.of("id", 20)));
    assertRecord(record)
        .hasFieldValue("id", 20)
        .hasFieldValue("node_type_id", 5)
        .hasFieldValue("name", "Floor1")
        .hasFieldValue("display_name", "Floor1");

    record = test.assertPresent(records.findRecord(Key.of("id", 21)));
    assertRecord(record)
        .hasFieldValue("id", 21)
        .hasFieldValue("node_type_id", 5)
        .hasFieldValue("name", "Floor2")
        .hasFieldValue("display_name", "Floor2");


    record = test.assertPresent(records.findRecord(Key.of("id", 22)));
    assertRecord(record)
        .hasFieldValue("id", 22)
        .hasFieldValue("node_type_id", 8)
        .hasFieldValue("name", "HVAC1")
        .hasFieldValue("display_name", "HVAC1");

    record = test.assertPresent(records.findRecord(Key.of("id", 23)));
    assertRecord(record)
        .hasFieldValue("id", 23)
        .hasFieldValue("node_type_id", 8)
        .hasFieldValue("name", "HVAC2")
        .hasFieldValue("display_name", "HVAC2");

    record = test.assertPresent(records.findRecord(Key.of("id", 24)));
    assertRecord(record)
        .hasFieldValue("id", 24)
        .hasFieldValue("node_type_id", 9)
        .hasFieldValue("name", "ZoneTempDev1")
        .hasFieldValue("display_name", "ZoneTempDev1");

    record = test.assertPresent(records.findRecord(Key.of("id", 25)));
    assertRecord(record)
        .hasFieldValue("id", 25)
        .hasFieldValue("node_type_id", 9)
        .hasFieldValue("name", "ZoneTempDev2")
        .hasFieldValue("display_name", "ZoneTempDev2");

    // assert contents of billing_location_tbl
    table = "billing_location_tbl";
    records = test.select(table);
    test.assertAdditionalRecs(table, records, 3);

    test.assertPresent(records.findRecord(Key.of("id", 17)));
    test.assertPresent(records.findRecord(Key.of("id", 18)));
    test.assertPresent(records.findRecord(Key.of("id", 19)));

    // assert contents of building_tbl
    table = "building_tbl";
    records = test.select(table);
    test.assertAdditionalRecs(table, records, 3);

    record = test.assertPresent(records.findRecord(Key.of("id", 17)));
    assertRecord(record)
        .hasFieldValue("id", 17)
        .hasFieldValue("ruby_timezone_id", 43)
        .hasFieldValue("status", "PENDING_ACTIVATION")
        .hasFieldValue("payment_type", "ONLINE");

    record = test.assertPresent(records.findRecord(Key.of("id", 18)));
    assertRecord(record)
        .hasFieldValue("id", 18)
        .hasFieldValue("ruby_timezone_id", 43)
        .hasFieldValue("status", "PENDING_ACTIVATION")
        .hasFieldValue("payment_type", "ONLINE");

    record = test.assertPresent(records.findRecord(Key.of("id", 19)));
    assertRecord(record)
        .hasFieldValue("id", 19)
        .hasFieldValue("ruby_timezone_id", 43)
        .hasFieldValue("status", "CREATED")
        .hasFieldValue("payment_type", "ONLINE");

    // assert contents of floor_tbl
    table = "floor_tbl";
    records = test.select(table);
    test.assertAdditionalRecs(table, records, 2);

    record = test.assertPresent(records.findRecord(Key.of("id", 20)));
    assertRecord(record)
        .hasFieldValue("id", 20);

    record = test.assertPresent(records.findRecord(Key.of("id", 21)));
    assertRecord(record)
        .hasFieldValue("id", 21);


    // assert contents of equipment_tbl
    table = "equipment_tbl";
    records = test.select(table);
    test.assertAdditionalRecs(table, records, 2);

    record = test.assertPresent(records.findRecord(Key.of("id", 22)));
    assertRecord(record)
        .hasFieldValue("id", 22);

    record = test.assertPresent(records.findRecord(Key.of("id", 23)));
    assertRecord(record)
        .hasFieldValue("id", 23);


    // assert contents of raw_point_tbl
    table = "raw_point_tbl";
    records = test.select(table);
    test.assertAdditionalRecs(table, records, 2);

    record = test.assertPresent(records.findRecord(Key.of("id", 11)));
    assertRecord(record)
        .hasFieldValue("id", 11)
        .hasFieldValue("customer_id", 2)
        .hasMutatedFieldValue("metric_id", prefix,
            p -> "__.Bldg" + p + "1.__Drivers/NiagaraNetwork/Building1/Floor1/HVAC1/ZoneTempDev");

    record = test.assertPresent(records.findRecord(Key.of("id", 12)));
    assertRecord(record)
        .hasFieldValue("id", 12)
        .hasFieldValue("customer_id", 2)
        .hasMutatedFieldValue("metric_id", prefix,
            p -> "__.Bldg" + p + "1.__Drivers/NiagaraNetwork/Building2/Floor2/HVAC2/ZoneTempDev");

    // assert contents of point_tbl
    table = "point_tbl";
    records = test.select(table);
    test.assertAdditionalRecs(table, records, 2);

    record = test.assertPresent(records.findRecord(Key.of("id", 24)));
    assertRecord(record)
        .hasFieldValue("id", 24)
        .hasMutatedFieldValue("metric_id", prefix,
            p -> "__.Bldg" + p + "1.__Drivers/NiagaraNetwork/Building1/Floor1/HVAC1/ZoneTempDev");

    record = test.assertPresent(records.findRecord(Key.of("id", 25)));
    assertRecord(record)
        .hasFieldValue("id", 25)
        .hasMutatedFieldValue("metric_id", prefix,
            p -> "__.Bldg" + p + "1.__Drivers/NiagaraNetwork/Building2/Floor2/HVAC2/ZoneTempDev");

    // assert contents of mappable_point_tbl
    table = "mappable_point_tbl";
    records = test.select(table);
    test.assertAdditionalRecs(table, records, 2);

    record = test.assertPresent(records.findRecord(Key.of("id", 24)));
    assertRecord(record)
        .hasFieldValue("id", 24)
        .hasFieldValue("raw_point_id", 11);

    record = test.assertPresent(records.findRecord(Key.of("id", 25)));
    assertRecord(record)
        .hasFieldValue("id", 25)
        .hasFieldValue("raw_point_id", 12);

    // assert contents of perspective_tbl
    table = "perspective_tbl";
    records = test.select(table);
    test.assertAdditionalRecs(table, records, 1);

    record = test.assertPresent(records.findRecord(Key.of("id", 2)));
    assertRecord(record)
        .hasFieldValue("id", 2)
        .hasFieldValue("perspective_type_id", 1)
        .hasFieldValue("customer_id", 2)
        .hasFieldValue("name", "Perspective 1")
        .hasFieldValue("display_name", "My First Perspective")
        .hasFieldValue("description", "A simple perspective")
        .hasFieldValue("template", false)
        .hasFieldValue("user_visible", true);

    // assert contents of standard_perspective_tbl
    table = "standard_perspective_tbl";
    records = test.select(table);
    test.assertAdditionalRecs(table, records, 1);

    record = test.assertPresent(records.findRecord(Key.of("id", 2)));
    assertRecord(record)
        .hasFieldValue("id", 2)
        .hasFieldValue("hide_summary", false);

    // assert contents of standard_perspective_customer_tbl
    table = "standard_perspective_customer_tbl";
    records = test.select(table);
    test.assertAdditionalRecs(table, records, 1);

    record = test.assertPresent(records.findRecord(Key.of("perspective_id", 2)));
    assertRecord(record)
        .hasFieldValue("perspective_id", 2)
        .hasFieldValue("customer_id", 2)
        .hasFieldValue("ordinal", 1)
        .hasFieldValue("visible_to_all", false);

    // assert contents of standard_perspective_node_tbl
    table = "standard_perspective_customer_node_tbl";
    records = test.select(table);
    test.assertAdditionalRecs(table, records, 1);

    record = test.assertPresent(records.findRecord(Key.of("perspective_id", 2)));
    assertRecord(record)
        .hasFieldValue("perspective_id", 2)
        .hasFieldValue("node_id", 17);

    // assert contents of widget_tbl
    table = "widget_tbl";
    records = test.select(table);
    test.assertAdditionalRecs(table, records, 3);

    record = test.assertPresent(records.findRecord(Key.of("id", 4)));
    assertRecord(record)
        .hasFieldValue("id", 4)
        .hasFieldValue("customer_id", 2)
        .hasFieldValue("widget_type_id", 3)
        .hasFieldValue("template", false)
        .hasFieldValue("name", "kWh per Col");

    record = test.assertPresent(records.findRecord(Key.of("id", 5)));
    assertRecord(record)
        .hasFieldValue("id", 5)
        .hasFieldValue("customer_id", 2)
        .hasFieldValue("widget_type_id", 6)
        .hasFieldValue("template", false)
        .hasFieldValue("name", "kWh per Hour");

    record = test.assertPresent(records.findRecord(Key.of("id", 6)));
    assertRecord(record)
        .hasFieldValue("id", 6)
        .hasFieldValue("customer_id", 2)
        .hasFieldValue("widget_type_id", 8)
        .hasFieldValue("template", false)
        .hasFieldValue("name", "Test Gauge");

    // assert contents of w_kpi_tbl
    table = "w_kpi_tbl";
    records = test.select(table);
    test.assertAdditionalRecs(table, records, 1);

    record = test.assertPresent(records.findRecord(Key.of("id", 4)));
    assertRecord(record)
        .hasFieldValue("id", 4)
        .hasFieldValue("history_aggregator_id", 1)
        .hasFieldValue("point_id", 24)
        .hasFieldValue("precision", 4)
        .hasFieldValue("prepend", "Test Prepend")
        .hasFieldValue("append", "Test Append")
        .hasFieldValue("low_threshold", 5)
        .hasFieldValue("high_threshold", 10);

    // assert contents of w_datablock_tbl
    table = "w_datablock_tbl";
    records = test.select(table);
    test.assertAdditionalRecs(table, records, 1);

    record = test.assertPresent(records.findRecord(Key.of("id", 5)));
    assertRecord(record)
        .hasFieldValue("id", 5)
        .hasFieldValue("point_id", 25)
        .hasFieldValue("precision", 4)
        .hasFieldValue("prepend", "Test Prepend")
        .hasFieldValue("append", "Test Append")
        .hasFieldValue("background_color", "red")
        .hasFieldValue("text_color", "blue")
        .hasFieldValue("humanize", "cardinal8deg");

    // assert contents of w_gauge_tbl
    table = "w_gauge_tbl";
    records = test.select(table);
    test.assertAdditionalRecs(table, records, 1);

    record = test.assertPresent(records.findRecord(Key.of("id", 6)));
    assertRecord(record)
        .hasFieldValue("id", 6)
        .hasFieldValue("min", 0)
        .hasFieldValue("max", 50);

    // assert contents of w_gauge_range_tbl
    table = "w_gauge_range_tbl";
    records = test.select(table);
    test.assertAdditionalRecs(table, records, 1);

    record = test.assertPresent(records.findRecord(Key.of("id", 2)));
    assertRecord(record)
        .hasFieldValue("id", 2)
        .hasFieldValue("widget_id", 6)
        .hasFieldValue("start", 0)
        .hasFieldValue("end", 50)
        .hasFieldValue("color", "green")
        .hasFieldValue("label", "Test Label")
        .hasFieldValue("short_label", "Test Short Label");

    // assert contents of w_gauge_point_tbl
    table = "w_gauge_point_tbl";
    records = test.select(table);
    test.assertAdditionalRecs(table, records, 2);

    record = test.assertPresent(records.findRecord(Key.of("id", 3)));
    assertRecord(record)
        .hasFieldValue("id", 3)
        .hasFieldValue("widget_id", 6)
        .hasFieldValue("point_id", 24)
        .hasFieldValue("widget_point_type_id", 5);

    record = test.assertPresent(records.findRecord(Key.of("id", 4)));
    assertRecord(record)
        .hasFieldValue("id", 4)
        .hasFieldValue("widget_id", 6)
        .hasFieldValue("point_id", 25)
        .hasFieldValue("widget_point_type_id", 6);

    // assert contents of perspective_widget_tbl
    table = "perspective_widget_tbl";
    records = test.select(table);
    test.assertAdditionalRecs(table, records, 2);

    record = test.assertPresent(records.findRecord(Key.of("perspective_id", 2, "widget_id", 4)));
    assertRecord(record)
        .hasFieldValue("perspective_id", 2)
        .hasFieldValue("widget_id", 4)
        .hasFieldValue("row_num", 1)
        .hasFieldValue("col_num", 1)
        .hasFieldValue("ordinal", 1);

    record = test.assertPresent(records.findRecord(Key.of("perspective_id", 2, "widget_id", 5)));
    assertRecord(record)
        .hasFieldValue("perspective_id", 2)
        .hasFieldValue("widget_id", 5)
        .hasFieldValue("row_num", 1)
        .hasFieldValue("col_num", 1)
        .hasFieldValue("ordinal", 2);

    // assert contents of client_credential_tbl
    table = "client_credential_tbl";
    records = test.select(table);
    test.assertAdditionalRecs(table, records, 2);

    record = test.assertPresent(records.findRecord(Key.of("id", 3)));
    assertRecord(record)
        .hasFieldValue("id", 3)
        .hasFieldValue("type", "DISTRIBUTOR")
        .hasMutatedFieldValue("client_id", prefix, p -> "client_id_1_" + p + "_1")
        .hasFieldValue("client_secret", "xyzpdq");

    record = test.assertPresent(records.findRecord(Key.of("id", 4)));
    assertRecord(record)
        .hasFieldValue("id", 4)
        .hasFieldValue("type", "CUSTOMER")
        .hasMutatedFieldValue("client_id", prefix, p -> "client_id_2_" + p + "_1")
        .hasFieldValue("client_secret", "xyzpdq");

    // assert contents of distributor_client_credential_tbl
    table = "distributor_client_credential_tbl";
    records = test.select(table);
    test.assertAdditionalRecs(table, records, 1);

    record = test.assertPresent(records.findRecord(Key.of("id", 3)));
    assertRecord(record)
        .hasFieldValue("id", 3)
        .hasFieldValue("distributor_id", 3);

    // assert contents of customer_client_credential_tbl
    table = "customer_client_credential_tbl";
    records = test.select(table);
    test.assertAdditionalRecs(table, records, 1);

    record = test.assertPresent(records.findRecord(Key.of("id", 4)));
    assertRecord(record)
        .hasFieldValue("id", 4)
        .hasFieldValue("customer_id", 2);

    // assert contents of export_job_tbl
    table = "export_job_tbl";
    records = test.select(table);
    test.assertAdditionalRecs(table, records, 2);

    record = test.assertPresent(records.findRecord(Key.of("id", 3)));
    assertRecord(record)
        .hasFieldValue("id", 3)
        .hasFieldValue("customer_id", 2)
        .hasFieldValue("export_job_status_id", 3)
        .hasFieldValue("export_job_type_id", 1)
        .hasFieldValue("creator_id", 3)
        .hasFieldValue("creator_email", "kdmiller@mclaren.com")
        .hasFieldValue("name", "Export Chart Data: Tue, 14 Jul 2020 13:13");

    record = test.assertPresent(records.findRecord(Key.of("id", 4)));
    assertRecord(record)
        .hasFieldValue("id", 4)
        .hasFieldValue("customer_id", 2)
        .hasFieldValue("export_job_status_id", 3)
        .hasFieldValue("export_job_type_id", 1)
        .hasFieldValue("creator_id", 4)
        .hasFieldValue("creator_email", "cdevoto@maddogtehcnology.com")
        .hasFieldValue("name", "Export Chart Data: Tue, 14 Jul 2020 13:02");

    // assert contents of ac_tag_tbl
    table = "ac_tag_tbl";
    records = test.select(table);
    test.assertAdditionalRecs(acTags, records, 1);

    int nextId = acTags - 2 + 1;
    record = test.assertPresent(records.findRecord(Key.of("id", nextId)));
    assertRecord(record)
        .hasFieldValue("id", nextId)
        .hasFieldValue("tag_type", "CUSTOMER")
        .hasFieldValue("name", "Customer Tag 1");

    // assert contents of ac_customer_tag_tbl
    table = "ac_customer_tag_tbl";
    records = test.select(table);
    test.assertAdditionalRecs(table, records, 1);

    record = test.assertPresent(records.findRecord(Key.of("id", nextId)));
    assertRecord(record)
        .hasFieldValue("id", nextId)
        .hasFieldValue("customer_id", 2);
  }


}
