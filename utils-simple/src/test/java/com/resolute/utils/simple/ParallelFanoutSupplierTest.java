package com.resolute.utils.simple;

import static java.util.stream.Collectors.toSet;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.hamcrest.Matchers;
import org.junit.Ignore;
import org.junit.Test;

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

    assertThat(licenses, notNullValue());
    assertThat(licenses.size(), equalTo(2));

    Set<String> licenseIds = licenses.stream()
        .map(License::toString)
        .collect(toSet());

    assertThat(licenseIds.contains("license1"), equalTo(true));
    assertThat(licenseIds.contains("license2"), equalTo(true));

    List<Device> devices =
        ParallelFanoutSupplier.newParallelFanoutSupplier(licenseSupplier, workers)
            .thenCompose(deviceFunction)
            .get();

    assertThat(devices, notNullValue());
    assertThat(devices.size(), equalTo(4));

    Set<String> deviceIds = devices.stream()
        .map(Device::toString)
        .collect(toSet());

    assertThat(deviceIds.contains("device1"), equalTo(true));
    assertThat(deviceIds.contains("device2"), equalTo(true));
    assertThat(deviceIds.contains("device3"), equalTo(true));
    assertThat(deviceIds.contains("device4"), equalTo(true));

    List<Point> points =
        ParallelFanoutSupplier.newParallelFanoutSupplier(licenseSupplier, workers)
            .thenCompose(deviceFunction)
            .thenCompose(pointFunction)
            .get();

    assertThat(points, notNullValue());
    assertThat(points.size(), equalTo(8));

    Set<String> pointIds = points.stream()
        .map(Point::toString)
        .collect(toSet());

    assertThat(pointIds.contains("point1"), equalTo(true));
    assertThat(pointIds.contains("point2"), equalTo(true));
    assertThat(pointIds.contains("point3"), equalTo(true));
    assertThat(pointIds.contains("point4"), equalTo(true));
    assertThat(pointIds.contains("point5"), equalTo(true));
    assertThat(pointIds.contains("point6"), equalTo(true));
    assertThat(pointIds.contains("point7"), equalTo(true));
    assertThat(pointIds.contains("point8"), equalTo(true));

  }

  @Test
  @Ignore // This test seems to be non-deterministic on Jenkins, so we will ignore it
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

    assertThat(points, notNullValue());
    assertThat(points.size(), equalTo(8));

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

    assertThat(points, notNullValue());
    assertThat(points.size(), equalTo(8));

    System.out.println("Parallel execution done in " + parallelDuration + " msecs");

    assertThat(parallelDuration, Matchers.lessThan(sequentialDuration));

  }

}
