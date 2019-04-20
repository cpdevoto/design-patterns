package org.devoware.homonculus.validators.validation;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.devoware.homonculus.config.validation.Validators;
import org.devoware.homonculus.validators.validation.ComparisonOperator;
import org.devoware.homonculus.validators.validation.FieldCompare;
import org.junit.Before;
import org.junit.Test;

public class FieldCompareValidatorTest {

  
  private Validator validator;

  @Before
  public void setup () {
     validator = Validators.newValidator();
  }
  
  @Test
  public void test_field_compare_less_than_is_valid() {
    WorkerConfiguration config = new WorkerConfiguration(1, 2);
    Set<ConstraintViolation<WorkerConfiguration>> violations = validator.validate(config);
    assertThat(violations.size(), equalTo(0));
    
  }

  @Test
  public void test_field_compare_equals_is_valid() {
    WorkerConfiguration config = new WorkerConfiguration(1, 1);
    Set<ConstraintViolation<WorkerConfiguration>> violations = validator.validate(config);
    assertThat(violations.size(), equalTo(0));
  }

  @Test
  public void test_field_compare_greater_than_is_not_valid() {
    WorkerConfiguration config = new WorkerConfiguration(2, 1);
    Set<ConstraintViolation<WorkerConfiguration>> violations = validator.validate(config);
    assertThat(violations.size(), equalTo(1));
  }
  
  @FieldCompare(first = "minThreads", 
      operator = ComparisonOperator.LESS_THAN_OR_EQUALS, 
      second = "maxThreads", 
      fieldClass = Integer.class)
  public static class WorkerConfiguration {
    
    private final int minThreads;
    private final int maxThreads;
    
    public WorkerConfiguration(int minThreads, int maxThreads) {
      super();
      this.minThreads = minThreads;
      this.maxThreads = maxThreads;
    }

    public int getMinThreads() {
      return minThreads;
    }

    public int getMaxThreads() {
      return maxThreads;
    }
  }
}
