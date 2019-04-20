package com.resolute.utils.simple;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;
import java.util.function.Supplier;

class RootParallelFanoutSupplier<T> implements ParallelFanoutSupplier<T> {
  private final Supplier<List<T>> rootSupplier;
  private final ExecutorService workers;
  private MapParallelFanoutSupplier<T, ?> nextSupplier;

  RootParallelFanoutSupplier(Supplier<List<T>> rootSupplier, ExecutorService workers) {
    this.rootSupplier = requireNonNull(rootSupplier, "rootSupplier cannot be null");
    this.workers = requireNonNull(workers, "workers cannot be null");
  }

  @SuppressWarnings("unchecked")
  @Override
  public <R> ParallelFanoutSupplier<R> thenCompose(Function<? super T, List<R>> mapFunction) {
    requireNonNull(mapFunction, "mapFunction cannot be null");
    this.nextSupplier = new MapParallelFanoutSupplier<>(this, mapFunction);
    return (ParallelFanoutSupplier<R>) this.nextSupplier;
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  public <R> List<R> getFromRoot() {
    CompletableFuture future = CompletableFuture.supplyAsync(() -> rootSupplier.get(), workers);
    if (nextSupplier != null) {
      future = future.thenCompose(inputs -> nextSupplier.getFuture((List<T>) inputs, workers));
    }
    return (List<R>) future.join();
  }

  @Override
  public List<T> get() {
    CompletableFuture<List<T>> future =
        CompletableFuture.supplyAsync(() -> rootSupplier.get(), workers);
    return future.join();
  }

}
