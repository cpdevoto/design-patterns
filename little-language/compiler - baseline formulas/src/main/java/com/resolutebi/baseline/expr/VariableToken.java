package com.resolutebi.baseline.expr;

class VariableToken extends Token {

  private final VariableId<?> id;
  
  VariableToken(VariableId<?> id, Position position) {
    super(Type.VARIABLE, position);
    this.id = id;
  }
  
  @SuppressWarnings("unchecked")
  <T> VariableId<T> id () {
    return (VariableId<T>) id;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((id == null) ? 0 : id.hashCode());
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
    VariableToken other = (VariableToken) obj;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "VariableToken [id=" + id + ", getType()=" + getType() + ", getPosition()="
        + getPosition() + "]";
  }

}
