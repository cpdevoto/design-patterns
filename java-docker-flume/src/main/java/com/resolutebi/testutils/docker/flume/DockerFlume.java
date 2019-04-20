package com.resolutebi.testutils.docker.flume;

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;

public class DockerFlume implements TestRule {

  private final DockerClient docker;
  private final AtomicBoolean started = new AtomicBoolean();
  private final CapturedEventWatchManager watcher = new CapturedEventWatchManager();
  private DockerFlumeStatement statement;


  public DockerFlume() {
    this.docker = new DefaultDockerClient("unix:///var/run/docker.sock");
  }

  public WatcherKey addWatcher(CapturedEventWatcher watcher) {
    return this.watcher.addWatcher(watcher);
  }

  public void removeWatcher(WatcherKey watcherKey) {
    this.watcher.removeWatcher(watcherKey);
  }

  @Override
  public Statement apply(Statement base, Description description) {
    if (!started.compareAndSet(false, true)) {
      return base;
    }

    statement = new DockerFlumeStatement(base, docker, watcher);
    return statement;
  }


}
