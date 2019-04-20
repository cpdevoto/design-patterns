package com.resolutebi.testutils.docker.flume;

import static java.util.Objects.requireNonNull;

public class WatcherKey {
  private CapturedEventWatcher watcher;

  WatcherKey(CapturedEventWatcher watcher) {
    this.watcher = requireNonNull(watcher, "watcher cannot be null");
  }

  CapturedEventWatcher get() {
    return this.watcher;
  }
}
