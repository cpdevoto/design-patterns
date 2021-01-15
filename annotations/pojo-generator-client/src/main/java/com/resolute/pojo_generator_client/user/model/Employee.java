package com.resolute.pojo_generator_client.user.model;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import com.google.common.collect.ImmutableMap;

public class Employee {
  private final String firstname;
  private final String lastname;
  private final Map<Integer, List<String>> roles;
  private final String username;

  public static Builder builder() {
    return new Builder();
  }

  public static Builder builder(Employee employee) {
    return new Builder(employee);
  }

  private Employee(Builder builder) {
    this.firstname = builder.firstname;
    this.lastname = builder.lastname;
    this.roles = builder.roles;
    this.username = builder.username;
  }

  public Optional<String> getFirstname() {
    return Optional.ofNullable(firstname);
  }

  public Optional<String> getLastname() {
    return Optional.ofNullable(lastname);
  }

  public Optional<Map<Integer, List<String>>> getRoles() {
    return Optional.ofNullable(roles);
  }

  public String getUsername() {
    return username;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + Objects.hash(firstname, lastname, roles, username);
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Employee other = (Employee) obj;
    return Objects.equals(firstname, other.firstname) && Objects.equals(lastname, other.lastname)
        && Objects.equals(roles, other.roles) && Objects.equals(username, other.username);
  }

  @Override
  public String toString() {
    return "Employee [firstname=" + firstname + ", lastname=" + lastname + ", roles=" + roles
        + ", username=" + username + "]";
  }

  public static class Builder {
    private String firstname;
    private String lastname;
    private Map<Integer, List<String>> roles;
    private String username;

    private Builder() {}

    private Builder(Employee employee) {
      requireNonNull(employee, "employee cannot be null");
      this.firstname = employee.firstname;
      this.lastname = employee.lastname;
      this.roles = employee.roles;
      this.username = employee.username;
    }

    public Builder with(Consumer<Builder> consumer) {
      requireNonNull(consumer, "consumer cannot be null");
      consumer.accept(this);
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

    public Builder withRoles(Map<Integer, List<String>> roles) {
      this.roles = (roles == null ? null : ImmutableMap.copyOf(roles));
      return this;
    }

    public Builder withUsername(String username) {
      requireNonNull(username, "username cannot be null");
      this.username = username;
      return this;
    }

    public Employee build() {
      requireNonNull(username, "username cannot be null");
      return new Employee(this);
    }
  }
}
