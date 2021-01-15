package com.resolute.utils.simple;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.resolute.utils.simple.OpenTsdbStringUtils;

public class OpenTsdbStringUtilsTest {

  @Test
  public void test_toValidMetricId() {
    {
      String input = "abc";
      String expected = input;
      assertThat(OpenTsdbStringUtils.toValidMetricId(input)).isEqualTo(expected);
    }
    {
      String ___input = "abc\\";
      String expected = "abc_";
      assertThat(OpenTsdbStringUtils.toValidMetricId(___input)).isEqualTo(expected);
    }
    {
      String input = "a-b-c";
      String expected = input;
      assertThat(OpenTsdbStringUtils.toValidMetricId(input)).isEqualTo(expected);
    }
    {
      String ___input = "a$b(c)A~B@C#1%1^1&-*_(.)/+='?><,";
      String expected = "a_b_c_A_B_C_1_1_1_-___._/_______";
      assertThat(OpenTsdbStringUtils.toValidMetricId(___input)).isEqualTo(expected);
    }
  }

  @Test
  public void test_toCompactMetricId() {
    {
      String input = "abc";
      String expected = input;
      assertThat(OpenTsdbStringUtils.toCompactMetricId(input)).isEqualTo(expected);
    }
    {
      String ___input = "/abc\\";
      String expected = "_abc_";
      assertThat(OpenTsdbStringUtils.toCompactMetricId(___input)).isEqualTo(expected);
    }
    {
      String input = "a-b-c";
      String expected = input;
      assertThat(OpenTsdbStringUtils.toCompactMetricId(input)).isEqualTo(expected);
    }
    {
      String ___input = "a$b(c)A~B@C#1%1^1&-*_(.)/+='?><,";
      String expected = "a_b_c_A_B_C_1_1_1_-___._________";
      assertThat(OpenTsdbStringUtils.toCompactMetricId(___input)).isEqualTo(expected);
    }
  }

}
