package org.devoware.homonculus.config.test.fixtures;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CompositeConfiguration {

  @JsonProperty
  private String serviceName;

  @JsonProperty
  private DatabaseConfiguration database;

  public String getServiceName() {
    return serviceName;
  }

  public DatabaseConfiguration getDatabase() {
    return database;
  }

}
