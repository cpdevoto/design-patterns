package com.resolute.coord;

import static com.resolute.coord.utils.CloudfillDefs.ASSIGNMENTS;
import static com.resolute.coord.utils.CloudfillDefs.EMPTY;
import static com.resolute.coord.utils.CloudfillDefs.LOCKS;
import static com.resolute.coord.utils.CloudfillDefs.WORKERS;
import static com.resolute.coord.utils.ZookeeperUtils.parent;
import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.ConnectionLossException;
import org.apache.zookeeper.KeeperException.NoNodeException;
import org.apache.zookeeper.KeeperException.NodeExistsException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.resolute.coord.LeaderService.LeaderState;

public class DistributedCoordinationServiceImpl implements DistributedCoordinationService {
  private static final Logger log =
      LoggerFactory.getLogger(DistributedCoordinationServiceImpl.class);

  private static final int SESSION_TIMEOUT = 30000; // 30 seconds

  private final String serviceId;
  private final String hostPort;
  private final ShardChangeWatcher watcher;
  private ExecutorService executor;
  private ZooKeeper zk;
  private LeaderService leaderService;
  private WorkerService workerService;
  private LockService lockService;
  private volatile boolean connected = false;
  private volatile boolean expired = false;

  public static Builder builder() {
    return new Builder();
  }

  private DistributedCoordinationServiceImpl(Builder builder) {
    this.hostPort = builder.hostPort;
    this.watcher = builder.watcher;
    this.serviceId = Integer.toHexString(new Random(System.nanoTime()).nextInt());

  }

  @Override
  public boolean isLeader() {
    if (this.zk == null || this.leaderService == null) {
      throw new IllegalStateException("The service has not been started");
    }
    return this.leaderService.getState() == LeaderState.ELECTED;
  }

  @Override
  public boolean isConnected() {
    return connected;
  }

  @Override
  public boolean isExpired() {
    return expired;
  }

  @Override
  public Optional<Lock> lock(String entity, int id) {
    if (this.zk == null || this.lockService == null) {
      throw new IllegalStateException("The service has not been started");
    }
    requireNonNull(entity, "entity cannot be null");
    return this.lockService.lock(LOCKS + "/" + entity + "/" + id, Optional.empty(),
        Optional.empty());
  }

  @Override
  public Optional<Lock> lock(String entity, int id, long timeout, TimeUnit unit) {
    if (this.zk == null || this.lockService == null) {
      throw new IllegalStateException("The service has not been started");
    }
    requireNonNull(entity, "entity cannot be null");
    return this.lockService.lock(LOCKS + "/" + entity + "/" + id, Optional.of(timeout),
        Optional.of(unit));
  }

  @Override
  public void unlock(Lock lock) {
    if (this.zk == null || this.lockService == null) {
      throw new IllegalStateException("The service has not been started");
    }
    requireNonNull(lock, "lock cannot be null");
    this.lockService.unlock(lock);
  }

  @Override
  public void start() {
    log.info("Starting worker " + serviceId);

    this.executor = Executors.newFixedThreadPool(10);

    startZookeeper();
    createBootstrapNodes();

    this.leaderService = new LeaderService(serviceId, zk, executor);
    this.leaderService.start();

    this.workerService = new WorkerService(serviceId, zk, executor, watcher);
    this.workerService.start();

    this.lockService = new LockService(zk);
    this.lockService.start();
  }

  @Override
  public void stop() {
    if (this.zk == null) {
      throw new IllegalStateException("The service has not been started");
    }
    this.executor.shutdown();
    log.info("Stopping worker " + serviceId);
    if (this.leaderService != null) {
      this.leaderService.stop();
    }
    if (this.workerService != null) {
      this.workerService.stop();
    }
    if (this.lockService != null) {
      this.lockService.stop();
    }
    stopZookeeper();
  }



  @Override
  public void process(WatchedEvent e) {
    log.info("Processing event: " + e.toString());
    if (e.getType() == Event.EventType.None) {
      switch (e.getState()) {
        case SyncConnected:
          expired = false;
          connected = true;
          break;
        case Disconnected:
          expired = false;
          connected = false;
          break;
        case Expired:
          expired = true;
          connected = false;
          log.error("Session expiration");
          stop();
          start();
          break;
        default:
          break;
      }
    }
  }

  private void startZookeeper() {
    try {
      this.zk = new ZooKeeper(hostPort, SESSION_TIMEOUT, this);
    } catch (IOException e) {
      throw new DistributedCoordinationException(
          "A problem occurred while attempting to establish a connection to Zookeeper", e);
    }
    try {
      while (!connected) {
        Thread.sleep(100);
      }
    } catch (InterruptedException e) {
    }
  }

  private void stopZookeeper() {
    try {
      this.zk.close();
    } catch (InterruptedException e) {
      throw new DistributedCoordinationException(
          "A problem occurred while attempting to close the connection to Zookeeper", e);
    }
  }

  private void createBootstrapNodes() {
    createBootstrapNode(WORKERS);
    createBootstrapNode(ASSIGNMENTS);
  }

  private void createBootstrapNode(String path) {
    try {
      Stat stat = zk.exists(path, false);
      if (stat != null) {
        log.info("Bootstrap node already exists: " + path);
        return;
      }
      zk.create(
          path,
          EMPTY,
          ZooDefs.Ids.OPEN_ACL_UNSAFE,
          CreateMode.PERSISTENT);
      log.info("Bootstrap node created: " + path);
    } catch (NoNodeException e) {
      createParentNode(parent(path));
      createBootstrapNode(path);
    } catch (KeeperException | InterruptedException e) {
      throw new DistributedCoordinationException(e);
    }
  }

  private void createParentNode(String path) {
    try {
      log.info("Attempting to created node " + path);
      zk.create(
          path,
          EMPTY,
          ZooDefs.Ids.OPEN_ACL_UNSAFE,
          CreateMode.PERSISTENT);
      log.info("Created node " + path);
    } catch (ConnectionLossException e) {
      // try again!
      createParentNode(path);
    } catch (NodeExistsException e) {
      // do nothing!
    } catch (NoNodeException e) {
      createParentNode(parent(path));
      createParentNode(path);
    } catch (InterruptedException | KeeperException e) {
      throw new DistributedCoordinationException(e);
    }

  }


  static class Builder implements DistributedCoordinationService.Builder {
    private String hostPort;
    private ShardChangeWatcher watcher;

    private Builder() {}

    @Override
    public Builder withHostPort(String hostPort) {
      this.hostPort = requireNonNull(hostPort, "hostPort cannot be null");
      return this;
    }

    @Override
    public Builder withWatcher(
        ShardChangeWatcher watcher) {
      this.watcher = requireNonNull(watcher, "watcher cannot be null");
      return this;
    }

    @Override
    public DistributedCoordinationService build() {
      requireNonNull(hostPort, "hostPort cannot be null");
      requireNonNull(watcher, "watcher cannot be null");
      return new DistributedCoordinationServiceImpl(this);
    }

  }

}
