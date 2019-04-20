package com.resolute.utils.simple;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;

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

    assertThat(outputs.size(), equalTo(3));
    assertThat(outputs, hasItems("one", "two", "three"));
  }

  @Test
  public void test_exception() {
    List<String> inputs = ImmutableList.of("one", "two", "three");
    Set<String> outputs = Sets.newHashSet();

    ExecutorService workers = Executors.newFixedThreadPool(3);
    try {
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
      fail("Expected a CompletionException");
    } catch (CompletionException e) {
      assertThat(e.getCause(), instanceOf(IllegalArgumentException.class));
    }
  }

}
