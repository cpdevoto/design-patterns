package com.resolute.coord;

import static com.resolute.coord.utils.CloudfillDefs.EMPTY;
import static com.resolute.coord.utils.ConcurrencyUtils.await;
import static com.resolute.coord.utils.ZookeeperUtils.parent;
import static com.resolute.coord.utils.ZookeeperUtils.seqNo;
import static java.util.Objects.requireNonNull;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.zookeeper.AsyncCallback.ChildrenCallback;
import org.apache.zookeeper.AsyncCallback.StatCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.KeeperException.ConnectionLossException;
import org.apache.zookeeper.KeeperException.NoNodeException;
import org.apache.zookeeper.KeeperException.NodeExistsException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

public class LockService implements ManagedService {

  private static final Logger log = LoggerFactory.getLogger(LockService.class);


  private final ZooKeeper zk;
  private volatile boolean stopped = false;

  LockService(ZooKeeper zk) {
    this.zk = requireNonNull(zk, "zk cannot be null");
  }

  @Override
  public void start() {}

  @Override
  public void stop() {
    this.stopped = true;
  }

  public Optional<Lock> lock(String path, Optional<Long> timeout, Optional<TimeUnit> unit) {
    if (stopped) {
      return null;
    }
    Optional<Lock> lock = Optional.empty();
    log.info("Attempting to place a lock on the " + path + " node");

    // First, we create an ephemeral sequential lock node as a child of the path
    String lockPath = createLockNode(path);
    String lockSeqNo = seqNo(lockPath);
    lock = Optional.of(new Lock(lockPath));


    // Next we check the children of the path
    CountDownLatch latch = new CountDownLatch(1);
    checkChildren(path, lockSeqNo, latch);
    if (timeout.isPresent() && unit.isPresent()) {
      if (await(latch, timeout.get(), unit.get())) {
        return lock;
      } else {
        return Optional.empty();
      }
    } else {
      await(latch);
      return lock;
    }
  }


  public void unlock(Lock lock) {
    if (stopped) {
      return;
    }
    deleteLockNode(lock.getPath());
  }

  private String createLockNode(String path) {
    if (stopped) {
      return null;
    }
    try {
      String lockPath = zk.create(
          path + "/lock-",
          EMPTY,
          ZooDefs.Ids.OPEN_ACL_UNSAFE,
          CreateMode.EPHEMERAL_SEQUENTIAL);
      log.info("Created lock node " + lockPath);
      return lockPath;
    } catch (ConnectionLossException e) {
      // try again!
      return createLockNode(path);
    } catch (NoNodeException e) {
      createParentNode(path);
      return createLockNode(path);
    } catch (InterruptedException | KeeperException e) {
      throw new DistributedCoordinationException(e);
    }
  }

  private void createParentNode(String path) {
    if (stopped) {
      return;
    }
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

  private void deleteLockNode(String path) {
    if (stopped) {
      return;
    }
    try {
      zk.delete(
          path,
          -1);
      log.info("Deleted lock node " + path);

      // If it is the last lock node, we also want to delete the parent to prevent seq num
      // overflows!
      String parentPath = parent(path);
      deleteParentNodeIfEmpty(parentPath);

    } catch (ConnectionLossException e) {
      // try again!
      deleteLockNode(path);
    } catch (InterruptedException | KeeperException e) {
      throw new DistributedCoordinationException(e);
    }

  }


  private void deleteParentNodeIfEmpty(String path) {
    if (stopped) {
      return;
    }
    ChildrenCallback callback = (statusCode, path2, ctx, locks) -> {
      if (this.stopped) {
        return;
      }
      switch (Code.get(statusCode)) {
        case CONNECTIONLOSS:
          deleteParentNodeIfEmpty(path);
          break;
        case OK:
          if (locks.isEmpty()) {
            deleteParentNode(path);
          }
          break;
        case NONODE:
          // The parent node has been deleted already!
        default:
          log.error("deleteParentNodeIfEmpty failed",
              KeeperException.create(Code.get(statusCode), path));
      }
    };

    zk.getChildren(path,
        false,
        callback,
        null);
  }

  private void deleteParentNode(String path) {
    if (stopped) {
      return;
    }
    try {
      zk.delete(
          path,
          -1);
      log.info("Deleted node " + path);
    } catch (ConnectionLossException e) {
      // try again!
      deleteParentNode(path);
    } catch (InterruptedException | KeeperException e) {
      // This is not a fatal problem!
      log.warn("Failed to delete node " + path);
    }

  }

  private void checkChildren(String path, String lockSeqNo, CountDownLatch latch) {
    if (stopped) {
      return;
    }
    ChildrenCallback callback = (statusCode, path2, ctx, locks) -> {
      if (this.stopped) {
        return;
      }
      switch (Code.get(statusCode)) {
        case CONNECTIONLOSS:
          checkChildren(path, lockSeqNo, latch);
          break;
        case OK:
          log.info("Succesfully got a list of locks: "
              + locks.size()
              + " locks");
          assert locks.size() > 0;
          // Ensure that the list of locks is mutable before sorting
          List<String> mutableLocks = Lists.newArrayList(locks);
          Collections.sort(mutableLocks);
          // Check if the first child has a sequence number equal to the sequence number of the lock
          // we created.
          if (seqNo(mutableLocks.get(0)).equals(lockSeqNo)) {
            log.info("Lock on node " + path + " obtained.");
            latch.countDown();
          } else {
            log.info("Lock on node " + path
                + " is in use by a different worker.");
            checkLockExists(mutableLocks.get(0), path, lockSeqNo, latch);
          }
          break;
        default:
          log.error("checkChildren failed",
              KeeperException.create(Code.get(statusCode), path));
      }
    };

    zk.getChildren(path,
        false,
        callback,
        null);
  }

  private void checkLockExists(String lock, String path, String lockSeqNo, CountDownLatch latch) {
    if (stopped) {
      return;
    }
    log.info("Monitoring lock " + lock);
    Watcher watcher = (e) -> {
      if (this.stopped) {
        return;
      }
      if (e.getType() == EventType.NodeDeleted) {
        assert (path + "/" + lock).equals(e.getPath());
        log.info("Lock " + lock + " has been released");
        checkChildren(path, lockSeqNo, latch);
      }
    };


    StatCallback callback = (statusCode, path2, ctx, stat) -> {
      if (this.stopped) {
        return;
      }
      switch (Code.get(statusCode)) {
        case CONNECTIONLOSS:
          checkLockExists(lock, path, lockSeqNo, latch);
          break;
        case OK:
          break;
        case NONODE:
          log.info("Lock " + lock + " has been released");
          checkChildren(path, lockSeqNo, latch);
          break;
        default:
          log.error("checkChildren failed",
              KeeperException.create(Code.get(statusCode), path));
          break;
      }
    };

    zk.exists(
        path + "/" + lock,
        watcher,
        callback,
        null);
  }

}
