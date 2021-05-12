package com.resolute.dataset.cloner.app;

import com.resolute.dataset.cloner.engine.RootNodeSelectSpecification;
import com.resolute.dataset.cloner.utils.TempTables;

class SelectSpecs {

  static class Building {

    static final RootNodeSelectSpecification ROOT_SELECT_EMAIL_NOTIFICATION =
        tableNamePrefix -> "SELECT id "
            + "FROM "
            + TempTables.getTempTableName(tableNamePrefix,
                "building_email_notification_tbl");

    static final RootNodeSelectSpecification ROOT_SELECT_RAW_POINT =
        tableNamePrefix -> "SELECT t2.raw_point_id AS id "
            + "FROM "
            + TempTables.getTempTableName(tableNamePrefix,
                "mappable_point_tbl")
            + " t1 "
            + "JOIN mappable_point_tbl t2 ON t1.id = t2.id "
            + "WHERE t2.raw_point_id IS NOT NULL";

    private Building() {}

  }

  static class Customer {

    static final RootNodeSelectSpecification ROOT_SELECT_EMAIL_NOTIFICATION =
        tableNamePrefix -> "SELECT id "
            + "FROM "
            + TempTables.getTempTableName(tableNamePrefix,
                "building_email_notification_tbl")
            + " UNION "
            + "SELECT id "
            + "FROM "
            + TempTables.getTempTableName(tableNamePrefix,
                "customer_email_notification_tbl");

    static final RootNodeSelectSpecification ROOT_SELECT_CLIENT_CREDENTIAL =
        tableNamePrefix -> "SELECT id "
            + "FROM "
            + TempTables.getTempTableName(tableNamePrefix,
                "customer_client_credential_tbl");

    static final RootNodeSelectSpecification ROOT_SELECT_USER =
        tableNamePrefix -> "SELECT id "
            + "FROM "
            + TempTables.getTempTableName(tableNamePrefix,
                "customer_user_tbl");

    static final RootNodeSelectSpecification ROOT_SELECT_AC_TAG =
        tableNamePrefix -> "SELECT id "
            + "FROM "
            + TempTables.getTempTableName(tableNamePrefix,
                "ac_customer_tag_tbl")
            + " UNION "
            + "SELECT id "
            + "FROM "
            + TempTables.getTempTableName(tableNamePrefix,
                "ac_task_tag_tbl");

    private Customer() {}

  }

  static class Distributor {

    static final RootNodeSelectSpecification ROOT_SELECT_EMAIL_NOTIFICATION =
        tableNamePrefix -> "SELECT id "
            + "FROM "
            + TempTables.getTempTableName(tableNamePrefix,
                "building_email_notification_tbl")
            + " UNION "
            + "SELECT id "
            + "FROM "
            + TempTables.getTempTableName(tableNamePrefix,
                "customer_email_notification_tbl")
            + " UNION "
            + "SELECT id "
            + "FROM "
            + TempTables.getTempTableName(tableNamePrefix,
                "distributor_email_notification_tbl");

    static final RootNodeSelectSpecification ROOT_SELECT_CLIENT_CREDENTIAL =
        tableNamePrefix -> "SELECT id "
            + "FROM "
            + TempTables.getTempTableName(tableNamePrefix,
                "customer_client_credential_tbl")
            + " UNION "
            + "SELECT id "
            + "FROM "
            + TempTables.getTempTableName(tableNamePrefix,
                "distributor_client_credential_tbl");

    static final RootNodeSelectSpecification ROOT_SELECT_USER =
        tableNamePrefix -> "SELECT id "
            + "FROM "
            + TempTables.getTempTableName(tableNamePrefix,
                "customer_user_tbl")
            + " UNION "
            + "SELECT id "
            + "FROM "
            + TempTables.getTempTableName(tableNamePrefix,
                "distributor_user_tbl");

    static final RootNodeSelectSpecification ROOT_SELECT_AC_TAG =
        tableNamePrefix -> "SELECT id "
            + "FROM "
            + TempTables.getTempTableName(tableNamePrefix,
                "ac_customer_tag_tbl")
            + " UNION "
            + "SELECT id "
            + "FROM "
            + TempTables.getTempTableName(tableNamePrefix,
                "ac_task_tag_tbl");

    private Distributor() {}

  }

  private SelectSpecs() {}

}
