# distributed-coordination-service 
**Owner(s):** Carlos Devoto

This library contains a `DistributedCoordinationService` class which encapsulates all of the ZooKeeper logic needed to facilitate the following coordination tasks among workers operating in a distributed cluster:

  * Dynamic hashmod-based resharding any time a new worker is introduced or an existing worker drops out of the cluster.
  * Distributed locks on entity instances to ensure that no two workers are processing data for the same entity instance at the same time.
  
## Usage 

To use this library in a different Java project, add the following ``compile`` directive to the ``dependencies`` section of your ``build.gradle`` file:
```groovy
compile "com.resolute:distributed-coordination-service:${rbiDepVersion}"
```

## Sample Code

```java
public class DistributedCoordinationServiceTest {

  @ClassRule
  public static DockerZookeeper zookeeper = new DockerZookeeper();


  @Test
  public void test_start() throws InterruptedException {
    DistributedCoordinationService service = DistributedCoordinationService.builder()
        .withHostPort("127.0.0.1:2181")
        .withWatcher((shard) -> {
          System.out.println("New shard assigned: " + new String(Shard.toBytes(shard), Charsets.UTF_8));
        })
        .build();
    service.start();

    Optional<Lock> lock = Optional.empty();
    try {
      lock = service.lock("customers", 4);
      if (lock.isPresent()) {
        System.out.println("Doing some work on customer 4!");
      } else {
        System.out.println("Could not obtain a lock on customer 4!");
      }
      Thread.sleep(300000);
    } finally {
      lock.ifPresent(lck -> service.unlock(lck));
    }
  }
}

```

