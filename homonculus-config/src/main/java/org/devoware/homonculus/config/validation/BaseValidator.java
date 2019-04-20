package org.devoware.homonculus.config.validation;

import javax.validation.Validation;
import javax.validation.Validator;

import org.devoware.homonculus.config.validation.valuehandling.GuavaOptionalValidatedValueUnwrapper;
import org.devoware.homonculus.config.validation.valuehandling.OptionalDoubleValidatedValueUnwrapper;
import org.devoware.homonculus.config.validation.valuehandling.OptionalIntValidatedValueUnwrapper;
import org.devoware.homonculus.config.validation.valuehandling.OptionalLongValidatedValueUnwrapper;
import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.HibernateValidatorConfiguration;

public class BaseValidator {
  private BaseValidator() { /* singleton */ }

  /**
   * Creates a new {@link Validator} based on {@link #newConfiguration()}
   */
  public static Validator newValidator() {
    return newConfiguration().buildValidatorFactory().getValidator();
  }

  /**
   * Creates a new {@link HibernateValidatorConfiguration} with the base custom
   * {@link org.hibernate.validator.spi.valuehandling.ValidatedValueUnwrapper} registered.
   */
  public static HibernateValidatorConfiguration newConfiguration() {
    return Validation.byProvider(HibernateValidator.class).configure()
        .addValidatedValueHandler(new GuavaOptionalValidatedValueUnwrapper())
        .addValidatedValueHandler(new OptionalDoubleValidatedValueUnwrapper())
        .addValidatedValueHandler(new OptionalIntValidatedValueUnwrapper())
        .addValidatedValueHandler(new OptionalLongValidatedValueUnwrapper());
  }
}
