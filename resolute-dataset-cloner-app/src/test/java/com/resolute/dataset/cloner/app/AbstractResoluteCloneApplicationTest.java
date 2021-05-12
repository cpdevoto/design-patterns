package com.resolute.dataset.cloner.app;

import static com.resolute.dataset.cloner.app.testutils.InitTableSize.getAcTagTableSize;
import static com.resolute.dataset.cloner.app.testutils.InitTableSize.resetAcTagIdSequence;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.function.BiFunction;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.resolute.dataset.cloner.app.integration.AbstractDatabaseTest;
import com.resolute.dataset.cloner.app.integration.IntegrationTestSuite;
import com.resolute.dataset.cloner.app.testutils.BuildingCloneAssertions;
import com.resolute.dataset.cloner.app.testutils.CustomerCloneAssertions;
import com.resolute.dataset.cloner.app.testutils.DistributorCloneAssertions;
import com.resolute.jdbc.simple.DaoUtils;

// TODO: Move the BuildingCloner into a separate resolute-dataset-cloner project.
public abstract class AbstractResoluteCloneApplicationTest extends AbstractDatabaseTest {

  @TempDir
  File tempDir;

  private int acTags;

  @BeforeEach
  void setup() throws IOException, SQLException {
    DaoUtils.executeSqlScript(dataSource, IntegrationTestSuite.class,
        "base-resolute-data.sql");
    this.acTags = getAcTagTableSize(statementFactory);
  }

  @AfterEach
  void teardown() throws IOException, SQLException {
    DaoUtils.executeSqlScript(dataSource, IntegrationTestSuite.class,
        "base-resolute-teardown.sql");
    resetAcTagIdSequence(statementFactory, acTags - 2 + 1);
  }

  @Test
  void test_clone_building() throws Exception {
    File confFile = createBuildingCloneConfFile();

    // SETUP
    String[] args = new String[] {confFile.getAbsolutePath()};

    // EXECUTE
    ResoluteCloneApplication.main(args);

    new BuildingCloneAssertions(this).assertStateAfterClone();

  }

  @Test
  void test_clone_customer() throws Exception {
    File confFile = createCustomerCloneConfFile();

    // SETUP
    String[] args = new String[] {confFile.getAbsolutePath()};

    // EXECUTE
    ResoluteCloneApplication.main(args);

    new CustomerCloneAssertions(this, acTags).assertStateAfterClone();

  }

  @Test
  void test_clone_distributor() throws Exception {
    File confFile = createDistributorCloneConfFile();

    // SETUP
    String[] args = new String[] {confFile.getAbsolutePath()};

    // EXECUTE
    ResoluteCloneApplication.main(args);

    new DistributorCloneAssertions(this, acTags).assertStateAfterClone();

  }

  private File createBuildingCloneConfFile() throws IOException {
    return createConfFile((logFile, databasePort) -> "" +
        "host=localhost\n" +
        "port=" + databasePort + "\n" +
        "database=resolute_cloud_dev\n" +
        "user=postgres\n" +
        "password=\n" +
        "logFile=" + logFile.getAbsolutePath() + "\n" +
        "numCopies=1\n" +
        "debug=false\n" +
        "entityType=BUILDING\n" +
        "entityId=2");
  }

  private File createCustomerCloneConfFile() throws IOException {
    return createConfFile((logFile, databasePort) -> "" +
        "host=localhost\n" +
        "port=" + databasePort + "\n" +
        "database=resolute_cloud_dev\n" +
        "user=postgres\n" +
        "password=\n" +
        "logFile=" + logFile.getAbsolutePath() + "\n" +
        "numCopies=1\n" +
        "debug=false\n" +
        "entityType=CUSTOMER\n" +
        "entityId=1");
  }

  private File createDistributorCloneConfFile() throws IOException {
    return createConfFile((logFile, databasePort) -> "" +
        "host=localhost\n" +
        "port=" + databasePort + "\n" +
        "database=resolute_cloud_dev\n" +
        "user=postgres\n" +
        "password=\n" +
        "logFile=" + logFile.getAbsolutePath() + "\n" +
        "numCopies=1\n" +
        "debug=false\n" +
        "entityType=DISTRIBUTOR\n" +
        "entityId=2");
  }

  private File createConfFile(BiFunction<File, Integer, String> contentGenerator)
      throws IOException {
    File confFile = new File(tempDir, "dataset-cloner.conf");
    File logFile = new File(tempDir, "dataset-cloner.log");
    int databasePort = IntegrationTestSuite.getDatabasePort();

    try (PrintWriter out = new PrintWriter(new FileWriter(confFile))) {
      String contents = contentGenerator.apply(logFile, databasePort);
      out.print(contents);
    }
    return confFile;
  }


}
