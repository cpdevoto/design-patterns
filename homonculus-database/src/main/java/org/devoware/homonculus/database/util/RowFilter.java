package org.devoware.homonculus.database.util;

public interface RowFilter<T> {

  public boolean accept(T entity);

}
