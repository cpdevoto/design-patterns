package com.resolutebi.baseline.expr;

import static java.util.Objects.requireNonNull;

class Variable <T> implements Expression<T> {

  private final VariableId<T> id;
  
  static <T> Variable<T> create (VariableId<T> id) {
    return new Variable<>(id);
  }
  
  public Variable(VariableId<T> id) {
    this.id = id;
  }

  @Override
  public T evaluate(Inputs inputs) {
    requireNonNull(inputs, "inputs cannot be null");
    return inputs.getValue(id);
  }

  @Override
  public Class<T> getType() {
    return id.getType();
  }

  VariableId<T> getId() {
    return id;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
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
    Variable<?> other = (Variable<?>) obj;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return id.toString();
  }

}
