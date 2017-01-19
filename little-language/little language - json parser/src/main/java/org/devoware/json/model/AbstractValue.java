package org.devoware.json.model;

public abstract class AbstractValue <T> extends AbstractJsonNode implements Value<T> {

  protected T value;
  
  public AbstractValue(Type type, T value) {
    super(type);
    this.value = value;
  }
  
  @Override
  public final T value() {
    return value;
  }
  
  @Override
  public String toString () {
    return String.valueOf(value);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((value == null) ? 0 : value.hashCode());
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
    @SuppressWarnings("rawtypes")
    AbstractValue other = (AbstractValue) obj;
    if (getType() != other.getType()) {
      return false;
    }
    if (value == null) {
      if (other.value != null)
        return false;
    } else if (!value.equals(other.value))
      return false;
    return true;
  }
  

}
