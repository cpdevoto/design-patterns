package com.resolutebi.testutils.docker.flume;

import static java.util.Objects.requireNonNull;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

class CapturedEventWatchService implements Runnable {
  private static final ObjectMapper mapper = new ObjectMapper();

  private final AtomicBoolean started = new AtomicBoolean();
  private volatile boolean stopped;
  private final Path folder;
  private final CapturedEventWatcher watcher;
  private final Thread thread;

  CapturedEventWatchService(File folder, CapturedEventWatcher watcher) {
    this.folder = requireNonNull(folder, "folder cannot be null").toPath();
    this.watcher = requireNonNull(watcher, "watcher cannot be null");
    this.thread = new Thread(this);
    this.thread.setDaemon(true);
  }

  public void start() {
    if (!started.compareAndSet(false, true)) {
      throw new IllegalStateException("The watch service has already been started");
    }
    this.thread.start();
  }


  public void stop() {
    stopped = true;
    this.thread.interrupt();
  }

  @Override
  public void run() {
    try {

      Set<File> currentFiles = Sets.newHashSet();

      while (!stopped) {
        Set<File> newFiles = Sets.newHashSet(folder.toFile().listFiles());
        Set<File> addedFiles = ImmutableSet.copyOf(Sets.difference(newFiles, currentFiles));
        currentFiles = newFiles;
        for (File file : addedFiles) {
          if (file.getName().endsWith(".json")) {
            String json = new String(Files.readAllBytes(file.toPath()), Charsets.UTF_8);
            CapturedEvent capturedEvent = mapper.readValue(json, CapturedEvent.class);
            if (!"ping".equals(capturedEvent.getHeaders().get("type"))) {
              this.watcher.accept(capturedEvent.toEvent());
            }
          }
        }
        Thread.sleep(500);
      }
    } catch (InterruptedException e) {
      // do nothing!
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }



}
