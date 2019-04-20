package com.resolutebi.testutils.docker.flume;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.flume.Event;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.api.RpcClient;
import org.apache.flume.api.RpcClientFactory;
import org.apache.flume.event.EventBuilder;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

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
    List<Event> events = Lists.newArrayList();

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
      List<Event> sorted = events.stream().sorted((e1, e2) -> {
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

      assertThat(new String(sorted.get(0).getBody(), Charsets.UTF_8), equalTo("event1"));
      assertThat(new String(sorted.get(1).getBody(), Charsets.UTF_8), equalTo("event2"));
      assertThat(new String(sorted.get(2).getBody(), Charsets.UTF_8), equalTo("event3"));
    } finally {
      dockerFlume.removeWatcher(watcherKey);
    }
  }

}
