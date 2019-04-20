package org.devoware.homonculus.validators.validation;

import static org.devoware.homonculus.validators.validation.ComparisonOperator.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ComparisonOperatorTest {

  @Test
  public void testEquals () {
     assertTrue(EQUALS.compare(1, 1));  
     assertFalse(EQUALS.compare(1, 2));  
     
     assertTrue(EQUALS.compare("Resolute", "Resolute"));  
     assertFalse(EQUALS.compare("Resolute", "resolute"));  
  }

  @Test
  public void testNotEquals () {
     assertFalse(NOT_EQUALS.compare(1, 1));  
     assertTrue(NOT_EQUALS.compare(1, 2));  
     
     assertFalse(NOT_EQUALS.compare("Resolute", "Resolute"));  
     assertTrue(NOT_EQUALS.compare("Resolute", "resolute"));  
  }

  @Test
  public void testGreaterThan () {
     assertFalse(GREATER_THAN.compare(1, 1));  
     assertFalse(GREATER_THAN.compare(1, 2));  
     assertTrue(GREATER_THAN.compare(2, 1));  
     
     assertFalse(GREATER_THAN.compare("Resolute", "Resolute"));  
     assertFalse(GREATER_THAN.compare("Resolute", "resolute"));  
     assertTrue(GREATER_THAN.compare("resolute", "Resolute"));  
  }

  @Test
  public void testLessThan () {
     assertFalse(LESS_THAN.compare(1, 1));  
     assertTrue(LESS_THAN.compare(1, 2));  
     assertFalse(LESS_THAN.compare(2, 1));  
     
     assertFalse(LESS_THAN.compare("Resolute", "Resolute"));  
     assertTrue(LESS_THAN.compare("Resolute", "resolute"));  
     assertFalse(LESS_THAN.compare("resolute", "Resolute"));  
  }

  @Test
  public void testGreaterThanOrEquals () {
     assertTrue(GREATER_THAN_OR_EQUALS.compare(1, 1));  
     assertFalse(GREATER_THAN_OR_EQUALS.compare(1, 2));  
     assertTrue(GREATER_THAN_OR_EQUALS.compare(2, 1));  
     
     assertTrue(GREATER_THAN_OR_EQUALS.compare("Resolute", "Resolute"));  
     assertFalse(GREATER_THAN_OR_EQUALS.compare("Resolute", "resolute"));  
     assertTrue(GREATER_THAN_OR_EQUALS.compare("resolute", "Resolute"));  
  }

  @Test
  public void testLessThanOrEquals () {
    assertTrue(LESS_THAN_OR_EQUALS.compare(1, 1));  
     assertTrue(LESS_THAN_OR_EQUALS.compare(1, 2));  
     assertFalse(LESS_THAN_OR_EQUALS.compare(2, 1));  
     
     assertTrue(LESS_THAN_OR_EQUALS.compare("Resolute", "Resolute"));  
     assertTrue(LESS_THAN_OR_EQUALS.compare("Resolute", "resolute"));  
     assertFalse(LESS_THAN_OR_EQUALS.compare("resolute", "Resolute"));  
  }
}
