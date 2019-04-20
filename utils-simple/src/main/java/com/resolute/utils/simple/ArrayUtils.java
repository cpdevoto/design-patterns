package com.resolute.utils.simple;

import java.util.List;

import com.google.common.collect.Lists;

public class ArrayUtils {

  public static Object[] toObjectArray(Object value) {
    if (value instanceof Object[]) {
      return (Object[]) value;
    }
    List<Object> result = Lists.newArrayList();
    if (value instanceof byte[]) {
      byte[] arr = (byte[]) value;
      for (int i = 0; i < arr.length; i++) {
        result.add(arr[i]);
      }
    } else if (value instanceof short[]) {
      short[] arr = (short[]) value;
      for (int i = 0; i < arr.length; i++) {
        result.add(arr[i]);
      }
    } else if (value instanceof char[]) {
      char[] arr = (char[]) value;
      for (int i = 0; i < arr.length; i++) {
        result.add(arr[i]);
      }
    } else if (value instanceof int[]) {
      int[] arr = (int[]) value;
      for (int i = 0; i < arr.length; i++) {
        result.add(arr[i]);
      }
    } else if (value instanceof long[]) {
      long[] arr = (long[]) value;
      for (int i = 0; i < arr.length; i++) {
        result.add(arr[i]);
      }
    } else if (value instanceof float[]) {
      float[] arr = (float[]) value;
      for (int i = 0; i < arr.length; i++) {
        result.add(arr[i]);
      }
    } else if (value instanceof double[]) {
      double[] arr = (double[]) value;
      for (int i = 0; i < arr.length; i++) {
        result.add(arr[i]);
      }
    } else if (value instanceof boolean[]) {
      boolean[] arr = (boolean[]) value;
      for (int i = 0; i < arr.length; i++) {
        result.add(arr[i]);
      }
    } else {
      throw new AssertionError("Invalid data type " + value.getClass().getCanonicalName());
    }
    return result.toArray();
  }

  private ArrayUtils() {}


}
