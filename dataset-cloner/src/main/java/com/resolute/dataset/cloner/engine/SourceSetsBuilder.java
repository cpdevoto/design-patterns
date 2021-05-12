package com.resolute.dataset.cloner.engine;

import java.util.function.Consumer;

import com.resolute.dataset.cloner.engine.DatasetClonerHelper.Builder;

public interface SourceSetsBuilder {

  Builder withSourceSet(
      Consumer<SourceSet.Builder> sourceSetConfigurator);

}
