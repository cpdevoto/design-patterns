package com.resolute.dataset.cloner.engine;

import com.resolute.database.crawler.model.Graph;

@FunctionalInterface
public interface Initializer {
  void initialize(Graph schemaGraph, SourceSetsBuilder sourceSetsBuilder);
}
