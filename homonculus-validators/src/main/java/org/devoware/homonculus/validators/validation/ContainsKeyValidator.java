package org.devoware.homonculus.validators.validation;

import java.util.Map;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Check that a {@link java.util.Map} contains the specified string key.
 */
public class ContainsKeyValidator implements ConstraintValidator<ContainsKeys, Map<String, ?>> {

    private String [] keys;

    @Override
    public void initialize(ContainsKeys constraintAnnotation) {
        this.keys = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(Map<String, ?> value, ConstraintValidatorContext context) {
        for (String key : keys) {
          if (!value.containsKey(key)) {
            return false;
          }
        }
        return true;
    }
}
