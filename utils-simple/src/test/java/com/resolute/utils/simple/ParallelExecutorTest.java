package com.resolute.utils.simple;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;

public class ParallelExecutorTest {

  @Test
  public void test() {
    List<String> inputs = ImmutableList.of("one", "two", "three");
    Set<String> outputs = Sets.newHashSet();

    ExecutorService workers = Executors.newFixedThreadPool(3);
    ParallelExecutor.<String>newParallelExecutor()
        .withWorkers(workers)
        .withInputs(inputs)
        .withConsumer(input -> outputs.add(input))
        .execute();

    assertThat(outputs).containsOnly("one", "two", "three");
  }

  @Test
  public void test_exception() {
    List<String> inputs = ImmutableList.of("one", "two", "three");
    Set<String> outputs = Sets.newHashSet();

    ExecutorService workers = Executors.newFixedThreadPool(3);

    assertThatThrownBy(() -> {
      ParallelExecutor.<String>newParallelExecutor()
          .withWorkers(workers)
          .withInputs(inputs)
          .withConsumer(input -> {
            if (input.equals("one")) {
              throw new IllegalArgumentException("Invalid input: " + input);
            }
            outputs.add(input);
          })
          .execute();
    }).isInstanceOf(CompletionException.class)
        .hasCauseInstanceOf(IllegalArgumentException.class);
  }

}
