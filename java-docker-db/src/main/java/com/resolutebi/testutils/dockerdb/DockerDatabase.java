package com.resolutebi.testutils.dockerdb;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;

public class DockerDatabase implements TestRule {

  private final DockerClient docker;
  private final ApplicationConfig conf;
  private final AtomicBoolean started = new AtomicBoolean();

  public DockerDatabase() {
    this.docker = new DefaultDockerClient("unix:///var/run/docker.sock");
    try {
      this.conf = ApplicationConfig.load();
    } catch (IOException e) {
      throw new ApplicationConfigLoadException(e);
    }
  }

  public ApplicationConfig conf() {
    return conf;
  }

  @Override
  public Statement apply(Statement base, Description description) {
    if (!started.compareAndSet(false, true)) {
      return base;
    }

    return new DockerDatabaseStatement(base, this);
  }

  DockerClient docker() {
    return docker;
  }

}
