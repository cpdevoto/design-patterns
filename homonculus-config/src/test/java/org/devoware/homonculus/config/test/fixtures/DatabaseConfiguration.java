package org.devoware.homonculus.config.test.fixtures;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DatabaseConfiguration {

  @JsonProperty
  private String driverClass;

  @JsonProperty
  private String user;

  @JsonProperty
  private String password;

  public String getDriverClass() {
    return driverClass;
  }

  public String getUser() {
    return user;
  }

  public String getPassword() {
    return password;
  }

}
