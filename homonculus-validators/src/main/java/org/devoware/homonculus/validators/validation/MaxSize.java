package org.devoware.homonculus.validators.validation;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import org.devoware.homonculus.validators.util.SizeUnit;

/**
 * The annotated element must be a {@link org.devoware.homonculus.validators.util.Size} whose value
 * must be less than or equal to the specified maximum.
 * <p/>
 * <code>null</code> elements are considered valid
 */
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = MaxSizeValidator.class)
public @interface MaxSize {
  String message() default "must be less than or equal to {value} {unit}";

  Class<?>[] groups() default {};

  @SuppressWarnings("UnusedDeclaration")
  Class<? extends Payload>[] payload() default {};

  /**
   * @return value the element must be less than or equal to
   */
  long value();

  /**
   * @return unit of the value the element must be less than or equal to
   */
  SizeUnit unit() default SizeUnit.BYTES;
}