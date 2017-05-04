package org.devoware.builder;

import static java.util.Objects.requireNonNull;

public class Dog extends Animal {

  private final String bark;
  
  public static Builder builder () {
    return new Builder();
  }

  private Dog(Builder builder) {
    super(builder);
    this.bark = builder.bark;
  }
  
  public String getBark() {
    return bark;
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((bark == null) ? 0 : bark.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    Dog other = (Dog) obj;
    if (bark == null) {
      if (other.bark != null)
        return false;
    } else if (!bark.equals(other.bark))
      return false;
    return true;
  }



  @Override
  public String toString() {
    return "Dog [bark=" + bark + ", name=" + getName() + "]";
  }



  public static class Builder extends Animal.Builder<Dog, Builder> {
    private String bark;
    
    private Builder () {}
    
    public Builder withBark (String bark) {
      this.bark = requireNonNull(bark, "bark cannot be null");
      return this;
    }

    @Override
    protected Builder getThis() {
      return this;
    }

    @Override
    protected Dog newInstance() {
      return new Dog(this);
    }
    
  }
}
