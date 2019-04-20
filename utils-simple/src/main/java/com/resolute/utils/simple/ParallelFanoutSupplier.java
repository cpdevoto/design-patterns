package com.resolute.utils.simple;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * The ParallelFanoutSupplier framework is designed to deal with use cases in which we have a
 * fan-out/recursive set of API calls that need to be parallelized. The following use case provides
 * an example of this:
 * <ul>
 * <li>The root API call returns a list of licenses</li>
 * <li>For each license, we want to retrieve a list of devices</li>
 * <li>For each device, we want to retrieve a list of points</li>
 * <li>When the calls are all completed we want to merge all of the point lists together into a
 * single list</li>
 * <li>Since non-terminal futures will spawn additional futures, we want to use a work stealing
 * thread pool to prevent potential deadlocks</li>
 * </ul>
 * 
 * The ParallelFanoutSupplier framework provides a fluent API for this purpose, encapsulating
 * complex logic similar to that shown in the following class. <code>
 *   
public static void main(String[] args) {

    KmcDao dao = new KmcDao();

    ExecutorService workers = Executors.newWorkStealingPool();

    CompletableFuture<List<Point>> f = CompletableFuture.supplyAsync(() -> dao.getLicenses())
        .thenCompose(licenses -> devicesFuture(dao, workers, licenses));



    System.out.println(f.join());
  }

  private static CompletableFuture<List<Point>> devicesFuture(KmcDao dao, ExecutorService workers,
      List<License> licenses) {
    return CompletableFuture.supplyAsync(() -> {
      List<CompletableFuture<List<Point>>> futures = licenses.stream()
          .map(license -> CompletableFuture.supplyAsync(() -> dao.getDevices(license), workers)
              .thenCompose(devices -> pointsFuture(dao, workers, devices)))
          .collect(Collectors.toList());

      List<Point> points = futures.stream()
          .flatMap(future -> future.join().stream())
          .collect(Collectors.toList());

      return points;

    }, workers);
  }

  private static CompletableFuture<List<Point>> pointsFuture(KmcDao dao, ExecutorService workers,
      List<Device> devices) {
    return CompletableFuture.supplyAsync(() -> {
      List<CompletableFuture<List<Point>>> futures = devices.stream()
          .map(device -> CompletableFuture.supplyAsync(() -> dao.getPoints(device), workers))
          .collect(Collectors.toList());

      List<Point> points = futures.stream()
          .flatMap(future -> future.join().stream())
          .collect(Collectors.toList());

      return points;

    }, workers);
  }
}
 * </code>
 * 
 * @author cdevoto
 *
 * @param <T>
 */
public interface ParallelFanoutSupplier<T> {

  public static <T> ParallelFanoutSupplier<T> newParallelFanoutSupplier(
      Supplier<List<T>> rootSupplier, ExecutorService workers) {
    return new RootParallelFanoutSupplier<>(rootSupplier, workers);
  }


  public <R> ParallelFanoutSupplier<R> thenCompose(
      Function<? super T, List<R>> mapFunction);

  public List<T> get();

}
