package org.devoware.builder.part9.builder_subclassing_w_interfaces;

import static com.google.common.base.Preconditions.checkArgument;

public class Goat extends AbstractAnimal {

  private final int numHorns;
  
  public static Builder builder () {
    return new Builder();
  }

  private Goat(Builder builder) {
    super(builder);
    this.numHorns = builder.numHorns;
  }
  
  public int getNumHorns() {
    return numHorns;
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + numHorns;
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
    Goat other = (Goat) obj;
    if (numHorns != other.numHorns)
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "Goat [numHorns=" + numHorns + ", name=" + getName() + "]";
  }




  public static class Builder extends AbstractAnimal.Builder<Goat, Builder> {
    private Integer numHorns;
    
    private Builder () {}
    
    public Builder withNumHorns (int numHorns) {
      checkArgument(numHorns >= 0, "numHorns cannot be negative");
      this.numHorns = numHorns;
      return this;
    }

    @Override
    protected Builder getThis() {
      return this;
    }

    @Override
    protected Goat newInstance() {
      return new Goat(this);
    }
    
  }
}
