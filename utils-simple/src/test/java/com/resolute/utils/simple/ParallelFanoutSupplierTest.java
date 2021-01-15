package com.resolute.utils.simple;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.resolute.utils.simple.fixtures.Device;
import com.resolute.utils.simple.fixtures.KmcDao;
import com.resolute.utils.simple.fixtures.License;
import com.resolute.utils.simple.fixtures.Point;

public class ParallelFanoutSupplierTest {

  @Test
  public void test() {
    KmcDao dao = new KmcDao();

    ExecutorService workers = Executors.newWorkStealingPool();

    Supplier<List<License>> licenseSupplier = () -> dao.getLicenses();
    Function<License, List<Device>> deviceFunction = license -> dao.getDevices(license);
    Function<Device, List<Point>> pointFunction = device -> dao.getPoints(device);

    // Retrieve all licenses
    List<License> licenses =
        ParallelFanoutSupplier.newParallelFanoutSupplier(licenseSupplier, workers)
            .get();

    assertThat(licenses).isNotNull()
        .extracting(License::toString)
        .containsOnly("license1", "license2");

    List<Device> devices =
        ParallelFanoutSupplier.newParallelFanoutSupplier(licenseSupplier, workers)
            .thenCompose(deviceFunction)
            .get();

    assertThat(devices).isNotNull()
        .extracting(Device::toString)
        .containsOnly("device1", "device2", "device3", "device4");

    List<Point> points =
        ParallelFanoutSupplier.newParallelFanoutSupplier(licenseSupplier, workers)
            .thenCompose(deviceFunction)
            .thenCompose(pointFunction)
            .get();

    assertThat(points).isNotNull()
        .extracting(Point::toString)
        .containsOnly("point1", "point2", "point3", "point4", "point5", "point6", "point7",
            "point8");

  }

  @Test
  @Disabled // This test seems to be non-deterministic on Jenkins, so we will ignore it
  public void test_sequential_versus_parallel_timings() {
    KmcDao dao = new KmcDao();

    ExecutorService workers = Executors.newWorkStealingPool();

    // Get the timing in msecs for sequential execution
    long sequentialStart = System.nanoTime();
    List<Point> points = dao.getLicenses().stream()
        .flatMap(license -> dao.getDevices(license).stream()
            .flatMap(device -> dao.getPoints(device).stream()))
        .collect(Collectors.toList());
    long sequentialDuration = (System.nanoTime() - sequentialStart) / 1_000_000;

    assertThat(points).isNotNull().hasSize(8);

    System.out.println("Sequential execution done in " + sequentialDuration + " msecs");

    // Get the timing in msecs for parallel execution
    long parallelStart = System.nanoTime();
    points =
        ParallelFanoutSupplier.newParallelFanoutSupplier(() -> {
          System.out.println("Retrieving licenses");
          List<License> licenses = dao.getLicenses();
          System.out.println("Licenses retrieved");
          return licenses;
        }, workers)
            .thenCompose(license -> {
              System.out.println("Retrieving devices for license " + license);
              List<Device> devices = dao.getDevices(license);
              System.out.println("Devices for license " + license + " retrieved");
              return devices;
            })
            .thenCompose(device -> {
              System.out.println("Retrieving points for device " + device);
              List<Point> pts = dao.getPoints(device);
              System.out.println("Points for device " + device + " retrieved");
              return pts;
            })
            .get();
    long parallelDuration = (System.nanoTime() - parallelStart) / 1_000_000;

    assertThat(points).isNotNull().hasSize(8);

    System.out.println("Parallel execution done in " + parallelDuration + " msecs");

    assertThat(parallelDuration).isLessThan(sequentialDuration);

  }

}
