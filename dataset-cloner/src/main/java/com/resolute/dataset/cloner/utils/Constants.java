package com.resolute.dataset.cloner.utils;

public class Constants {
  public static final String SET_SESSION_REPLICATION_ROLE_SQL =
      "SET session_replication_role = replica;";
  public static final int DEFAULT_MAX_RECS_PER_INSERT = 5000;
  public static final int DEFAULT_MAX_RECS_PER_DELETE = 5000;

}
