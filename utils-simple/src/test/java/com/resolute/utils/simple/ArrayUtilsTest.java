package com.resolute.utils.simple;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertThat;

import java.util.Arrays;

import org.junit.Test;

public class ArrayUtilsTest {

  @Test
  public void test() {
    Object value = new int[] {1, 2, 3};
    Object[] array = ArrayUtils.toObjectArray(value);
    assertThat(array.length, equalTo(3));
    assertThat(Arrays.asList(array), hasItems(1, 2, 3));

    value = new byte[] {1, 2, 3};
    array = ArrayUtils.toObjectArray(value);
    assertThat(array.length, equalTo(3));
    assertThat(Arrays.asList(array), hasItems((byte) 1, (byte) 2, (byte) 3));

    value = new short[] {1, 2, 3};
    array = ArrayUtils.toObjectArray(value);
    assertThat(array.length, equalTo(3));
    assertThat(Arrays.asList(array), hasItems((short) 1, (short) 2, (short) 3));

    value = new char[] {'a', 'b', 'c'};
    array = ArrayUtils.toObjectArray(value);
    assertThat(array.length, equalTo(3));
    assertThat(Arrays.asList(array), hasItems('a', 'b', 'c'));

    value = new long[] {1, 2, 3};
    array = ArrayUtils.toObjectArray(value);
    assertThat(array.length, equalTo(3));
    assertThat(Arrays.asList(array), hasItems((long) 1, (long) 2, (long) 3));


    value = new float[] {1, 2, 3};
    array = ArrayUtils.toObjectArray(value);
    assertThat(array.length, equalTo(3));
    assertThat(Arrays.asList(array), hasItems((float) 1, (float) 2, (float) 3));


    value = new double[] {1, 2, 3};
    array = ArrayUtils.toObjectArray(value);
    assertThat(array.length, equalTo(3));
    assertThat(Arrays.asList(array), hasItems((double) 1, (double) 2, (double) 3));


    value = new boolean[] {false, true};
    array = ArrayUtils.toObjectArray(value);
    assertThat(array.length, equalTo(2));
    assertThat(Arrays.asList(array), hasItems(false, true));


    value = new String[] {"hello", "world"};
    array = ArrayUtils.toObjectArray(value);
    assertThat(array.length, equalTo(2));
    assertThat(Arrays.asList(array), hasItems("hello", "world"));
  }

}
