package com.resolute.utils.simple;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;

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

    assertThat(outputs.size(), equalTo(3));
    assertThat(outputs, hasItems("one", "two", "three"));

  }

  @Test
  public void test_mapper_exception() {
    List<String> inputs = ImmutableList.of("one", "two", "three");

    ExecutorService workers = Executors.newFixedThreadPool(3);
    try {
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
      fail("Expected a CompletionException");
    } catch (CompletionException e) {
      assertThat(e.getCause(), instanceOf(IllegalArgumentException.class));
    }
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

    assertThat(outputs.size(), equalTo(6));
    assertThat(outputs, hasItems("one", "two", "three"));

    Map<String, Long> counts =
        outputs.stream().collect(groupingBy(e -> e, counting()));

    assertThat(counts.get("one"), equalTo(2L));
    assertThat(counts.get("two"), equalTo(2L));
    assertThat(counts.get("three"), equalTo(2L));

  }

  @Test
  public void test_flat_mapper_exception() {
    List<String> inputs = ImmutableList.of("one", "two", "three");

    ExecutorService workers = Executors.newFixedThreadPool(3);
    try {
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
      fail("Expected a CompletionException");
    } catch (CompletionException e) {
      assertThat(e.getCause(), instanceOf(IllegalArgumentException.class));
    }
  }

}
