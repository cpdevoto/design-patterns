package com.resolute.utils.simple;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableList;

public class ParallelSupplierTest {

  @Test
  public void test_mapper() {
    List<String> inputs = ImmutableList.of("one", "two", "three");

    ExecutorService workers = Executors.newFixedThreadPool(3);
    List<String> outputs = ParallelSupplier.<String, String>newParallelSupplier()
        .withWorkers(workers)
        .withInputs(inputs)
        .withMapper(input -> input)
        .get();

    assertThat(outputs).containsOnly("one", "two", "three");

  }

  @Test
  public void test_mapper_exception() {
    List<String> inputs = ImmutableList.of("one", "two", "three");

    ExecutorService workers = Executors.newFixedThreadPool(3);

    assertThatThrownBy(() -> {
      ParallelSupplier.<String, String>newParallelSupplier()
          .withWorkers(workers)
          .withInputs(inputs)
          .withMapper(input -> {
            if (input.equals("one")) {
              throw new IllegalArgumentException("Invalid input: " + input);
            }
            return input;
          })
          .get();
    }).isInstanceOf(CompletionException.class)
        .hasCauseInstanceOf(IllegalArgumentException.class);

  }

  @Test
  public void test_flat_mapper() {
    List<String> inputs = ImmutableList.of("one", "two", "three");

    ExecutorService workers = Executors.newFixedThreadPool(3);
    List<String> outputs = ParallelSupplier.<String, String>newParallelSupplier()
        .withWorkers(workers)
        .withInputs(inputs)
        .withFlatMapper(input -> ImmutableList.of(input, input))
        .get();

    assertThat(outputs).hasSize(6).contains("one", "two", "three");

    Map<String, Long> counts =
        outputs.stream().collect(groupingBy(e -> e, counting()));

    assertThat(counts.get("one")).isEqualTo(2L);
    assertThat(counts.get("two")).isEqualTo(2L);
    assertThat(counts.get("three")).isEqualTo(2L);

  }

  @Test
  public void test_flat_mapper_exception() {
    List<String> inputs = ImmutableList.of("one", "two", "three");

    ExecutorService workers = Executors.newFixedThreadPool(3);

    assertThatThrownBy(() -> {
      ParallelSupplier.<String, String>newParallelSupplier()
          .withWorkers(workers)
          .withInputs(inputs)
          .withFlatMapper(input -> {
            if (input.equals("one")) {
              throw new IllegalArgumentException("Invalid input: " + input);
            }
            return ImmutableList.of(input, input);
          })
          .get();
    }).isInstanceOf(CompletionException.class)
        .hasCauseInstanceOf(IllegalArgumentException.class);

  }

}
