package com.resolutebi.testutils.dockerdb;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static java.util.Objects.requireNonNull;

public class ApplicationConfig {

  private final Properties conf;

  public static ApplicationConfig load() throws IOException {
    Properties conf = new Properties();
    try (InputStream in =
        ApplicationConfig.class.getClassLoader().getResourceAsStream("jdbc.properties")) {
      conf.load(in);
    }
    return new ApplicationConfig(conf);
  }

  private ApplicationConfig(Properties conf) {
    this.conf = conf;
  }

  public String expectProperty(String name) {
    return requireNonNull(getProperty(name), "Expected a property named " + name);
  }

  public String getProperty(String name) {
    return getProperty(name, null);
  }

  public String getProperty(String name, String defaultValue) {
    return conf.getProperty(name, defaultValue);
  }


}
