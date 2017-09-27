package com.resolutebi.baseline.expr;

interface Expression <T> {
  
  public T value(Inputs inputs);
  
  public Class<T> getType();

}
