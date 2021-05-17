package org.devoware.table;

import static java.util.stream.Collectors.groupingBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.devoware.testutils.Distribution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.testcontainers.shaded.com.google.common.collect.Sets;

public class TablesTest {

  @TempDir
  File tempDir;

  @Test
  public void test_load_from_classpath() throws IOException {
    Tables tables = Tables.loadFromClasspath("org.devoware.table");

    assertTables(tables);

  }

  @Test
  public void test_load_from_directory() throws IOException {
    copyFilesToTempDir();

    Tables tables = Tables.loadFromDirectory(tempDir);

    assertTables(tables);

  }

  @Test
  public void test_roll_on_simple_table() throws IOException {
    Tables tables = Tables.loadFromClasspath("org.devoware.table");

    Distribution distribution = new Distribution(() -> tables.roll("Table 1"));

    assertThat(distribution.getPercentage("Result 1-1")).isCloseTo(25, within(0.25));
    assertThat(distribution.getPercentage("Result 1-2")).isCloseTo(25, within(0.25));
    assertThat(distribution.getPercentage("Result 1-3")).isCloseTo(25, within(0.25));
    assertThat(distribution.getPercentage("Result 1-4")).isCloseTo(25, within(0.25));

  }

  @Test
  public void test_roll_on_table_w_roll_once_on_different_table() throws IOException {
    Tables tables = Tables.loadFromClasspath("org.devoware.table");

    Distribution distribution = new Distribution(() -> tables.roll("Table 3"));

    assertThat(distribution.getPercentage("Result 1-1")).isCloseTo(25, within(0.25));
    assertThat(distribution.getPercentage("Result 1-2")).isCloseTo(25, within(0.25));
    assertThat(distribution.getPercentage("Result 1-3")).isCloseTo(25, within(0.25));
    assertThat(distribution.getPercentage("Result 1-4")).isCloseTo(25, within(0.25));

  }

  @Test
  public void test_roll_on_table_w_roll_twice_on_different_table() throws IOException {
    Tables tables = Tables.loadFromClasspath("org.devoware.table");

    Distribution distribution = new Distribution(() -> tables.roll("Table 4"));

    assertThat(distribution.getPercentage("Result 2-1; Result 2-1")).isCloseTo(6.25, within(0.25));
    assertThat(distribution.getPercentage("Result 2-1; Result 2-2")).isCloseTo(6.25, within(0.25));
    assertThat(distribution.getPercentage("Result 2-1; Result 2-3")).isCloseTo(6.25, within(0.25));
    assertThat(distribution.getPercentage("Result 2-1; Result 2-4")).isCloseTo(6.25, within(0.25));

    assertThat(distribution.getPercentage("Result 2-2; Result 2-1")).isCloseTo(6.25, within(0.25));
    assertThat(distribution.getPercentage("Result 2-2; Result 2-2")).isCloseTo(6.25, within(0.25));
    assertThat(distribution.getPercentage("Result 2-2; Result 2-3")).isCloseTo(6.25, within(0.25));
    assertThat(distribution.getPercentage("Result 2-2; Result 2-4")).isCloseTo(6.25, within(0.25));

    assertThat(distribution.getPercentage("Result 2-3; Result 2-1")).isCloseTo(6.25, within(0.25));
    assertThat(distribution.getPercentage("Result 2-3; Result 2-2")).isCloseTo(6.25, within(0.25));
    assertThat(distribution.getPercentage("Result 2-3; Result 2-3")).isCloseTo(6.25, within(0.25));
    assertThat(distribution.getPercentage("Result 2-3; Result 2-4")).isCloseTo(6.25, within(0.25));

    assertThat(distribution.getPercentage("Result 2-4; Result 2-1")).isCloseTo(6.25, within(0.25));
    assertThat(distribution.getPercentage("Result 2-4; Result 2-2")).isCloseTo(6.25, within(0.25));
    assertThat(distribution.getPercentage("Result 2-4; Result 2-3")).isCloseTo(6.25, within(0.25));
    assertThat(distribution.getPercentage("Result 2-4; Result 2-4")).isCloseTo(6.25, within(0.25));

  }

  @Test
  public void test_roll_on_table_w_roll_twice_on_same_table() throws IOException {
    Tables tables = Tables.loadFromClasspath("org.devoware.table");

    Distribution distribution = new Distribution(() -> tables.roll("Table 5"));

    Set<String> resultSet = Sets.newTreeSet(distribution.getResultSet());
    Map<Integer, Long> resultsByNumberOfSemiColons = resultSet.stream()
        .collect(groupingBy(this::numSemicolons, Collectors.counting()));

    assertThat(resultsByNumberOfSemiColons.get(0)).isEqualTo(2);
    assertThat(resultsByNumberOfSemiColons.get(1)).isEqualTo(4);
    assertThat(resultsByNumberOfSemiColons.get(2)).isEqualTo(8);

    assertThat(distribution.getPercentage("Result 5-1")).isCloseTo(49, within(0.25));
    assertThat(distribution.getPercentage("Result 5-2")).isCloseTo(49, within(0.25));

    Set<String> visited = Sets.newHashSet("Result 5-1", "Result 5-2");
    double remainingPercentage = 0.0;
    for (String result : resultSet) {
      if (visited.contains(result)) {
        continue;
      }
      remainingPercentage += distribution.getPercentage(result);
    }
    assertThat(remainingPercentage).isCloseTo(2, within(0.25));
  }

  private int numSemicolons(String result) {
    return result.length() - result.replace(";", "").length();
  }

  private void copyFilesToTempDir() throws IOException {
    String[] tableFiles = {"table1.tbl", "table2.tbl", "table3.tbl", "table4.tbl", "table5.tbl"};
    for (String tableFile : tableFiles) {
      try (
          BufferedReader in = new BufferedReader(
              new InputStreamReader(TablesTest.class.getResourceAsStream(tableFile)));
          PrintWriter out = new PrintWriter(new FileWriter(new File(tempDir, tableFile)))) {
        for (String line = in.readLine(); line != null; line = in.readLine()) {
          out.println(line);
        }

      }
    }

  }

  private void assertTables(Tables tables) {
    Table table;
    assertThat(tables).isNotNull();
    assertThat(tables.size()).isEqualTo(5);

    table = tables.get("Table 1");
    assertThat(table).isNotNull();
    assertThat(table.size()).isEqualTo(4);
    assertThat(table.getLowerEndpoint()).isEqualTo(1);
    assertThat(table.getUpperEndpoint()).isEqualTo(4);
    assertThat(table.get(1)).isEqualTo("Result 1-1");
    assertThat(table.get(2)).isEqualTo("Result 1-2");
    assertThat(table.get(3)).isEqualTo("Result 1-3");
    assertThat(table.get(4)).isEqualTo("Result 1-4");

    table = tables.get("Table 2");
    assertThat(table).isNotNull();
    assertThat(table.size()).isEqualTo(4);
    assertThat(table.getLowerEndpoint()).isEqualTo(1);
    assertThat(table.getUpperEndpoint()).isEqualTo(4);
    assertThat(table.get(1)).isEqualTo("Result 2-1");
    assertThat(table.get(2)).isEqualTo("Result 2-2");
    assertThat(table.get(3)).isEqualTo("Result 2-3");
    assertThat(table.get(4)).isEqualTo("Result 2-4");

    table = tables.get("Table 3");
    assertThat(table).isNotNull();
    assertThat(table.size()).isEqualTo(1);
    assertThat(table.getLowerEndpoint()).isEqualTo(1);
    assertThat(table.getUpperEndpoint()).isEqualTo(1);
    assertThat(table.get(1)).isEqualTo("Roll 1 time on table 'Table 1'");

    table = tables.get("Table 4");
    assertThat(table).isNotNull();
    assertThat(table.size()).isEqualTo(1);
    assertThat(table.getLowerEndpoint()).isEqualTo(1);
    assertThat(table.getUpperEndpoint()).isEqualTo(1);
    assertThat(table.get(1)).isEqualTo("Roll 2 times on table 'Table 2'");

    table = tables.get("Table 5");
    assertThat(table).isNotNull();
    assertThat(table.size()).isEqualTo(3);
    assertThat(table.getLowerEndpoint()).isEqualTo(1);
    assertThat(table.getUpperEndpoint()).isEqualTo(100);
    assertThat(table.get(1)).isEqualTo("Result 5-1");
    assertThat(table.get(49)).isEqualTo("Result 5-1");
    assertThat(table.get(50)).isEqualTo("Result 5-2");
    assertThat(table.get(98)).isEqualTo("Result 5-2");
    assertThat(table.get(99)).isEqualTo("Roll 2 times on table 'Table 5'");
    assertThat(table.get(100)).isEqualTo("Roll 2 times on table 'Table 5'");

  }

}
