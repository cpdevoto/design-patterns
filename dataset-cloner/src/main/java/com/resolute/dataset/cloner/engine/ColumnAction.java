package com.resolute.dataset.cloner.engine;

enum ColumnAction {
  COPY, OMIT, MUTATE, FK_LOOKUP, RESOLVE_AT_ROW_LEVEL;
}
