package com.resolute.user;

import static java.util.Objects.requireNonNull;

import java.util.function.Consumer;

public class Department2 {
  private final int id;
  private final String name;

  public static Builder builder() {
    return new Builder();
  }

  public static Builder builder(Department2 department) {
    return new Builder(department);
  }

  private Department2(Builder builder) {
    this.id = builder.id;
    this.name = builder.name;
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public static class Builder {
    private Integer id;
    private String name;

    private Builder() {}

    private Builder(Department2 department) {
      requireNonNull(department, "department cannot be null");
      this.id = department.id;
      this.name = department.name;
    }

    public Builder with(Consumer<Builder> consumer) {
      requireNonNull(consumer, "consumer cannot be null");
      consumer.accept(this);
      return this;
    }

    public Builder withId(int id) {
      this.id = id;
      return this;
    }

    public Builder withName(String name) {
      requireNonNull(name, "name cannot be null");
      this.name = name;
      return this;
    }

    public Department2 build() {
      requireNonNull(id, "id cannot be null");
      requireNonNull(name, "name cannot be null");
      return new Department2(this);
    }
  }
}
