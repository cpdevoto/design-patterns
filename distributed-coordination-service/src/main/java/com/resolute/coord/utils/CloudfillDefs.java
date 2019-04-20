package com.resolute.coord.utils;

public class CloudfillDefs {
  public static final String ROOT = "/cloudfill";
  public static final String LEADER = ROOT + "/leader";
  public static final String WORKERS = ROOT + "/workers";
  public static final String ASSIGNMENTS = ROOT + "/assignments";
  public static final String LOCKS = ROOT + "/locks";

  public static final byte[] EMPTY = new byte[0];


  private CloudfillDefs() {}

}
