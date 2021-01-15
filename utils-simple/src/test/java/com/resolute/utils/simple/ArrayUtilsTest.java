package com.resolute.utils.simple;


import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class ArrayUtilsTest {

  @Test
  public void test() {
    Object value = new int[] {1, 2, 3};
    Object[] actualArray = ArrayUtils.toObjectArray(value);
    assertThat(actualArray).containsExactly(1, 2, 3);

    value = new byte[] {1, 2, 3};
    actualArray = ArrayUtils.toObjectArray(value);
    assertThat(actualArray).containsExactly((byte) 1, (byte) 2, (byte) 3);

    value = new short[] {1, 2, 3};
    actualArray = ArrayUtils.toObjectArray(value);
    assertThat(actualArray).containsExactly((short) 1, (short) 2, (short) 3);

    value = new char[] {'a', 'b', 'c'};
    actualArray = ArrayUtils.toObjectArray(value);
    assertThat(actualArray).containsExactly('a', 'b', 'c');

    value = new long[] {1, 2, 3};
    actualArray = ArrayUtils.toObjectArray(value);
    assertThat(actualArray).containsExactly((long) 1, (long) 2, (long) 3);


    value = new float[] {1, 2, 3};
    actualArray = ArrayUtils.toObjectArray(value);
    assertThat(actualArray).containsExactly((float) 1, (float) 2, (float) 3);


    value = new double[] {1, 2, 3};
    actualArray = ArrayUtils.toObjectArray(value);
    assertThat(actualArray).containsExactly((double) 1, (double) 2, (double) 3);


    value = new boolean[] {false, true};
    actualArray = ArrayUtils.toObjectArray(value);
    assertThat(actualArray).containsExactly(false, true);


    value = new String[] {"hello", "world"};
    actualArray = ArrayUtils.toObjectArray(value);
    assertThat(actualArray).containsExactly("hello", "world");
  }

}
