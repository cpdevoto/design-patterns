package org.devoware.homonculus.validators.validation;

import java.lang.reflect.InvocationTargetException;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Compare the value of a field to the value of a different field using the specified comparison
 * operator.
 */
public class FieldCompareValidator implements ConstraintValidator<FieldCompare, Object> {
  private static final Logger log = LoggerFactory.getLogger(FieldCompareValidator.class);

  private String firstFieldName;
  private ComparisonOperator operator;
  private String secondFieldName;
  @SuppressWarnings("rawtypes")
  private Class<? extends Comparable> fieldClass;

  @Override
  public void initialize(final FieldCompare constraintAnnotation) {
    this.firstFieldName = constraintAnnotation.first();
    this.operator = constraintAnnotation.operator();
    this.secondFieldName = constraintAnnotation.second();
    this.fieldClass = constraintAnnotation.fieldClass();
  }

  @SuppressWarnings("unchecked")
  @Override
  public boolean isValid(Object value, ConstraintValidatorContext context) {
    try {
      final Object firstObj = PropertyUtils.getProperty(value, firstFieldName);
      final Object secondObj = PropertyUtils.getProperty(value, secondFieldName);
      return operator.compare(fieldClass.cast(firstObj), fieldClass.cast(secondObj));
    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      log.error("A problem occurred while attempting to evaluate the FieldCompare annotation", e);
      throw new RuntimeException(e);
    }
  }
}
