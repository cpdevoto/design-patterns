package org.devoware.homonculus.validators.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.devoware.homonculus.validators.util.Size;
import org.devoware.homonculus.validators.util.SizeUnit;

/**
 * Check that a {@link Size} being validated is greater than or equal to the minimum value
 * specified.
 */
public class MinSizeValidator implements ConstraintValidator<MinSize, Size> {

  private long minQty = 0;
  private SizeUnit minUnit = SizeUnit.BYTES;

  @Override
  public void initialize(MinSize constraintAnnotation) {
    this.minQty = constraintAnnotation.value();
    this.minUnit = constraintAnnotation.unit();
  }

  @Override
  public boolean isValid(Size value, ConstraintValidatorContext context) {
    return (value == null) || (value.toBytes() >= minUnit.toBytes(minQty));
  }
}
