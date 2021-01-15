package com.resolute.utils.simple;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MoreExecutors {
  private static RejectedExecutionHandler CALLER_RUNS_HANDLER =
      new ThreadPoolExecutor.CallerRunsPolicy();

  public static ExecutorService newFixedThreadPoolWithBoundedQueue(int nThreads, int maxQueueSize) {
    return new ThreadPoolExecutor(nThreads, nThreads,
        0L, TimeUnit.MILLISECONDS,
        new LinkedBlockingQueue<Runnable>(maxQueueSize),
        CALLER_RUNS_HANDLER);
  }

  private MoreExecutors() {}

}
