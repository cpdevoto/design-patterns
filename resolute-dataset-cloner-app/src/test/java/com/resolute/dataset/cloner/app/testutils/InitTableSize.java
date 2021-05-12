package com.resolute.dataset.cloner.app.testutils;

import static com.resolute.jdbc.simple.QueryHandler.toObject;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.resolute.jdbc.simple.JdbcStatementFactory;

public class InitTableSize {

  private static final Map<String, Integer> sizeByTable = ImmutableMap.<String, Integer>builder()
      .put("distributor_tbl", 2)
      .put("online_distributor_tbl", 1)
      .put("customer_tbl", 1)
      .put("component_tbl", 1)
      .put("user_tbl", 2)
      .put("customer_user_tbl", 1)
      .put("distributor_user_tbl", 1)
      .put("user_setting_tbl", 2)
      .put("user_role_tbl", 3)
      .put("user_application_tbl", 2)
      .put("node_tbl", 10)
      .put("billing_location_tbl", 3)
      .put("building_tbl", 3)
      .put("floor_tbl", 2)
      .put("equipment_tbl", 2)
      .put("raw_point_tbl", 2)
      .put("point_tbl", 2)
      .put("mappable_point_tbl", 2)
      .put("email_notification_tbl", 4)
      .put("distributor_email_notification_tbl", 1)
      .put("customer_email_notification_tbl", 1)
      .put("building_email_notification_tbl", 2)
      .put("node_closure_tbl", 31)
      .put("perspective_tbl", 1)
      .put("standard_perspective_tbl", 1)
      .put("standard_perspective_customer_tbl", 1)
      .put("standard_perspective_customer_node_tbl", 1)
      .put("widget_tbl", 3)
      .put("w_kpi_tbl", 1)
      .put("w_datablock_tbl", 1)
      .put("w_gauge_tbl", 1)
      .put("w_gauge_range_tbl", 1)
      .put("w_gauge_point_tbl", 2)
      .put("perspective_widget_tbl", 2)
      .put("client_credential_tbl", 2)
      .put("customer_client_credential_tbl", 1)
      .put("distributor_client_credential_tbl", 1)
      .put("export_job_tbl", 2)
      .put("ac_customer_tag_tbl", 1)
      .build();

  public static Integer get(String table) {
    if (!sizeByTable.containsKey(table)) {
      throw new IllegalArgumentException("no initial size listed for table " + table);
    }
    return sizeByTable.get(table);
  }

  public static int getAcTagTableSize(JdbcStatementFactory statementFactory) {
    // The initial number of records in ac_tag_tbl will change as more global tags are added
    // so we have to retrieve it dynamically
    return statementFactory.newStatement()
        .withSql("SELECT count(*) AS num_recs FROM ac_tag_tbl")
        .withErrorMessage(
            "A problem occurred while attempting to determine the initial number of records in ac_tag_tbl")
        .executeQuery(toObject(rs -> rs.getInt("num_recs")));

  }

  public static void resetAcTagIdSequence(JdbcStatementFactory statementFactory, int nextId) {
    statementFactory.newStatement()
        .withSql("ALTER SEQUENCE ac_tag_tbl_id_seq RESTART WITH " + nextId)
        .withErrorMessage(
            "A problem occurred while attempting to reset the sequence for the id field within ac_tag_tbl")
        .execute();

  }

  private InitTableSize() {}

}
