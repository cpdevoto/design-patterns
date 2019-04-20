package org.devoware.homonculus.config.test.fixtures;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SimpleConfigurationWithValidation {

  @JsonProperty
  @NotNull
  private String firstName;

  @JsonProperty
  @NotNull
  @Size(min = 5, max = 12)
  private String lastName;

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

}
