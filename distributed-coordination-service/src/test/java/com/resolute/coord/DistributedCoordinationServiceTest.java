package com.resolute.coord;

import static java.util.Objects.requireNonNull;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.ClassRule;
import org.junit.Test;

import com.resolutebi.testutils.dockerzk.DockerZookeeper;

public class DistributedCoordinationServiceTest {

  @ClassRule
  public static DockerZookeeper zookeeper = new DockerZookeeper();


  @Test
  public void test_shard_watcher() throws InterruptedException {
    DistributedCoordinationService service1 = null;
    DistributedCoordinationService service2 = null;

    try {

      Thread.sleep(5000); // let zookeeper clean up anything left over from the prior test

      // Initially, there should only be one worker with shard id=0 and workerCount=1, and it should
      // be the leader
      CountDownLatch latch1_1 = new CountDownLatch(1);
      ShardCaptor shardCaptor1 = new ShardCaptor();

      shardCaptor1.setWatcher(() -> {
        latch1_1.countDown();
      });

      service1 = DistributedCoordinationService.builder()
          .withHostPort("127.0.0.1:" + zookeeper.getPort())
          .withWatcher(shardCaptor1)
          .build();
      service1.start();

      assertThat("Timed out waiting for the shard change event",
          latch1_1.await(1, TimeUnit.MINUTES),
          equalTo(true));

      Shard shard1 = shardCaptor1.get();
      assertThat(shard1.getId(), equalTo(0));
      assertThat(shard1.getWorkerCount(), equalTo(1));

      assertThat(service1.isLeader(), equalTo(true));

      // If we add a second worker, each worker should have a new shard assigned. One shard will
      // have
      // an id of 0 and one will have an id of 1. The workerCount should be 2 for both shards. The
      // first worker should be the leader


      CountDownLatch latch1_2 = new CountDownLatch(1);

      shardCaptor1.setWatcher(() -> {
        latch1_2.countDown();
      });

      CountDownLatch latch2_1 = new CountDownLatch(1);
      ShardCaptor shardCaptor2 = new ShardCaptor();

      shardCaptor2.setWatcher(() -> {
        latch2_1.countDown();
      });

      service2 = DistributedCoordinationService.builder()
          .withHostPort("127.0.0.1:" + zookeeper.getPort())
          .withWatcher(shardCaptor2)
          .build();
      service2.start();


      boolean id0Found = false;
      boolean id1Found = false;

      assertThat("Timed out waiting for the shard change event",
          latch1_2.await(1, TimeUnit.MINUTES),
          equalTo(true));

      shard1 = shardCaptor1.get();
      assertThat(shard1.getWorkerCount(), equalTo(2));
      if (shard1.getId() == 0) {
        id0Found = true;
      } else if (shard1.getId() == 1) {
        id1Found = true;
      }

      assertThat(service1.isLeader(), equalTo(true));

      assertThat("Timed out waiting for the shard change event",
          latch2_1.await(1, TimeUnit.MINUTES),
          equalTo(true));

      Shard shard2 = shardCaptor2.get();
      assertThat(shard2.getWorkerCount(), equalTo(2));
      if (shard2.getId() == 0) {
        id0Found = true;
      } else if (shard2.getId() == 1) {
        id1Found = true;
      }

      assertThat(service2.isLeader(), equalTo(false));

      assertThat(id0Found && id1Found, equalTo(true));

      // If we shutdown the first worker, the second worker should remain; it should have shard id=0
      // and workerCount=1, and it should
      // be the leader

      CountDownLatch latch2_2 = new CountDownLatch(1);

      shardCaptor2.setWatcher(() -> {
        latch2_2.countDown();
      });

      service1.stop();

      assertThat("Timed out waiting for the shard change event",
          latch2_2.await(1, TimeUnit.MINUTES),
          equalTo(true));

      shard2 = shardCaptor2.get();
      assertThat(shard2.getId(), equalTo(0));
      assertThat(shard2.getWorkerCount(), equalTo(1));

      assertThat(service2.isLeader(), equalTo(true));
    } finally {
      if (service1 != null && service1.isConnected()) {
        service1.stop();
      }
      if (service2 != null && service2.isConnected()) {
        service2.stop();
      }
    }
  }

  @Test
  public void test_distributed_lock() throws InterruptedException {

    DistributedCoordinationService service1 = null;
    DistributedCoordinationService service2 = null;

    Thread.sleep(5000); // let zookeeper clean up anything left over from the prior test

    try {

      // Test that locks on the same entity are serialized

      CountDownLatch latch1_1 = new CountDownLatch(1);
      CountDownLatch latch1_2 = new CountDownLatch(1);
      CountDownLatch latch1_3 = new CountDownLatch(1);
      ShardCaptor shardCaptor1 = new ShardCaptor();
      LockCaptor lockCaptor1 = new LockCaptor();

      shardCaptor1.setWatcher(() -> {
        latch1_1.countDown();
      });

      service1 = DistributedCoordinationService.builder()
          .withHostPort("127.0.0.1:" + zookeeper.getPort())
          .withWatcher(shardCaptor1)
          .build();
      DistributedCoordinationService svc1 = service1;
      service1.start();

      CountDownLatch latch2_1 = new CountDownLatch(1);
      CountDownLatch latch2_2 = new CountDownLatch(1);
      CountDownLatch latch2_3 = new CountDownLatch(1);
      ShardCaptor shardCaptor2 = new ShardCaptor();
      LockCaptor lockCaptor2 = new LockCaptor();

      shardCaptor2.setWatcher(() -> {
        latch2_1.countDown();
      });

      service2 = DistributedCoordinationService.builder()
          .withHostPort("127.0.0.1:" + zookeeper.getPort())
          .withWatcher(shardCaptor2)
          .build();
      DistributedCoordinationService svc2 = service2;
      service2.start();

      assertThat("Timed out waiting for the shard change event",
          latch1_1.await(1, TimeUnit.MINUTES),
          equalTo(true));

      assertThat("Timed out waiting for the shard change event",
          latch2_1.await(1, TimeUnit.MINUTES),
          equalTo(true));

      new Thread(() -> {
        Optional<Lock> lock1 = svc1.lock("customers", 4, 1, TimeUnit.MINUTES);
        lockCaptor1.set(lock1);
        latch1_2.countDown();
        try {
          assertThat("Timed out waiting for the shard change event",
              latch1_3.await(1, TimeUnit.MINUTES),
              equalTo(true));
        } catch (InterruptedException e) {
        }
        if (lock1.isPresent()) {
          svc1.unlock(lock1.get());
        }
      }).start();

      assertThat("Timed out waiting for the shard change event",
          latch1_2.await(1, TimeUnit.MINUTES),
          equalTo(true));

      new Thread(() -> {
        Optional<Lock> lock2 = svc2.lock("customers", 4, 1, TimeUnit.MINUTES);
        lockCaptor2.set(lock2);
        latch2_2.countDown();
        try {
          assertThat("Timed out waiting for the shard change event",
              latch2_3.await(1, TimeUnit.MINUTES),
              equalTo(true));
        } catch (InterruptedException e) {
        }
        if (lock2.isPresent()) {
          svc2.unlock(lock2.get());
        }
      }).start();

      Thread.sleep(5000);
      assertThat(lockCaptor1.lock.isPresent(), equalTo(true));
      assertThat(lockCaptor2.lock.isPresent(), equalTo(false));

      latch1_3.countDown();

      assertThat("Timed out waiting for the shard change event",
          latch2_2.await(1, TimeUnit.MINUTES),
          equalTo(true));

      assertThat(lockCaptor2.lock.isPresent(), equalTo(true));

      latch2_3.countDown();

    } finally {
      if (service1 != null && service1.isConnected()) {
        service1.stop();
      }
      if (service2 != null && service2.isConnected()) {
        service2.stop();
      }
    }

  }

  private static class ShardCaptor implements ShardChangeWatcher {
    private Shard shard;
    private Runnable watcher;

    private ShardCaptor() {}

    private Shard get() {
      return this.shard;
    }

    private void setWatcher(Runnable watcher) {
      this.watcher = requireNonNull(watcher, "watcher cannot be null");
    }

    @Override
    public void shardChanged(Shard shard) {
      this.shard = shard;
      if (watcher != null) {
        watcher.run();
      }
    }
  }

  private static class LockCaptor {
    private Optional<Lock> lock = Optional.empty();

    private LockCaptor() {}

    private void set(Optional<Lock> lock) {
      this.lock = lock;
    }

  }
}

