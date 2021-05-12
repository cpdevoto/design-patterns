package com.resolute.dataset.cloner.script;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.resolute.dataset.cloner.Environment;
import com.resolute.dataset.cloner.engine.script.ScriptOperation;
import com.resolute.dataset.cloner.integration.AbstractDatabaseTest;
import com.resolute.dataset.cloner.integration.IntegrationTestSuite;
import com.resolute.dataset.cloner.log.Logger;
import com.resolute.jdbc.simple.DaoUtils;

public abstract class AbstractScriptOperationTest extends AbstractDatabaseTest {

  @TempDir
  File tempDir;



  @Test
  public void test_execute() throws IOException, SQLException {

    File sqlScript = generatorScriptFile("base-test-data-no-functions.sql");

    Environment env = Environment.builder()
        .withDataSource(dataSource)
        .withSchemaGraph(schemaGraph)
        .withDebug(false)
        .withLogger(new Logger(new File(tempDir, "dataset-cloner.log")))
        .withOutputFile(new Logger(sqlScript))
        .withNumCopies(1)
        .withProperties(new Properties())
        .build();

    assertStateBeforeScript();

    try {
      ScriptOperation.execute(env);

      assertStateAfterScript();
    } finally {
      DaoUtils.executeSqlScript(dataSource, IntegrationTestSuite.class,
          "base-test-teardown.sql");
    }
  }

  @Test
  public void test_execute_with_low_execution_threshold() throws IOException, SQLException {

    File sqlScript = generatorScriptFile("base-test-data-no-functions.sql");

    assertStateBeforeScript();

    try {
      ScriptOperation op = ScriptOperation.builder()
          .withDataSource(dataSource)
          .withDebug(true)
          .withScriptFile(sqlScript)
          .withExecutionThreshold(1)
          .build();

      op.execute();

      assertStateAfterScript();
    } finally {
      DaoUtils.executeSqlScript(dataSource, IntegrationTestSuite.class,
          "base-test-teardown.sql");
    }
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
