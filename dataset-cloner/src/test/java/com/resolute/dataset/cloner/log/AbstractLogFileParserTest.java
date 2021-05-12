package com.resolute.dataset.cloner.log;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.resolute.database.crawler.model.Graph;
import com.resolute.dataset.cloner.integration.AbstractDatabaseTest;
import com.resolute.dataset.cloner.utils.Key;

public abstract class AbstractLogFileParserTest extends AbstractDatabaseTest {

  @TempDir
  File tempDir;

  @Test
  public void test_parse() {
    Graph subgraph = schemaGraph.getSubgraphReachableFrom("test1_tbl");
    File file = new File(tempDir, "dataset-cloner.log");

    generateLogFile(file);

    LogFileParser parser = LogFileParser.forGraph(subgraph);

    parser.parse(file, 5000, logFile -> {
      assertThat(logFile).isNotNull();
      assertThat(logFile.getTableNamePrefix()).isEqualTo(234567);
      Map<String, Set<Key>> keysInserted = logFile.getKeysInserted();
      assertThat(keysInserted)
          .isNotNull()
          .hasSize(4);


      Set<Key> keys;

      keys = keysInserted.get("test1_tbl");
      assertThat(keys)
          .hasSize(1)
          .contains(
              Key.of("id", 3));

      keys = keysInserted.get("test4_tbl");
      assertThat(keys)
          .hasSize(4)
          .contains(
              Key.of("id", 7),
              Key.of("id", 8),
              Key.of("id", 9),
              Key.of("id", 10));

      keys = keysInserted.get("test4_closure_tbl");
      assertThat(keys)
          .hasSize(9)
          .contains(
              Key.of("parent_id", 7, "child_id", 7),
              Key.of("parent_id", 7, "child_id", 9),
              Key.of("parent_id", 7, "child_id", 10),
              Key.of("parent_id", 9, "child_id", 9),
              Key.of("parent_id", 9, "child_id", 10),
              Key.of("parent_id", 10, "child_id", 10),
              Key.of("parent_id", 4, "child_id", 8),
              Key.of("parent_id", 5, "child_id", 8),
              Key.of("parent_id", 8, "child_id", 8));

      keys = keysInserted.get("test3_tbl");
      assertThat(keys)
          .hasSize(2)
          .contains(
              Key.of("test1_id", 3, "test2_id", 1),
              Key.of("test1_id", 3, "test2_id", 2));
    });



  }

  private void generateLogFile(File logFile) {
    try (PrintWriter out = new PrintWriter(new FileWriter(logFile))) {
      out.print(">>>>TABLE NAME PREFIX: 234567\n" +
          ">>>>INSERT INTO TABLE: test1_tbl\n" +
          "3\n" +
          ">>>>INSERT INTO TABLE: test4_tbl\n" +
          "7\n" +
          "8\n" +
          "9\n" +
          "10\n" +
          ">>>>INSERT INTO TABLE: test4_closure_tbl\n" +
          "7,7\n" +
          "7,9\n" +
          "7,10\n" +
          "9,9\n" +
          "9,10\n" +
          "10,10\n" +
          "4,8\n" +
          "5,8\n" +
          "8,8\n" +
          ">>>>INSERT INTO TABLE: test3_tbl\n" +
          "3,1\n" +
          "3,2\n" +
          ">>>>INSERT INTO TABLE: test10_tbl");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

  }

}
