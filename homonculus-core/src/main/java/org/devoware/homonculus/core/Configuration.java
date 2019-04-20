package org.devoware.homonculus.core;

import javax.validation.Valid;

import org.devoware.homonculus.logging.DefaultLoggingFactory;
import org.devoware.homonculus.logging.LoggingFactory;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

public class Configuration {
  @Valid
  private LoggingFactory logging;

  /**
   * Returns the logging-specific section of the configuration file.
   *
   * @return logging-specific configuration parameters
   */
  @JsonProperty("logging")
  public synchronized LoggingFactory getLoggingFactory() {
    if (logging == null) {
      // Lazy init to avoid a hard dependency to logback
      logging = new DefaultLoggingFactory();
    }
    return logging;
  }

  /**
   * Sets the logging-specific section of the configuration file.
   */
  @JsonProperty("logging")
  public synchronized void setLoggingFactory(LoggingFactory factory) {
    this.logging = factory;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("logging", logging)
        .toString();
  }



}
