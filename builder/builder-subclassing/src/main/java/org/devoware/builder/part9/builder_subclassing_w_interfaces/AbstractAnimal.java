package org.devoware.builder.part9.builder_subclassing_w_interfaces;

import static java.util.Objects.requireNonNull;

abstract class AbstractAnimal implements Animal {

  private final String name;

  protected <T extends AbstractAnimal, B extends Builder<T, B>> AbstractAnimal(B builder) {
    this.name = builder.name();
  }

  @Override
  public final String getName() {
    return name;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
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
    AbstractAnimal other = (AbstractAnimal) obj;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    return true;
  }



  abstract static class Builder<T extends Animal, B extends Builder<T, B>>
      implements Animal.Builder<T, B> {
    private String name;

    protected Builder() {}

    @Override
    public B withName(String name) {
      this.name = requireNonNull(name, "name cannot be null");
      return getThis();
    }

    protected String name() {
      return name;
    }

    @Override
    public T build() {
      requireNonNull(name, "name cannot be null");
      return newInstance();
    }

    protected abstract B getThis();

    protected abstract T newInstance();

  }
}
