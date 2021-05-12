package com.resolute.dataset.cloner.app;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import com.google.common.primitives.Ints;
import com.resolute.dataset.cloner.Environment;
import com.resolute.dataset.cloner.engine.DatasetCloner;
import com.resolute.utils.simple.ElapsedTimeUtils;

public class DatasetCloners {

  public static DatasetCloner create(Environment env) {
    requireNonNull(env, "env cannot be null");
    String entityType = env.getProperties().getProperty("entityType");
    checkArgument(entityType != null, "expected an 'entityType' property");
    Entity entity = Entity.valueOf(entityType);
    checkArgument(entity != null, "invalid value for the 'entityType' property");
    Integer entityId = Ints.tryParse(env.getProperties().getProperty("entityId"));
    checkArgument(entityId != null, "expected an 'entityid' property");
    switch (entity) {
      case BUILDING:
        return BuildingCloner.builder(env)
            .withBuildingId(entityId)
            .withBeforeAllListener(
                () -> System.out.println("Starting the building clone operation..."))
            .withAfterAllListener((elapsed) -> System.out
                .println(
                    "Finished the building clone operation in " + ElapsedTimeUtils.format(elapsed)))
            .withBeforeEachListener(
                (copyNumber) -> System.out.println("Creating copy " + copyNumber + "..."))
            .withAfterEachListener((elapsed, copyNumber) -> System.out
                .println("Finished copy " + copyNumber + " in " + ElapsedTimeUtils.format(elapsed)))
            .build();
      case CUSTOMER:
        return CustomerCloner.builder(env)
            .withCustomerId(entityId)
            .withBeforeAllListener(
                () -> System.out.println("Starting the customer clone operation..."))
            .withAfterAllListener((elapsed) -> System.out
                .println(
                    "Finished the customer clone operation in " + ElapsedTimeUtils.format(elapsed)))
            .withBeforeEachListener(
                (copyNumber) -> System.out.println("Creating copy " + copyNumber + "..."))
            .withAfterEachListener((elapsed, copyNumber) -> System.out
                .println("Finished copy " + copyNumber + " in " + ElapsedTimeUtils.format(elapsed)))
            .build();
      case DISTRIBUTOR:
        return DistributorCloner.builder(env)
            .withDistributorId(entityId)
            .withBeforeAllListener(
                () -> System.out.println("Starting the distributor clone operation..."))
            .withAfterAllListener((elapsed) -> System.out
                .println(
                    "Finished the distributor clone operation in "
                        + ElapsedTimeUtils.format(elapsed)))
            .withBeforeEachListener(
                (copyNumber) -> System.out.println("Creating copy " + copyNumber + "..."))
            .withAfterEachListener((elapsed, copyNumber) -> System.out
                .println("Finished copy " + copyNumber + " in " + ElapsedTimeUtils.format(elapsed)))
            .build();
      default:
        // This should never happen!
        throw new AssertionError();
    }
  }

  private DatasetCloners() {}

}
