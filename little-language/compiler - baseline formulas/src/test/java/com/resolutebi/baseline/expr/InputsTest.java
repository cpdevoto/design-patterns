package com.resolutebi.baseline.expr;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.resolutebi.baseline.expr.Inputs;
import com.resolutebi.baseline.expr.VariableId;

public class InputsTest {

  @Test
  public void test_construction() {
    Inputs inputs = Inputs.builder()
        .withInput(VariableId.WEEK_DAY, true)
        .withInput(VariableId.AVG_DAILY_TEMP, 25.0)
        .build();
    
    assertNotNull(inputs);
    assertThat(inputs.getValue(VariableId.WEEK_DAY), equalTo(true));
    assertThat(inputs.getValue(VariableId.AVG_DAILY_TEMP), equalTo(25.0));
  }
  
  @Test
  public void test_values_must_be_specified_for_all_variables() {
    Inputs.Builder builder = Inputs.builder();
    
    builder.withInput(VariableId.WEEK_DAY, true);
    
    try {
      builder.build();
      fail("Expected an IllegalStateException");
    } catch (IllegalStateException ex) {}
    
    builder.withInput(VariableId.AVG_DAILY_TEMP, 25.0);
    builder.build();
    
    builder = Inputs.builder();
    
    builder.withInput(VariableId.AVG_DAILY_TEMP, 25.0);
    
    try {
      builder.build();
      fail("Expected an IllegalStateException");
    } catch (IllegalStateException ex) {}
    
    builder.withInput(VariableId.WEEK_DAY, true);
    builder.build();
  }

}
