package com.resolute.coord;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.apache.zookeeper.Watcher;

public interface DistributedCoordinationService extends Watcher, ManagedService {

  public static Builder builder() {
    return DistributedCoordinationServiceImpl.builder();
  }

  public Optional<Lock> lock(String entity, int id);

  public Optional<Lock> lock(String entity, int id, long timeout, TimeUnit unit);

  public void unlock(Lock lock);

  public boolean isLeader();

  public boolean isConnected();

  public boolean isExpired();

  public static interface Builder {

    public Builder withHostPort(String hostPort);

    public Builder withWatcher(ShardChangeWatcher watcher);

    public DistributedCoordinationService build();
  }

}
