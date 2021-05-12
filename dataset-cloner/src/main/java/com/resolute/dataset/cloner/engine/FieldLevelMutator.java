package com.resolute.dataset.cloner.engine;

@FunctionalInterface
public interface FieldLevelMutator {

  public static final FieldLevelMutator DEFAULT =
      (tableNamePrefix, copyNumber, value) -> value + "_" + tableNamePrefix + "_" + copyNumber;

  String mutate(int tableNamePrefix, int copyNumber, String value);

}
