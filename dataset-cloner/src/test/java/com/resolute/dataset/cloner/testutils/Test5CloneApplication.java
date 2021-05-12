package com.resolute.dataset.cloner.testutils;

import static com.google.common.base.Preconditions.checkArgument;
import static org.assertj.core.api.Assertions.assertThat;

import org.testcontainers.shaded.com.google.common.primitives.Ints;

import com.resolute.dataset.cloner.Application;
import com.resolute.dataset.cloner.Environment;

public class Test5CloneApplication extends Application {

  public static void main(String[] args) throws Exception {
    new Test5CloneApplication().run(args);
  }

  @Override
  public void run(Environment env) {
    assertThat(env).isNotNull();
    assertThat(env.getDataSource()).isNotNull();
    assertThat(env.getLogger()).isNotNull();
    assertThat(env.getSchemaGraph()).isNotNull();
    assertThat(env.getNumCopies()).isEqualTo(1);
    assertThat(env.getOutputFile()).isNotNull();
    assertThat(env.getProperties()).isNotNull()
        .hasSize(1);
    assertThat(env.getProperties().getProperty("customProperty"))
        .isEqualTo("1");

    // The following two lines show how to read a custom property,
    // try converting it into an int, and throw an exception if
    // the conversion fails.
    Integer recordId = Ints.tryParse(env.getProperties().getProperty("customProperty"));
    checkArgument(recordId != null, "expected a positive integer value for customProperty");

    Test5Cloner cloner = Test5Cloner.builder(env)
        .withRecordId(recordId)
        .build();

    try {
      cloner.execute();
    } catch (Throwable t) {
      t.printStackTrace();
      cloner.rollback();
    }

  }

}
