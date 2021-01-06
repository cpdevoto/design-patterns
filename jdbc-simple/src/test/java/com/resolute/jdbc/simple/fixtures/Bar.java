package com.resolute.jdbc.simple.fixtures;

import static java.util.Objects.requireNonNull;

import java.util.Optional;

public class Bar {
  public static enum Column {
    ID("id"), NAME("name");

    private final String name;

    private Column(String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }
  }

  private final Integer id;
  private final String name;

  public static Builder builder() {
    return new Builder();
  }

  public static Builder builder(Bar foo) {
    return new Builder(foo);
  }

  private Bar(Builder builder) {
    this.id = builder.id;
    this.name = builder.name;
  }

  public Optional<Integer> getId() {
    return Optional.ofNullable(id);
  }

  public String getName() {
    return name;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
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
    Bar other = (Bar) obj;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
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
    return "Bar [id=" + id + ", name=" + name + "]";
  }

  public static class Builder {
    private Integer id;
    private String name;

    private Builder() {}

    private Builder(Bar foo) {
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

    public Bar build() {
      requireNonNull(name, "name cannot be null");
      return new Bar(this);
    }

  }
}
