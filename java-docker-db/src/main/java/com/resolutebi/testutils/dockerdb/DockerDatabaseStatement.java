package com.resolutebi.testutils.dockerdb;

import static com.resolutebi.testutils.dockerdb.RuleUtils.DATABASE_HOST_PROP;
import static com.resolutebi.testutils.dockerdb.RuleUtils.DATABASE_NAME_PROP;
import static com.resolutebi.testutils.dockerdb.RuleUtils.DATABASE_PASSWORD_PROP;
import static com.resolutebi.testutils.dockerdb.RuleUtils.DATABASE_PORT_PROP;
import static com.resolutebi.testutils.dockerdb.RuleUtils.DATABASE_USER_PROP;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

import static java.util.Objects.requireNonNull;

import jersey.repackaged.com.google.common.collect.ImmutableList;

class DockerDatabaseStatement extends Statement {

  private static final Logger log = LoggerFactory.getLogger(DockerDatabaseStatement.class);

  private static final String DOCKER_DEVELOP_REPO = "maddogtechnology-docker-develop.jfrog.io";
  private static final String DOCKER_STABLE_REPO = "maddogtechnology-docker-stable.jfrog.io";

  private static final String IMAGE = "postgres-schema";
  private static final String IMAGE_DEPENDENCY_KEY = "postgres";

  private final DockerClient docker;
  private final String image;
  private final Statement base;
  private final String databaseHost;
  private final String databasePort;
  private final String databaseName;
  private final String databaseUser;
  private final String databasePassword;

  private String containerId;

  DockerDatabaseStatement(Statement base, DockerDatabase rule) {
    this.base = requireNonNull(base, "base cannot be null");
    requireNonNull(rule, "rule cannot be null");
    this.docker = rule.docker();
    ApplicationConfig conf = rule.conf();
    this.databaseHost = conf.expectProperty(DATABASE_HOST_PROP);
    this.databasePort = conf.expectProperty(DATABASE_PORT_PROP);
    this.databaseName = conf.expectProperty(DATABASE_NAME_PROP);
    this.databaseUser = conf.expectProperty(DATABASE_USER_PROP);
    this.databasePassword = conf.expectProperty(DATABASE_PASSWORD_PROP);
    this.image = computeImageName();
  }

  @Override
  public void evaluate() throws Throwable {
    startDockerDatabase();
    try {
      executeTests();
    } finally {
      stopDockerDatabase();
    }
  }

  private void startDockerDatabase() throws DockerException, InterruptedException, IOException {
    log.info("Starting Docker database " + image + "...");
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
    log.info("Docker database " + image + " started.");
    waitForConnection();
  }

  private void waitForConnection() throws InterruptedException, IOException {
    int counter = 0;
    boolean connectionEstablished = false;
    String url = "jdbc:postgresql://localhost:" + databasePort + "/" + databaseName;
    for (; !connectionEstablished && counter < 44; counter++) {

      try {
        DriverManager.getConnection(url, databaseUser, databasePassword);
        connectionEstablished = true;
        log.info("Trying to establish a connection to the database: SUCCESS");
      } catch (SQLException e) {
        log.info("Trying to establish a connection to the database: FAILED");
        Thread.sleep(1000);
      }
    }

    if (!connectionEstablished) {
      throw new IOException("Could not establish a connection to database: " + url);
    }
  }

  private void stopDockerDatabase() throws DockerException, InterruptedException {
    log.info("Stopping Docker database " + image + "...");
    if (this.containerId != null) {
      docker.killContainer(this.containerId);
      docker.removeContainer(this.containerId);
    }
    log.info("Docker database " + image + " stopped.");
  }

  private void executeTests() throws Throwable {
    base.evaluate();
  }

  private ContainerConfig containerConfig() {
    String containerPort = "5432";
    final Map<String, List<PortBinding>> portBindings = Maps.newHashMap();
    portBindings.put(containerPort, ImmutableList.of(PortBinding.of(databaseHost, databasePort)));

    final HostConfig hostConfig = HostConfig.builder().portBindings(portBindings).build();

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
