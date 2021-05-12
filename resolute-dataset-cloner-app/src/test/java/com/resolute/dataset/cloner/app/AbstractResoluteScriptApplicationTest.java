package com.resolute.dataset.cloner.app;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.SQLException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.resolute.dataset.cloner.app.integration.AbstractDatabaseTest;
import com.resolute.dataset.cloner.app.integration.IntegrationTestSuite;
import com.resolute.jdbc.simple.DaoUtils;

// TODO: Move the BuildingCloner into a separate resolute-dataset-cloner project.
public abstract class AbstractResoluteScriptApplicationTest extends AbstractDatabaseTest {

  @TempDir
  File tempDir;

  private File confFile;

  @BeforeEach
  void setup() throws IOException, SQLException {
    this.confFile = createConfFile();
  }


  @AfterEach
  void cleanupTestTables() throws IOException, SQLException {
    DaoUtils.executeSqlScript(dataSource, IntegrationTestSuite.class,
        "base-resolute-teardown.sql");
  }

  @Test
  void test_run() throws Exception {

    generatorScriptFile("base-test-data-no-functions.sql");

    assertStateBeforeScript();

    // SETUP
    String[] args = new String[] {confFile.getAbsolutePath()};

    // EXECUTE
    ResoluteScriptApplication.main(args);

    // ASSERT
    assertStateAfterScript();

  }

  private void assertStateBeforeScript() {
    assertThat(select("test1_tbl").size()).isEqualTo(0);
    assertThat(select("test2_tbl").size()).isEqualTo(0);
    assertThat(select("test3_tbl").size()).isEqualTo(0);
    assertThat(select("test4_tbl").size()).isEqualTo(0);
    assertThat(select("test4_closure_tbl").size()).isEqualTo(0);
    assertThat(select("test5_tbl").size()).isEqualTo(0);
    assertThat(select("test6_tbl").size()).isEqualTo(0);
    assertThat(select("test7_tbl").size()).isEqualTo(0);
    assertThat(select("test8_tbl").size()).isEqualTo(0);
    assertThat(select("test9_tbl").size()).isEqualTo(0);
  }

  private void assertStateAfterScript() {
    assertThat(select("test1_tbl").size()).isEqualTo(2);
    assertThat(select("test2_tbl").size()).isEqualTo(2);
    assertThat(select("test3_tbl").size()).isEqualTo(4);
    assertThat(select("test4_tbl").size()).isEqualTo(6);
    assertThat(select("test4_closure_tbl").size()).isEqualTo(12);
    assertThat(select("test5_tbl").size()).isEqualTo(2);
    assertThat(select("test6_tbl").size()).isEqualTo(2);
    assertThat(select("test7_tbl").size()).isEqualTo(2);
    assertThat(select("test8_tbl").size()).isEqualTo(3);
    assertThat(select("test9_tbl").size()).isEqualTo(3);
  }

  private File generatorScriptFile(String inputFile) throws IOException, FileNotFoundException {
    File sqlScript = new File(tempDir, "dataset-cloner.sql");
    try (InputStream in = IntegrationTestSuite.class.getResourceAsStream(inputFile);
        FileOutputStream out = new FileOutputStream(sqlScript)) {
      int read;
      byte[] bytes = new byte[1024];

      while ((read = in.read(bytes)) != -1) {
        out.write(bytes, 0, read);
      }
    }
    return sqlScript;
  }


  private File createConfFile() throws IOException {
    File confFile = new File(tempDir, "dataset-cloner.conf");
    File logFile = new File(tempDir, "dataset-cloner.log");
    File outputFile = new File(tempDir, "dataset-cloner.sql");
    int databasePort = IntegrationTestSuite.getDatabasePort();

    String contents =
        "host=localhost\n" +
            "port=" + databasePort + "\n" +
            "database=resolute_cloud_dev\n" +
            "user=postgres\n" +
            "password=\n" +
            "logFile=" + logFile.getAbsolutePath() + "\n" +
            "outputFile=" + outputFile.getAbsolutePath() + "\n" +
            "numCopies=1\n" +
            "debug=false\n" +
            "entityType=BUILDING\n" +
            "entityId=2";

    try (PrintWriter out = new PrintWriter(new FileWriter(confFile))) {
      out.print(contents);
    }
    return confFile;
  }

}
