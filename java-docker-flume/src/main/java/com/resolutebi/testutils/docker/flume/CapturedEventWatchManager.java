package com.resolutebi.testutils.docker.flume;

import static java.util.Objects.requireNonNull;

import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.flume.Event;

import com.google.common.collect.Sets;

class CapturedEventWatchManager implements CapturedEventWatcher {
  private final ReadWriteLock lock = new ReentrantReadWriteLock();
  private final Set<CapturedEventWatcher> watchers = Sets.newLinkedHashSet();

  public WatcherKey addWatcher(CapturedEventWatcher watcher) {
    WatcherKey key = new WatcherKey(requireNonNull(watcher, "watcher cannot be null"));
    lock.writeLock().lock();
    try {
      watchers.add(watcher);
      return key;
    } finally {
      lock.writeLock().unlock();
    }
  }

  public void removeWatcher(WatcherKey watcherKey) {
    requireNonNull(watcherKey, "watcherKey cannot be null");
    lock.writeLock().lock();
    try {
      watchers.remove(watcherKey.get());
    } finally {
      lock.writeLock().unlock();
    }

  }

  @Override
  public void accept(Event event) throws Exception {
    lock.readLock().lock();
    try {
      for (CapturedEventWatcher watcher : watchers) {
        watcher.accept(event);
      }
    } finally {
      lock.readLock().unlock();
    }

  }
}
