package com.resolute.dataset.cloner.engine;

@FunctionalInterface
public interface RootNodeSelectSpecification {

  public String getRootSelectStatement(int tableNamePrefix);

}
