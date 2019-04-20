package org.devoware.homonculus.config.test.fixtures;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SimpleConfiguration {

  @JsonProperty
  private String firstName;

  @JsonProperty
  private String lastName;

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

}
