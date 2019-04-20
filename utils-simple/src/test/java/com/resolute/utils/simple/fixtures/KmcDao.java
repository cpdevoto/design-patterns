package com.resolute.utils.simple.fixtures;

import static com.resolute.utils.simple.fixtures.Utils.fixedDelay;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KmcDao {

  private static final List<License> licenses =
      Arrays.asList(new License("license1"), new License("license2"));

  private static final Map<License, List<Device>> devices;

  private static final Map<Device, List<Point>> points;

  static {
    Map<License, List<Device>> dev = new HashMap<>();
    dev.put(new License("license1"), Arrays.asList(new Device("device1"), new Device("device2")));
    dev.put(new License("license2"), Arrays.asList(new Device("device3"), new Device("device4")));
    devices = Collections.unmodifiableMap(dev);

    Map<Device, List<Point>> pt = new HashMap<>();
    pt.put(new Device("device1"), Arrays.asList(new Point("point1"), new Point("point2")));
    pt.put(new Device("device2"), Arrays.asList(new Point("point3"), new Point("point4")));
    pt.put(new Device("device3"), Arrays.asList(new Point("point5"), new Point("point6")));
    pt.put(new Device("device4"), Arrays.asList(new Point("point7"), new Point("point8")));
    points = Collections.unmodifiableMap(pt);

  }

  public List<License> getLicenses() {
    fixedDelay();
    return licenses;
  }

  public List<Device> getDevices(License license) {
    if ("license2".equals(license.toString())) {
      fixedDelay();
      fixedDelay();
    }
    fixedDelay();
    List<Device> result = devices.get(license);
    if (result == null) {
      return new ArrayList<>();
    }
    return result;
  }

  public List<Point> getPoints(Device device) {
    fixedDelay();
    List<Point> result = points.get(device);
    if (result == null) {
      return new ArrayList<>();
    }
    return result;
  }

}
