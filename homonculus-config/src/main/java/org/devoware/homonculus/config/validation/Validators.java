package org.devoware.homonculus.config.validation;

import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.hibernate.validator.HibernateValidatorConfiguration;

/**
 * A utility class for Hibernate.
 */
public class Validators {
  private Validators() { /* singleton */ }

  /**
   * Creates a new {@link Validator} based on {@link #newValidatorFactory()}
   */
  public static Validator newValidator() {
    return newValidatorFactory().getValidator();
  }

  /**
   * Creates a new {@link ValidatorFactory} based on {@link #newConfiguration()}
   */
  public static ValidatorFactory newValidatorFactory() {
    return newConfiguration().buildValidatorFactory();
  }

  /**
   * Creates a new {@link HibernateValidatorConfiguration} with all the custom
   * {@link org.hibernate.validator.spi.valuehandling.ValidatedValueUnwrapper} registered.
   */
  public static HibernateValidatorConfiguration newConfiguration() {
    return BaseValidator.newConfiguration();
  }
}
