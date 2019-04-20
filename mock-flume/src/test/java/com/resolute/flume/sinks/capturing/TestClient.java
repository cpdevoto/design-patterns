package com.resolute.flume.sinks.capturing;

import java.util.Properties;

import org.apache.flume.Event;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.api.RpcClient;
import org.apache.flume.api.RpcClientFactory;
import org.apache.flume.event.EventBuilder;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;

public class TestClient {

  // Before you run this test client, you must first start up the mock-flume docker image produced
  // when you run ./gradlew clean build
  // You can start the docker image by executing the ./run.sh shell script
  public static void main(String[] args) throws EventDeliveryException {
    RpcClient client = null;
    try {
      Properties props = new Properties();
      props.setProperty("hosts.h1", "localhost:44444");
      props.setProperty("hosts", "h1");
      // props.setProperty("compression-type", "deflate"); --> This causes an exception because the
      // server is not configured to use deflate compression
      props.setProperty("truststore-type", "JKS");
      props.setProperty("ssl", "true");
      props.setProperty("truststore", "flume-dist/trustcacerts.jks");
      props.setProperty("truststore-password", "changeit");
      // props.setProperty("trust-all-certs", "true");
      client = RpcClientFactory.getInstance(props);
      Event event =
          EventBuilder.withBody("hello, world!", Charsets.UTF_8, ImmutableMap.of("txId", "12345"));
      client.append(event);
    } finally {
      if (client != null) {
        client.close();
      }
    }

  }
}
