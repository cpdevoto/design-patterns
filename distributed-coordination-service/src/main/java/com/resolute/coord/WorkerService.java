package com.resolute.coord;

import static com.resolute.coord.utils.CloudfillDefs.ASSIGNMENTS;
import static com.resolute.coord.utils.CloudfillDefs.EMPTY;
import static com.resolute.coord.utils.CloudfillDefs.WORKERS;
import static java.util.Objects.requireNonNull;

import java.util.concurrent.ExecutorService;

import org.apache.zookeeper.AsyncCallback.DataCallback;
import org.apache.zookeeper.AsyncCallback.StringCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;

class WorkerService implements ManagedService {
  private static final Logger log = LoggerFactory.getLogger(WorkerService.class);

  private final String serviceId;
  private final ZooKeeper zk;
  private final ShardChangeWatcher watcher;
  private final ExecutorService executor;
  private volatile boolean stopped = false;
  private Shard shard;

  WorkerService(String serviceId, ZooKeeper zk, ExecutorService executor,
      ShardChangeWatcher watcher) {
    this.serviceId = requireNonNull(serviceId, "serviceId cannot be null");
    this.zk = requireNonNull(zk, "zk cannot be null");
    this.executor = requireNonNull(executor, "executor cannot be null");
    this.watcher = requireNonNull(watcher, "watcher cannot be null");
  }

  @Override
  public void start() {
    register();

    checkAssignment();
  }

  @Override
  public void stop() {
    this.stopped = true;
  }

  private void register() {
    StringCallback callback = (statusCode, path, ctx, name) -> {
      if (this.stopped) {
        return;
      }
      switch (Code.get(statusCode)) {
        case CONNECTIONLOSS:
          /*
           * Try again. Note that registering again is not a problem. If the znode has already been
           * created, then we get a NODEEXISTS event back.
           */
          register();

          break;
        case OK:
          log.info("Registered successfully: worker-" + serviceId);

          break;
        case NODEEXISTS:
          log.warn("Already registered: worker-" + serviceId);

          break;
        default:
          log.error("Something went wrong: ",
              KeeperException.create(Code.get(statusCode), path));
      }
    };

    zk.create(
        WORKERS + "/worker-" + serviceId,
        EMPTY,
        ZooDefs.Ids.OPEN_ACL_UNSAFE,
        CreateMode.EPHEMERAL,
        callback,
        null);

  }

  private void checkAssignment() {
    Watcher watcher = (e) -> {
      if (this.stopped) {
        return;
      }
      if (e.getType() == EventType.NodeDeleted || e.getType() == EventType.NodeCreated) {
        assert (ASSIGNMENTS + "/worker-" + serviceId).equals(e.getPath());
        checkAssignment();
      }
    };


    DataCallback callback = (statusCode, path, ctx, data, stat) -> {
      if (this.stopped) {
        return;
      }
      switch (Code.get(statusCode)) {
        case CONNECTIONLOSS:
          checkAssignment();
          break;
        case OK:
          Shard oldShard = this.shard;
          this.shard = Shard.fromBytes(data);
          if (oldShard == null || !oldShard.equals(this.shard)) {
            log.info("Successfully retrieved assignment for worker-" + serviceId + ": "
                + new String(data, Charsets.UTF_8));
            executor.execute(() -> {
              try {
                this.watcher.shardChanged(shard);
              } catch (Exception e) {
                log.error(
                    "A problem occurred while attempting to propagate the shard changed event", e);
              }
            });
          }
          break;
        case NONODE:
          log.info("Assignment for worker-" + serviceId + " does not exist. Will keep checking.");
          executor.execute(() -> {
            try {
              Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
            checkAssignment();
          });
          break;
        default:
          log.error("Something went wrong: ",
              KeeperException.create(Code.get(statusCode), path));
      }
    };

    zk.getData(
        ASSIGNMENTS + "/worker-" + serviceId,
        watcher,
        callback,
        null);
  }



}
