package com.resolute.utils.simple;

import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

public class ParallelExecutor<IN> {
  private ExecutorService workers;
  private Collection<IN> inputs;
  private Consumer<IN> consumer;

  public static <IN> ParallelExecutor<IN> newParallelExecutor() {
    return new ParallelExecutor<>();
  }

  private ParallelExecutor() {}

  public ParallelExecutor<IN> withWorkers(ExecutorService workers) {
    this.workers = requireNonNull(workers, "workers cannot be null");
    return this;
  }

  public ParallelExecutor<IN> withInputs(Collection<IN> inputs) {
    this.inputs = requireNonNull(inputs, "inputs cannot be null");
    return this;
  }


  public ParallelExecutor<IN> withConsumer(Consumer<IN> consumer) {
    this.consumer = requireNonNull(consumer, "consumer cannot be null");
    return this;
  }


  public void execute() {
    requireNonNull(workers, "workers cannot be null");
    requireNonNull(inputs, "inputs cannot be null");
    requireNonNull(consumer, "consumer cannot be null");
    @SuppressWarnings("rawtypes")
    CompletableFuture[] futures = inputs.stream()
        .map(input -> CompletableFuture.runAsync(() -> consumer.accept(input)))
        .toArray(CompletableFuture[]::new);

    CompletableFuture.allOf(futures).join();
  }

}
