package com.resolutebi.testutils.docker.flume;

import static java.util.Objects.requireNonNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import org.apache.flume.Event;
import org.apache.flume.api.RpcClient;
import org.apache.flume.api.RpcClientFactory;
import org.apache.flume.event.EventBuilder;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.DockerClient.ListContainersParam;
import com.spotify.docker.client.auth.ConfigFileRegistryAuthSupplier;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.exceptions.ImageNotFoundException;
import com.spotify.docker.client.messages.Container;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.ContainerCreation;
import com.spotify.docker.client.messages.HostConfig;
import com.spotify.docker.client.messages.PortBinding;

class DockerFlumeStatement extends Statement {

  private static final Logger log = LoggerFactory.getLogger(DockerFlumeStatement.class);

  private static final String DOCKER_DEVELOP_REPO = "maddogtechnology-docker-develop.jfrog.io";
  private static final String DOCKER_STABLE_REPO = "maddogtechnology-docker-stable.jfrog.io";

  private static final String IMAGE = "mock-flume";
  private static final String IMAGE_DEPENDENCY_KEY = "mock-flume";

  private final Statement base;
  private final DockerClient docker;
  private final CapturedEventWatcher watcher;
  private final String image;
  private File folder;
  private String containerId;

  DockerFlumeStatement(Statement base, DockerClient docker, CapturedEventWatcher watcher) {
    this.base = requireNonNull(base, "base cannot be null");
    this.docker = requireNonNull(docker, "docker cannot be null");
    this.watcher = requireNonNull(watcher, "watcher cannot be null");
    this.image = computeImageName();
  }

  @Override
  public void evaluate() throws Throwable {
    folder = createTemporaryFolder();
    CapturedEventWatchService watchService = new CapturedEventWatchService(folder, watcher);
    watchService.start();
    startDockerFlume();
    try {
      executeTests();
    } finally {
      try {
        stopDockerFlume();
      } finally {
        try {
          watchService.stop();
        } finally {
          deleteTemporaryFolder();
          this.folder = null;
        }
      }

    }
  }

  private void startDockerFlume() throws DockerException, InterruptedException, IOException {
    log.info("Starting Docker Flume (" + image + ")...");
    log.info("Kill any running " + IMAGE + " containers");
    List<Container> containers = docker.listContainers(ListContainersParam.allContainers(false));
    for (Container c : containers) {
      if (c.image().contains(IMAGE)) {
        docker.killContainer(c.id());
      }
    } ;
    log.info("Containers killed");
    log.info("Removing any existing " + image + " image from the local repository");
    try {
      docker.removeImage(image, true, false);
    } catch (ImageNotFoundException ex) {
      log.info("Did not remove image " + image
          + " because it could not found in the local Docker repository");
    }
    log.info("Image removed");

    log.info("Pulling the latest copy of the " + image + " image from the remote repository");
    ConfigFileRegistryAuthSupplier supplier = new ConfigFileRegistryAuthSupplier();
    docker.pull(image, supplier.authFor(image));
    log.info("Image pulled");
    log.info("Starting a container for image " + image);
    final ContainerCreation creation = docker.createContainer(containerConfig());
    this.containerId = creation.id();
    docker.startContainer(this.containerId);
    log.info("Container started");
    log.info("Docker Flume (" + image + ") started.");
    waitForConnection();
    // Thread.sleep(3000000);
  }

  private void waitForConnection() throws InterruptedException, IOException {
    RpcClient client = null;
    try {
      Properties props = new Properties();
      props.setProperty("hosts.h1", "localhost:44444");
      props.setProperty("hosts", "h1");
      props.setProperty("truststore-type", "JKS");
      props.setProperty("ssl", "true");
      props.setProperty("trust-all-certs", "true");

      int counter = 0;
      boolean connectionEstablished = false;
      for (; !connectionEstablished && counter < 44; counter++) {

        try {
          client = RpcClientFactory.getInstance(props);
          Event event =
              EventBuilder.withBody("hello, world!", Charsets.UTF_8,
                  ImmutableMap.of("type", "ping"));
          client.append(event);
          connectionEstablished = true;
          log.info("Trying to establish a connection to Flume: SUCCESS");
        } catch (Exception e) {
          log.info("Trying to establish a connection to Flume: FAILED");
          Thread.sleep(1000);
        }
      }

      if (!connectionEstablished) {
        throw new IOException("Could not establish a connection to Flume");
      }
    } finally {
      if (client != null) {
        client.close();
      }
    }


  }


  private void stopDockerFlume() throws DockerException, InterruptedException {
    log.info("Stopping Docker Flume (" + image + ")...");
    if (this.containerId != null) {
      docker.killContainer(this.containerId);
      docker.removeContainer(this.containerId);
    }
    log.info("Docker Flume (" + image + ") stopped.");
  }

  private File createTemporaryFolder() throws IOException {
    File createdFolder = File.createTempFile("junit", "", null);
    log.info("Using temporary directory " + createdFolder.getCanonicalPath());
    createdFolder.delete();
    createdFolder.mkdir();
    return createdFolder;
  }

  private void deleteTemporaryFolder() {
    if (folder != null) {
      recursiveDelete(folder);
    }
  }

  private void recursiveDelete(File file) {
    File[] files = file.listFiles();
    if (files != null) {
      for (File each : files) {
        recursiveDelete(each);
      }
    }
    file.delete();
  }


  private void executeTests() throws Throwable {
    base.evaluate();
  }

  private ContainerConfig containerConfig() throws IOException {
    String exposedHost = "localhost";
    String exposedPort = "44444";
    String containerPort = "44444";
    final Map<String, List<PortBinding>> portBindings = Maps.newHashMap();
    portBindings.put(containerPort, ImmutableList.of(PortBinding.of(exposedHost, exposedPort)));

    final HostConfig hostConfig = HostConfig.builder()
        .appendBinds(folder.getCanonicalPath() + ":/opt/apache-flume/captures")
        .portBindings(portBindings)
        .build();

    final ContainerConfig containerConfig = ContainerConfig.builder()
        .hostname("localhost")
        .hostConfig(hostConfig)
        .image(image)
        .exposedPorts(containerPort)
        .build();
    return containerConfig;

  }

  private String computeImageName() {
    final Optional<String> image = getNonDevelopmentImageName();
    if (!image.isPresent()) {
      return DOCKER_DEVELOP_REPO + "/" + IMAGE + ":latest";
    }
    return image.get();
  }

  private Optional<String> getNonDevelopmentImageName() {
    // If this is a non-development build, a docker-dependencies.lock file will exist, and it will
    // contain an entry
    // for a specific tagged version of the Docker image with the key 'postgres'
    final File f = new File("docker-dependencies.lock");
    if (!f.exists()) {
      return Optional.empty();
    }
    final Properties locks = new Properties();
    try (InputStream in = new FileInputStream(f)) {
      locks.load(in);
      final String image = locks.getProperty(IMAGE_DEPENDENCY_KEY);
      if (image == null) {
        throw new DockerDependencyLockReadException(
            "Expected a 'postgres' entry in the docker-dependencies.lock file");
      }
      return Optional.of(DOCKER_STABLE_REPO + "/" + image);
    } catch (IOException e) {
      throw new DockerDependencyLockReadException(e);
    }
  }

}
