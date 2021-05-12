package com.resolute.dataset.cloner.utils;

public class TempTables {

  public static String getTempTableName(int tableNamePrefix, String tableName) {
    return "temp_" + tableNamePrefix + "_" + tableName;
  }


  public static String getTempUnaryTableName(int tableNamePrefix, String tableName) {
    return "temp_unary_" + tableNamePrefix + "_" + tableName;
  }


  private TempTables() {}

}
