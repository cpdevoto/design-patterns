# java-docker-flume 
**Owner(s):** Carlos Devoto

This library contains a useful JUnit Rule:

**DockerFlume:** This rule uses Java code to automatically pull the ``mock-flume`` Docker image. It then starts a Docker container based on this image before any of your tests are executed.  When your tests are completed, the container is stopped.  
 
## Usage 

To use this library in a different Java project, add the following ``testCompile`` directive to the ``dependencies`` section of your ``build.gradle`` file:
```groovy
testCompile "com.resolute:java-docker-flume:${rbiDepVersion}"
```
## Best Practices
 * Because of how long it takes to bring up and take down the Docker Flume container, you won't want to do it before and after every test, or even before and after every test class.  Instead, you will define the ```DockerFlume``` instance as a static ```ClassRule``` in your test suite class so that it gets executed once before ALL of your tests.  Individual test classes can then each include a static ```DockerFlume``` data member, also annotated as the ```ClassRule``` annotation, which gets initialized to the value of the ```DockerFlume``` instance declared within the test suite class.  This allows you to run the test class independently, while also allowing it to run as part of the test suite. 

## Sample Code

```java
public class DockerFlumeTest {
  @ClassRule
  public static final DockerFlume dockerFlume = new DockerFlume();

  private static RpcClient client;

  @Before
  public void setup() {
    Properties props = new Properties();
    props.setProperty("hosts.h1", "localhost:44444");
    props.setProperty("hosts", "h1");
    props.setProperty("truststore-type", "JKS");
    props.setProperty("ssl", "true");
    props.setProperty("trust-all-certs", "true");
    client = RpcClientFactory.getInstance(props);
  }

  @Test
  public void test_docker_flume() throws EventDeliveryException, InterruptedException {
    CountDownLatch eventLatch = new CountDownLatch(3);
    List<CapturedEvent> events = Lists.newArrayList();

    WatcherKey watcherKey = dockerFlume.addWatcher(event -> {
      events.add(event);
      eventLatch.countDown();
    });

    try {
      Event event =
          EventBuilder.withBody("event1", Charsets.UTF_8,
              ImmutableMap.of("type", "TEST", "eventTime", String.valueOf(System.nanoTime())));
      client.append(event);

      event =
          EventBuilder.withBody("event2", Charsets.UTF_8,
              ImmutableMap.of("type", "TEST", "eventTime", String.valueOf(System.nanoTime())));
      client.append(event);

      event =
          EventBuilder.withBody("event3", Charsets.UTF_8,
              ImmutableMap.of("type", "TEST", "eventTime", String.valueOf(System.nanoTime())));
      client.append(event);

      if (!eventLatch.await(1, TimeUnit.MINUTES)) {
        throw new RuntimeException("Timed out waiting for the events to be processed by Flume");
      }

      assertThat(events.size(), equalTo(3));
      assertThat(events.get(0).getHeaders(), notNullValue());
      assertThat(events.get(1).getHeaders(), notNullValue());
      assertThat(events.get(2).getHeaders(), notNullValue());
      assertThat(events.get(0).getHeaders().get("type"), equalTo("TEST"));
      assertThat(events.get(1).getHeaders().get("type"), equalTo("TEST"));
      assertThat(events.get(2).getHeaders().get("type"), equalTo("TEST"));
      assertThat(events.get(0).getHeaders().get("eventTime"), notNullValue());
      assertThat(events.get(1).getHeaders().get("eventTime"), notNullValue());
      assertThat(events.get(2).getHeaders().get("eventTime"), notNullValue());


      // Sort the list by the eventTime header
      List<CapturedEvent> sorted = events.stream().sorted((e1, e2) -> {
        long eventTime1 = Long.parseLong(e1.getHeaders().get("eventTime"));
        long eventTime2 = Long.parseLong(e2.getHeaders().get("eventTime"));
        long diff = eventTime1 - eventTime2;
        if (diff < 0) {
          return -1;
        } else if (diff > 0) {
          return 1;
        }
        return 0;
      }).collect(Collectors.toList());

      assertThat(sorted.get(0).getBodyAsString(), equalTo("event1"));
      assertThat(sorted.get(1).getBodyAsString(), equalTo("event2"));
      assertThat(sorted.get(2).getBodyAsString(), equalTo("event3"));
    } finally {
      dockerFlume.removeWatcher(watcherKey);
    }
  }

}
```

## Issue Troubleshooting

* Verify that `~/.docker/config.json` is as follows:

```javascript
{
  "credsStore" : "osxkeychain",
  ...
}

```

* If you get the following error: `Caused by: java.io.IOException: Cannot run program "docker-credential-osxkeychain": error=2, No such file or directory`, then perform the following steps
  * open a Terminal window and execute ``echo $PATH`` to confirm that your PATH system variable includes the ``/user/bin/local`` path. If not, you should update your `~/.bashrc` or `~/.bash_profile` file to include the following line: ``export PATH="$PATH:/usr/local/bin"``
  * Even if you did not make the preceding update, you should update your `~/.bashrc` or `~/.bash_profile` file to include the following line (NOTE: You may need to change `Eclipse\ J2EE.app` to match the actual directory that Eclipse is installed in): ``alias eclipse="nohup /Applications/Eclipse\ J2EE.app/Contents/MacOS/eclipse >/dev/null 2>&1 &"``

  * Ensure that you are starting Eclipse via the alias above (this ensures that if you do a `System.out.println(System.getenv("PATH"));`, it contains `/usr/local/bin`)   
