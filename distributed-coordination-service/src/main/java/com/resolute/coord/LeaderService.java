package com.resolute.coord;

import static com.resolute.coord.utils.CloudfillDefs.ASSIGNMENTS;
import static com.resolute.coord.utils.CloudfillDefs.LEADER;
import static com.resolute.coord.utils.CloudfillDefs.WORKERS;
import static com.resolute.coord.utils.ConcurrencyUtils.await;
import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

import org.apache.zookeeper.AsyncCallback.ChildrenCallback;
import org.apache.zookeeper.AsyncCallback.DataCallback;
import org.apache.zookeeper.AsyncCallback.StatCallback;
import org.apache.zookeeper.AsyncCallback.StringCallback;
import org.apache.zookeeper.AsyncCallback.VoidCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.KeeperException.ConnectionLossException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;

class LeaderService implements ManagedService {

  static enum LeaderState {
    RUNNING, ELECTED, NOT_ELECTED
  }

  private static final Logger log = LoggerFactory.getLogger(LeaderService.class);


  private final String serviceId;
  private final ZooKeeper zk;
  private final ExecutorService executor;
  private LeaderState state = LeaderState.RUNNING;
  private volatile boolean stopped = false;

  LeaderService(String serviceId, ZooKeeper zk, ExecutorService executor) {
    this.serviceId = requireNonNull(serviceId, "serviceId cannot be null");
    this.zk = requireNonNull(zk, "zk cannot be null");
    this.executor = requireNonNull(executor, "executor cannot be null");
  }

  LeaderState getState() {
    return state;
  }

  @Override
  public void start() {
    runForLeader();
  }

  @Override
  public void stop() {
    this.stopped = true;
  }


  private void runForLeader() {
    log.info("Running for leader...");
    final StringCallback callback = (statusCode, path, ctx, name) -> {
      if (this.stopped) {
        return;
      }
      switch (Code.get(statusCode)) {
        case CONNECTIONLOSS:
          checkLeader();
          break;
        case OK:
          state = LeaderState.ELECTED;
          takeLeadership();
          break;
        case NODEEXISTS:
          state = LeaderState.NOT_ELECTED;
          leaderExists();
          break;
        default:
          state = LeaderState.NOT_ELECTED;
          log.error("Something went wrong when running for master.",
              KeeperException.create(Code.get(statusCode), path));
      }
    };

    zk.create(
        LEADER,
        serviceId.getBytes(Charsets.UTF_8),
        ZooDefs.Ids.OPEN_ACL_UNSAFE,
        CreateMode.EPHEMERAL,
        callback,
        null);

  }

  private void checkLeader() {
    DataCallback callback = (statusCode, path, ctx, data, stat) -> {
      if (this.stopped) {
        return;
      }
      switch (Code.get(statusCode)) {
        case CONNECTIONLOSS:
          checkLeader();
          return;
        case NONODE:
          runForLeader();
          return;
        case OK:
          if (serviceId.equals(new String(data))) {
            state = LeaderState.ELECTED;
            takeLeadership();
          } else {
            state = LeaderState.NOT_ELECTED;
            leaderExists();
          }
          break;
        default:
          log.error("Something went wrong: ", KeeperException.create(Code.get(statusCode), path));
      }
    };

    zk.getData(
        LEADER,
        false,
        callback,
        null);
  }

  private void leaderExists() {
    log.info("I am not the leader.");
    log.info("Let's start monitoring the leader in case it goes away.");
    Watcher watcher = (e) -> {
      if (this.stopped) {
        return;
      }
      if (e.getType() == EventType.NodeDeleted) {
        assert LEADER.equals(e.getPath());
        runForLeader();
      }
    };


    StatCallback callback = (statusCode, path, ctx, stat) -> {
      if (this.stopped) {
        return;
      }
      switch (Code.get(statusCode)) {
        case CONNECTIONLOSS:
          leaderExists();
          break;
        case OK:
          break;
        case NONODE:
          state = LeaderState.RUNNING;
          runForLeader();
          log.info("It sounds like the previous leader is gone, " +
              "so let's run for leader again.");
          break;
        default:
          checkLeader();
          break;
      }
    };

    zk.exists(
        LEADER,
        watcher,
        callback,
        null);
  }

  private void takeLeadership() {
    log.info("I am the leader.");
    log.info("Let's start monitoring the list of workers.");

    getWorkers();
  }

  private void getWorkers() {
    Watcher watcher = (e) -> {
      if (this.stopped) {
        return;
      }
      if (e.getType() == EventType.NodeChildrenChanged) {
        assert WORKERS.equals(e.getPath());

        getWorkers();
      }
    };


    ChildrenCallback callback = (statusCode, path, ctx, workers) -> {
      if (this.stopped) {
        return;
      }
      switch (Code.get(statusCode)) {
        case CONNECTIONLOSS:
          getWorkers();
          break;
        case OK:
          log.info("Succesfully got a list of workers: "
              + workers.size()
              + " workers");
          executor.execute(() -> {
            regenerateAssignments(workers);
          });
          break;
        default:
          log.error("getWorkers failed",
              KeeperException.create(Code.get(statusCode), path));
      }
    };

    zk.getChildren(WORKERS,
        watcher,
        callback,
        null);
  }

  private void regenerateAssignments(List<String> workers) {
    log.info("Regenerating assignments for the following workers: " + workers);

    deleteAssignments();

    int workerCount = workers.size();
    int counter = 0;
    for (String worker : workers) {
      Shard shard = Shard.builder()
          .withId(counter++)
          .withWorkerCount(workerCount)
          .build();
      createAssignment(worker, shard);
    }
  }

  private void deleteAssignments() {
    log.info("Deleting all existing assignments");
    CountDownLatch latch = new CountDownLatch(1);
    getAndDeleteAssignments(latch);
    await(latch);
    log.info("Successfully deleted all assignments");
  }

  private void getAndDeleteAssignments(CountDownLatch latch) {
    // We retrieve the current assignments synchronously, but delete them asynchronously
    if (this.stopped) {
      latch.countDown();
      return;
    }
    try {
      List<String> assignments = zk.getChildren(ASSIGNMENTS,
          false);
      log.info("Succesfully got a list of assignments: "
          + assignments.size()
          + " assignments");
      if (assignments.size() > 0) {
        CountDownLatch deleteLatch = new CountDownLatch(assignments.size());
        executor.execute(() -> {
          deleteAssignments(assignments, deleteLatch);
        });

        await(deleteLatch);
      }
      latch.countDown();
    } catch (ConnectionLossException e) {
      getAndDeleteAssignments(latch);
    } catch (InterruptedException | KeeperException e) {
      latch.countDown();
      log.error("getAndDeleteAssignments failed",
          e, ASSIGNMENTS);

    }
  }

  private void deleteAssignments(List<String> assignments, CountDownLatch latch) {
    for (String assignment : assignments) {
      log.info("Deleting assignment " + assignment);
      deleteAssignment(assignment, latch);
    }

  }

  private void deleteAssignment(String assignment, CountDownLatch latch) {
    VoidCallback callback = (statusCode, path, ctx) -> {
      if (this.stopped) {
        latch.countDown();
        return;
      }
      switch (Code.get(statusCode)) {
        case CONNECTIONLOSS:
          deleteAssignment(assignment, latch);
          break;
        case OK:
          log.info("Successfully deleted assignment " + path);
          latch.countDown();
          break;
        case NONODE:
          log.warn("Tried to delete non-existent assignment " + path);
          latch.countDown();
          break;
        default:
          latch.countDown();
          log.error("deleteAssignment failed for " + assignment,
              KeeperException.create(Code.get(statusCode), path));
      }
    };

    zk.delete(
        ASSIGNMENTS + "/" + assignment,
        -1,
        callback,
        null);

  }

  private void createAssignment(String worker, Shard shard) {
    StringCallback callback = (statusCode, path, ctx, name) -> {
      if (this.stopped) {
        return;
      }
      switch (Code.get(statusCode)) {
        case CONNECTIONLOSS:
          /*
           * Try again. Note that creating a node again is not a problem. If the znode has already
           * been created, then we get a NODEEXISTS event back.
           */
          createAssignment(worker, shard);

          break;
        case OK:
          log.info("Successfully assigned shard id " + shard.getId() + " to " + worker);

          break;
        case NODEEXISTS:
          log.warn("Assignment already exists for " + worker);

          break;
        default:
          log.error("Something went wrong: ",
              KeeperException.create(Code.get(statusCode), path));
      }
    };

    log.info("Assigning shard id " + shard.getId() + " to " + worker);
    zk.create(
        ASSIGNMENTS + "/" + worker,
        Shard.toBytes(shard),
        ZooDefs.Ids.OPEN_ACL_UNSAFE,
        CreateMode.PERSISTENT,
        callback,
        null);


  }



}
