package com.resolute.utils.simple;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;
import java.util.stream.Collectors;

class MapParallelFanoutSupplier<T1, T2> implements ParallelFanoutSupplier<T2> {
  private final RootParallelFanoutSupplier<T1> rootSupplier;
  private final Function<? super T1, List<T2>> mapFunction;
  private MapParallelFanoutSupplier<T2, ?> nextSupplier;

  MapParallelFanoutSupplier(RootParallelFanoutSupplier<T1> rootSupplier,
      Function<? super T1, List<T2>> mapFunction) {
    this.rootSupplier = requireNonNull(rootSupplier, "rootSupplier cannot be null");
    this.mapFunction = requireNonNull(mapFunction, "mapFunction cannot be null");
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
  public <R> ParallelFanoutSupplier<R> thenCompose(Function<? super T2, List<R>> mapFunction) {
    requireNonNull(mapFunction, "mapFunction cannot be null");
    this.nextSupplier = new MapParallelFanoutSupplier(rootSupplier, mapFunction);
    return (ParallelFanoutSupplier<R>) this.nextSupplier;
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  public <R> CompletableFuture<List<R>> getFuture(List<T1> inputs, ExecutorService workers) {
    return CompletableFuture.supplyAsync(() -> {
      List<CompletableFuture> futures = inputs.stream()
          .map(input -> {
            CompletableFuture future =
                CompletableFuture.supplyAsync(() -> mapFunction.apply(input), workers);
            if (this.nextSupplier != null) {
              future = future
                  .thenCompose(inputs2 -> nextSupplier.getFuture((List<T2>) inputs2, workers));
            }
            return future;
          })
          .collect(Collectors.toList());

      List outputs = futures.stream()
          .flatMap(future -> ((List<R>) future.join()).stream())
          .collect(Collectors.toList());

      return outputs;

    }, workers);

  }

  @Override
  public List<T2> get() {
    return rootSupplier.getFromRoot();
  }


}
