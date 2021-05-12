package com.resolute.dataset.cloner.app;

import java.util.function.Consumer;

import com.resolute.dataset.cloner.engine.FieldLevelMutator;
import com.resolute.dataset.cloner.engine.TupleLevelMutator;

class Mutators {

  static FieldLevelMutator METRIC_ID_MUTATOR =
      (tableNamePrefix, copyNumber, value) -> "__.Bldg" + tableNamePrefix + copyNumber + ".__"
          + value;

  static Consumer<TupleLevelMutator.Context> NODE_TBL_MUTATOR =
      c -> c.setValue("display_name", c.getValue("name"));

  private Mutators() {}

}
