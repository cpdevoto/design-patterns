package com.resolute.coord;

import static java.util.Objects.requireNonNull;

public class Lock {

  private final String path;

  Lock(String path) {
    this.path = requireNonNull(path, "path cannot be null");
  }

  String getPath() {
    return path;
  }

}
