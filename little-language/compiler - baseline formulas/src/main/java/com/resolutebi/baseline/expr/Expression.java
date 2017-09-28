package com.resolutebi.baseline.expr;

interface Expression <T> {
  
  public T evaluate(Inputs inputs);
  
  public Class<T> getType();

}
