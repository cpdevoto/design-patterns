package com.resolute.jdbc.simple.fixtures;

import static java.util.Objects.requireNonNull;

public class Foo {
  private final Integer id;
  private final String name;

  public static Builder builder() {
    return new Builder();
  }

  public static Builder builder(Foo foo) {
    return new Builder(foo);
  }

  private Foo(Builder builder) {
    this.id = builder.id;
    this.name = builder.name;
  }

  public Integer getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + id;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
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
    Foo other = (Foo) obj;
    if (id != other.id)
      return false;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "Foo [id=" + id + ", name=" + name + "]";
  }

  public static class Builder {
    private Integer id;
    private String name;

    private Builder() {}

    private Builder(Foo foo) {
      this.id = foo.id;
      this.name = foo.name;
    }

    public Builder withId(int id) {
      this.id = id;
      return this;
    }

    public Builder withName(String name) {
      this.name = requireNonNull(name, "name cannot be null");
      return this;
    }

    public Foo build() {
      requireNonNull(name, "name cannot be null");
      return new Foo(this);
    }

  }
}
