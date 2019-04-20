package com.resolute.dataset.cloner;

public interface FieldMutator {
  public String getName();

  public Object mutate(Object value);



}
