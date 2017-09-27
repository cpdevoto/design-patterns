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
  
}
