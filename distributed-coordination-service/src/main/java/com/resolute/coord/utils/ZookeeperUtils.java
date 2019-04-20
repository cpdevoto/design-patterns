package com.resolute.coord.utils;

public class ZookeeperUtils {

  public static String parent(String path) {
    int pos = path.lastIndexOf('/');
    if (pos < 1) {
      throw new RuntimeException("Path " + path + " has no parent");
    }
    return path.substring(0, pos);
  }

  public static String seqNo(String path) {
    int pos = path.lastIndexOf('-');
    if (pos == -1 || pos > path.length() - 1) {
      throw new RuntimeException("Path " + path + " does not have a valid sequence number");
    }
    return path.substring(pos + 1);
  }


  private ZookeeperUtils() {}

}
