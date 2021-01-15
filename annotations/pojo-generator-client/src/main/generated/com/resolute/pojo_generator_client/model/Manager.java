package com.resolute.pojo_generator_client.model;

import static java.util.Objects.requireNonNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.util.Optional;
import java.util.function.Consumer;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = Manager.Builder.class)
public class Manager {
  private final String username;
  private final String firstname;
  private final String lastname;
  private final int numEmployees;

  @JsonCreator
  public static Builder builder () {
    return new Builder();
  }

  public static Builder builder (Manager manager) {
    return new Builder(manager);
  }

  private Manager (Builder builder) {
    this.username = builder.username;
    this.firstname = builder.firstname;
    this.lastname = builder.lastname;
    this.numEmployees = builder.numEmployees;
  }

  public String getUsername() {
    return username;
  }

  public Optional<String> getFirstname() {
    return Optional.ofNullable(firstname);
  }

  public Optional<String> getLastname() {
    return Optional.ofNullable(lastname);
  }

  public int getNumEmployees() {
    return numEmployees;
  }

  @JsonPOJOBuilder
  public static class Builder {
    private String username;
    private String firstname;
    private String lastname;
    private Integer numEmployees;

    private Builder() {}

    private Builder(Manager manager) {
      requireNonNull(manager, "manager cannot be null");
      this.username = manager.username;
      this.firstname = manager.firstname;
      this.lastname = manager.lastname;
      this.numEmployees = manager.numEmployees;
    }

    public Builder with(Consumer<Builder> consumer) {
      requireNonNull(consumer, "consumer cannot be null");
      consumer.accept(this);
      return this;
    }

    public Builder withUsername(String username) {
      requireNonNull(username, "username cannot be null");
      this.username = username;
      return this;
    }

    public Builder withFirstname(String firstname) {
      this.firstname = firstname;
      return this;
    }

    public Builder withLastname(String lastname) {
      this.lastname = lastname;
      return this;
    }

    public Builder withNumEmployees(int numEmployees) {
      this.numEmployees = numEmployees;
      return this;
    }

    public Manager build() {
      requireNonNull(username, "username cannot be null");
      requireNonNull(numEmployees, "numEmployees cannot be null");
      return new Manager(this);
    }
  }
}
