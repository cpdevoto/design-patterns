package com.resolute.dataset.cloner.testutils;

import static java.util.Objects.requireNonNull;

import com.resolute.dataset.cloner.Environment;
import com.resolute.dataset.cloner.engine.DatasetCloner;
import com.resolute.dataset.cloner.utils.Key;

public class Test5Cloner extends DatasetCloner {

  public static Builder builder(Environment env) {
    return new Builder(env);
  }

  private Test5Cloner(Builder builder) {
    super(builder, (schemaGraph, sourceSetsBuilder) -> {
      sourceSetsBuilder
          .withSourceSet(sourceSetBuilder -> sourceSetBuilder
              .withRootSelectStatement("test5_tbl",
                  Key.of("id", builder.recordId)));

    });
  }

  public static class Builder extends DatasetCloner.Builder<Test5Cloner, Builder> {

    private Integer recordId;

    private Builder(Environment env) {
      super(env);
    }

    public Builder withRecordId(int recordId) {
      this.recordId = recordId;
      return this;
    }

    @Override
    protected Builder getThis() {
      return this;
    }

    @Override
    protected Test5Cloner newInstance() {
      requireNonNull(recordId, "recordId cannot be null");
      return new Test5Cloner(this);
    }
  }

}
