package org.devoware.homonculus.config.test.fixtures;

import com.fasterxml.jackson.annotation.JsonProperty;

public class HostConfiguration {

  @JsonProperty
  private String host;

  @JsonProperty
  private int port;

  public String getHost() {
    return host;
  }

  public int getPort() {
    return port;
  }

}
