package com.resolute.pojo_generator_client.model;

import static java.util.Objects.requireNonNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import com.google.common.collect.ImmutableMap;

public class Employee {
  private final String username;
  private final String firstname;
  private final String lastname;
  private final Map<Integer, List<String>> roles;
  private final int[] ids;

  public static Builder builder() {
    return new Builder();
  }

  public static Builder builder(Employee employee) {
    return new Builder(employee);
  }

  private Employee(Builder builder) {
    this.username = builder.username;
    this.firstname = builder.firstname;
    this.lastname = builder.lastname;
    this.roles = builder.roles;
    this.ids = builder.ids;
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

  public Optional<Map<Integer, List<String>>> getRoles() {
    return Optional.ofNullable(roles);
  }

  public Optional<int[]> getIds() {
    return Optional.ofNullable(ids);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((firstname == null) ? 0 : firstname.hashCode());
    result = prime * result + Arrays.hashCode(ids);
    result = prime * result + ((lastname == null) ? 0 : lastname.hashCode());
    result = prime * result + ((roles == null) ? 0 : roles.hashCode());
    result = prime * result + ((username == null) ? 0 : username.hashCode());
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
    if (firstname == null) {
      if (other.firstname != null)
        return false;
    } else if (!firstname.equals(other.firstname))
      return false;
    if (!Arrays.equals(ids, other.ids))
      return false;
    if (lastname == null) {
      if (other.lastname != null)
        return false;
    } else if (!lastname.equals(other.lastname))
      return false;
    if (roles == null) {
      if (other.roles != null)
        return false;
    } else if (!roles.equals(other.roles))
      return false;
    if (username == null) {
      if (other.username != null)
        return false;
    } else if (!username.equals(other.username))
      return false;
    return true;
  }



  public static class Builder {
    private String username;
    private String firstname;
    private String lastname;
    private Map<Integer, List<String>> roles;
    private int[] ids;

    private Builder() {}

    private Builder(Employee employee) {
      requireNonNull(employee, "employee cannot be null");
      this.username = employee.username;
      this.firstname = employee.firstname;
      this.lastname = employee.lastname;
      this.roles = employee.roles;
      this.ids = employee.ids;
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

    public Builder withRoles(Map<Integer, List<String>> roles) {
      this.roles = (roles == null ? null : ImmutableMap.copyOf(roles));
      return this;
    }

    public Builder withIds(int[] ids) {
      this.ids = ids;
      return this;
    }

    public Employee build() {
      requireNonNull(username, "username cannot be null");
      return new Employee(this);
    }
  }
}
