package com.resolute.dataset.cloner;

import static com.resolute.dataset.cloner.testutils.RecordAssertion.assertRecord;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.google.common.base.Charsets;
import com.resolute.dataset.cloner.integration.AbstractDatabaseTest;
import com.resolute.dataset.cloner.integration.IntegrationTestSuite;
import com.resolute.dataset.cloner.log.Logger;
import com.resolute.dataset.cloner.testutils.Records;
import com.resolute.dataset.cloner.testutils.Test5CloneApplication;
import com.resolute.dataset.cloner.testutils.Test5Cloner;
import com.resolute.dataset.cloner.testutils.Test5RollbackApplication;
import com.resolute.dataset.cloner.testutils.Test5ScriptApplication;
import com.resolute.dataset.cloner.utils.Key;
import com.resolute.jdbc.simple.DaoUtils;
import com.resolute.utils.simple.ElapsedTimeUtils;

public abstract class AbstractApplicationTest extends AbstractDatabaseTest {

  @TempDir
  File tempDir;

  @BeforeEach
  void setup() throws IOException, SQLException {
    DaoUtils.executeSqlScript(dataSource, IntegrationTestSuite.class,
        "base-test-data.sql");
  }

  @AfterEach
  void teardown() throws IOException, SQLException {
    DaoUtils.executeSqlScript(dataSource, IntegrationTestSuite.class,
        "base-test-teardown.sql");
  }

  @Test
  public void test_clone_app() throws Exception {


    // SETUP
    File confFile = createConfFile(false);
    String[] args = new String[] {confFile.getAbsolutePath()};

    // EXECUTE
    Test5CloneApplication.main(args);

    // ASSERT
    assertSuccessfulClone();
  }

  @Test
  public void test_clone_app_with_pure_copy_mode() throws Exception {


    // SETUP
    File confFile = createConfFile(true);
    String[] args = new String[] {confFile.getAbsolutePath()};

    // EXECUTE
    Test5CloneApplication.main(args);

    // ASSERT
    assertNoClone();

    File outputFile = new File(tempDir, "dataset-cloner.sql");

    assertThat(outputFile).exists();
    String actual = new String(Files.readAllBytes(outputFile.toPath()), Charsets.UTF_8).trim();
    System.out.println(actual);

    // @formatter:off
    String expected =
        "SET session_replication_role = replica;\n" + 
        "\n" + 
        "-- Inserting records into table test5_tbl\n" + 
        "\n" + 
        "INSERT INTO test5_tbl (\"id\", \"name\") VALUES\n" + 
        "      (1, 'Customer1') RETURNING id;\n" + 
        "\n" + 
        "-- Inserting records into table test6_tbl\n" + 
        "\n" + 
        "INSERT INTO test6_tbl (\"id\", \"test5_id\", \"name\") VALUES\n" + 
        "      (1, 1, 'Node1') RETURNING id;\n" + 
        "\n" + 
        "-- Inserting records into table test8_tbl\n" + 
        "\n" + 
        "INSERT INTO test8_tbl (\"id\", \"test5_id\", \"test6_id\", \"name\") VALUES\n" + 
        "      (1, 1, 1, 'Customer1 - Node1 Notification'),\n" + 
        "      (2, 1, NULL, 'Customer1 Notification') RETURNING id;\n" + 
        "\n" + 
        "-- Inserting records into table test9_tbl\n" + 
        "\n" + 
        "INSERT INTO test9_tbl (\"id\", \"test8_id\", \"name\") VALUES\n" + 
        "      (1, 1, 'Customer1 - Node1 Notification Settings'),\n" + 
        "      (2, 2, 'Customer1 Notification Settings') RETURNING id;\n" + 
        "\n" + 
        "-- Inserting records into table test7_tbl\n" + 
        "\n" + 
        "INSERT INTO test7_tbl (\"id\", \"test6_id\", \"name\") VALUES\n" + 
        "      (1, 1, 'NodeTag1') RETURNING id;";
    // @formatter:on

    assertThat(actual).isEqualTo(expected);


  }

  @Test
  public void test_rollback_app() throws Exception {
    File logFile = new File(tempDir, "dataset-cloner.log");

    Environment env = Environment.builder()
        .withSchemaGraph(schemaGraph)
        .withDataSource(dataSource)
        .withLogger(new Logger(logFile))
        .build();

    Test5Cloner cloner = Test5Cloner.builder(env)
        .withRecordId(1)
        .withBeforeAllListener(() -> System.out.println("Starting test5 clone operation..."))
        .withAfterAllListener((elapsed) -> System.out
            .println("Finished test5 clone operation in " + ElapsedTimeUtils.format(elapsed)))
        .build();

    try {
      cloner.execute();

      assertSuccessfulClone();

      // SETUP
      File confFile = createConfFile(false);
      String[] args = new String[] {confFile.getAbsolutePath()};

      // EXECUTE
      Test5RollbackApplication.main(args);

      // ASSERT
      assertSuccessfulRollback();
    } finally {
      cloner.rollback();
    }
  }

  @Test
  public void test_script_app() throws Exception {
    DaoUtils.executeSqlScript(dataSource, IntegrationTestSuite.class,
        "base-test-teardown.sql");

    generatorScriptFile("base-test-data-no-functions.sql");

    assertStateBeforeScript();

    // SETUP
    File confFile = createConfFile(false);
    String[] args = new String[] {confFile.getAbsolutePath()};

    // EXECUTE
    Test5ScriptApplication.main(args);

    // ASSERT
    assertStateAfterScript();
  }


  private void assertSuccessfulClone() {
    Records records;
    Map<String, String> record;


    // assert contents of test5_tbl
    records = select("test5_tbl");
    assertThat(records.size()).isEqualTo(3);

    record = assertPresent(records.findRecord(Key.of("id", 3)));
    assertRecord(record)
        .hasFieldValue("id", 3);
    assertThat(record.get("name")).matches("^Customer1_\\d+_1$");

  }

  private void assertNoClone() {
    Records records;


    // assert contents of test5_tbl
    records = select("test5_tbl");
    assertThat(records.size()).isEqualTo(2);

  }

  private void assertSuccessfulRollback() {
    Records records;

    // assert contents of test5_tbl
    records = select("test5_tbl");
    assertThat(records.size()).isEqualTo(2);

  }

  private File createConfFile(boolean pureCopyMode) throws IOException {
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
            "numCopies=1\n" +
            "debug=false\n" +
            "pureCopyMode=" + pureCopyMode + "\n" +
            "outputFile=" + outputFile.getAbsolutePath() + "\n" +
            "customProperty=1";

    try (PrintWriter out = new PrintWriter(new FileWriter(confFile))) {
      out.print(contents);
    }
    return confFile;
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
    assertThat(select("test11_tbl").size()).isEqualTo(0);
    assertThat(select("test12_tbl").size()).isEqualTo(0);
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
    assertThat(select("test11_tbl").size()).isEqualTo(4);
    assertThat(select("test12_tbl").size()).isEqualTo(4);
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


}
