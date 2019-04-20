package org.devoware.homonculus.core.test.fixtures;

import org.devoware.homonculus.core.Configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

public class HostConfiguration extends Configuration {

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
