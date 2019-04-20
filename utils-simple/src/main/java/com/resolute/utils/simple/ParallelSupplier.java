package com.resolute.utils.simple;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ParallelSupplier<IN, OUT> {
  private ExecutorService workers;
  private Collection<IN> inputs;
  private Function<IN, OUT> mapper;
  private Function<IN, List<OUT>> flatMapper;

  public static <IN, OUT> ParallelSupplier<IN, OUT> newParallelSupplier() {
    return new ParallelSupplier<>();
  }

  private ParallelSupplier() {}

  public ParallelSupplier<IN, OUT> withWorkers(ExecutorService workers) {
    this.workers = requireNonNull(workers, "workers cannot be null");
    return this;
  }

  public ParallelSupplier<IN, OUT> withInputs(Collection<IN> inputs) {
    this.inputs = requireNonNull(inputs, "inputs cannot be null");
    return this;
  }

  public ParallelSupplier<IN, OUT> withMapper(Function<IN, OUT> mapper) {
    checkArgument(this.flatMapper == null, "Cannot specify both a mapper and a flatMapper");
    this.mapper = requireNonNull(mapper, "mapper cannot be null");
    return this;
  }

  public ParallelSupplier<IN, OUT> withFlatMapper(Function<IN, List<OUT>> flatMapper) {
    checkArgument(this.mapper == null, "Cannot specify both a mapper and a flatMapper");
    this.flatMapper = requireNonNull(flatMapper, "flatMapper cannot be null");

    return this;
  }

  public List<OUT> get() {
    requireNonNull(workers, "workers cannot be null");
    requireNonNull(inputs, "inputs cannot be null");
    checkArgument(mapper != null || flatMapper != null,
        "mapper and flatMapper cannot both be null");
    if (this.mapper != null) {
      return getWithMapper();

    } else {
      return getWithFlatMapper();
    }

  }

  private List<OUT> getWithMapper() {
    List<CompletableFuture<OUT>> futures = inputs.stream()
        .map(input -> CompletableFuture.supplyAsync(() -> mapper.apply(input),
            workers))
        .collect(Collectors.toList());

    return futures.stream()
        .map(future -> future.join())
        .collect(Collectors.toList());
  }

  private List<OUT> getWithFlatMapper() {
    List<CompletableFuture<List<OUT>>> futures = inputs.stream()
        .map(input -> CompletableFuture.supplyAsync(() -> flatMapper.apply(input),
            workers))
        .collect(Collectors.toList());

    return futures.stream()
        .flatMap(future -> future.join().stream())
        .collect(Collectors.toList());
  }

}
