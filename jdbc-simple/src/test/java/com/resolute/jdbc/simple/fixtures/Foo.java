package com.resolute.jdbc.simple.fixtures;

import static java.util.Objects.requireNonNull;

import java.util.Optional;

public class Foo {
  public static enum Column {
    ID("id"), BAR_ID("bar_id"), NAME("name");

    private final String name;

    private Column(String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }
  }

  private final Integer id;
  private final Integer barId;
  private final String name;

  public static Builder builder() {
    return new Builder();
  }

  public static Builder builder(Foo foo) {
    return new Builder(foo);
  }

  private Foo(Builder builder) {
    this.id = builder.id;
    this.barId = builder.barId;
    this.name = builder.name;
  }

  public Optional<Integer> getId() {
    return Optional.ofNullable(id);
  }

  public Optional<Integer> getBarId() {
    return Optional.ofNullable(barId);
  }

  public String getName() {
    return name;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((barId == null) ? 0 : barId.hashCode());
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
    Foo other = (Foo) obj;
    if (barId == null) {
      if (other.barId != null)
        return false;
    } else if (!barId.equals(other.barId))
      return false;
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
    return "Foo [id=" + id + ", barId=" + barId + ", name=" + name + "]";
  }

  public static class Builder {
    private Integer id;
    private Integer barId;
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

    public Builder withBarId(int barId) {
      this.barId = barId;
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
