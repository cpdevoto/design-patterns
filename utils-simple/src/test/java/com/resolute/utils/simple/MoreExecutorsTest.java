package com.resolute.utils.simple;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

public class MoreExecutorsTest {


  @Test
  public void test() {
    System.out.println(
        "-------------------------------------------------------------------------------------------");
    System.out.println(
        "Executing more jobs than the thread pool can handle with the default abort rejection policy");
    System.out.println(
        "-------------------------------------------------------------------------------------------");
    assertThatThrownBy(() -> {
      // Create a thread pool with a bounded queue, using the default abort rejection policy.
      // If we post more jobs to this pool than it can handle, we should get an exception
      Executor workers1 = new ThreadPoolExecutor(2, 2,
          0L, TimeUnit.MILLISECONDS,
          new LinkedBlockingQueue<Runnable>(1));

      IntStream.range(0, 5)
          .boxed()
          .map(i -> CompletableFuture.runAsync(() -> {
            System.out.println("Executing job in thread " + Thread.currentThread().getName());
            try {
              Thread.sleep(1_000);
            } catch (InterruptedException e) {
            }
          }, workers1))
          .collect(toList());
    }).isInstanceOf(RejectedExecutionException.class);


    System.out.println(
        "\n-------------------------------------------------------------------------------------------");
    System.out.println(
        "Executing more jobs than the thread pool can handle with the caller runs rejection policy");
    System.out.println(
        "-------------------------------------------------------------------------------------------");
    // Create a thread pool with a bounded queue, using the caller runs rejection policy.
    // If we post most jobs to this pool than it can handle, the caller will execute the spillover
    // jobs thereby throttling the work; in other words, no exception will be thrown!
    Executor workers2 = MoreExecutors.newFixedThreadPoolWithBoundedQueue(2, 1);

    List<CompletableFuture<Void>> futures = IntStream.range(0, 5)
        .boxed()
        .map(i -> CompletableFuture.runAsync(() -> {
          System.out.println("Executing job in thread " + Thread.currentThread().getName());
          try {
            Thread.sleep(5_000);
          } catch (InterruptedException e) {
          }
        }, workers2))
        .collect(toList());

    futures.stream()
        .forEach(CompletableFuture::join);

  }
}
