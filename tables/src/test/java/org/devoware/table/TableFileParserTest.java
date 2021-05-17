package org.devoware.table;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class TableFileParserTest {

  @TempDir
  File tempDir;

  @Test
  public void test_parse_w_no_number_ranges() throws IOException {
    String fileName = "sample1.tbl";
    String contents =
        "Sample Table\n"
            + "Result 1\n"
            + "Result 2\n"
            + "Result 3\n"
            + "Result 4";
    writeFile(fileName, contents);

    Table table = TableFileParser.parse(new File(tempDir, fileName));
    assertThat(table).isNotNull();
    assertThat(table.getName()).isEqualTo("Sample Table");
    assertThat(table.size()).isEqualTo(4);
    assertThat(table.getLowerEndpoint()).isEqualTo(1);
    assertThat(table.getUpperEndpoint()).isEqualTo(4);
    assertThat(table.get(1)).isEqualTo("Result 1");
    assertThat(table.get(2)).isEqualTo("Result 2");
    assertThat(table.get(3)).isEqualTo("Result 3");
    assertThat(table.get(4)).isEqualTo("Result 4");
  }

  @Test
  public void test_parse_w_single_number_ranges() throws IOException {
    String fileName = "sample1.tbl";
    String contents =
        "Sample Table\n"
            + "1,Result 1\n"
            + "2,Result 2\n"
            + "3,Result 3\n"
            + "4,Result 4";
    writeFile(fileName, contents);

    Table table = TableFileParser.parse(new File(tempDir, fileName));
    assertThat(table).isNotNull();
    assertThat(table.getName()).isEqualTo("Sample Table");
    assertThat(table.size()).isEqualTo(4);
    assertThat(table.getLowerEndpoint()).isEqualTo(1);
    assertThat(table.getUpperEndpoint()).isEqualTo(4);
    assertThat(table.get(1)).isEqualTo("Result 1");
    assertThat(table.get(2)).isEqualTo("Result 2");
    assertThat(table.get(3)).isEqualTo("Result 3");
    assertThat(table.get(4)).isEqualTo("Result 4");
  }

  @Test
  public void test_parse_w_two_number_ranges() throws IOException {
    String fileName = "sample1.tbl";
    String contents =
        "Sample Table\n"
            + "01-02,Result 1\n"
            + "03-04,Result 2\n"
            + "05-06,Result 3\n"
            + "07-00,Result 4";
    writeFile(fileName, contents);

    Table table = TableFileParser.parse(new File(tempDir, fileName));
    assertThat(table).isNotNull();
    assertThat(table.getName()).isEqualTo("Sample Table");
    assertThat(table.size()).isEqualTo(4);
    assertThat(table.getLowerEndpoint()).isEqualTo(1);
    assertThat(table.getUpperEndpoint()).isEqualTo(100);
    assertThat(table.get(1)).isEqualTo("Result 1");
    assertThat(table.get(2)).isEqualTo("Result 1");
    assertThat(table.get(3)).isEqualTo("Result 2");
    assertThat(table.get(4)).isEqualTo("Result 2");
    assertThat(table.get(5)).isEqualTo("Result 3");
    assertThat(table.get(6)).isEqualTo("Result 3");
    for (int i = 7; i <= 100; i++) {
      assertThat(table.get(i)).as(String.format("Expected table.get(i) to equal 'Result 4'", i))
          .isEqualTo("Result 4");
    }
  }

  @Test
  public void test_parse_w_no_int_value_before_first_comma() throws IOException {
    String fileName = "sample1.tbl";
    String contents =
        "Sample Table\n"
            + "Result 1\n"
            + "Test,Result 2\n"
            + "Result 3\n"
            + "Result 4";
    writeFile(fileName, contents);

    Table table = TableFileParser.parse(new File(tempDir, fileName));
    assertThat(table).isNotNull();
    assertThat(table.getName()).isEqualTo("Sample Table");
    assertThat(table.size()).isEqualTo(4);
    assertThat(table.getLowerEndpoint()).isEqualTo(1);
    assertThat(table.getUpperEndpoint()).isEqualTo(4);
    assertThat(table.get(1)).isEqualTo("Result 1");
    assertThat(table.get(2)).isEqualTo("Test,Result 2");
    assertThat(table.get(3)).isEqualTo("Result 3");
    assertThat(table.get(4)).isEqualTo("Result 4");
  }

  @Test
  public void test_parse_w_multiple_commas() throws IOException {
    String fileName = "sample1.tbl";
    String contents =
        "Sample Table\n"
            + "1,Result 1,Next,Next,Next\n"
            + "2,Result 2\n"
            + "3,Result 3\n"
            + "4,Result 4";
    writeFile(fileName, contents);

    Table table = TableFileParser.parse(new File(tempDir, fileName));
    assertThat(table).isNotNull();
    assertThat(table.getName()).isEqualTo("Sample Table");
    assertThat(table.size()).isEqualTo(4);
    assertThat(table.getLowerEndpoint()).isEqualTo(1);
    assertThat(table.getUpperEndpoint()).isEqualTo(4);
    assertThat(table.get(1)).isEqualTo("Result 1,Next,Next,Next");
    assertThat(table.get(2)).isEqualTo("Result 2");
    assertThat(table.get(3)).isEqualTo("Result 3");
    assertThat(table.get(4)).isEqualTo("Result 4");
  }

  @Test
  public void test_parse_w_range_and_empty_value() throws IOException {
    String fileName = "sample1.tbl";
    String contents =
        "Sample Table\n"
            + "1,\n"
            + "Result 2\n"
            + "Result 3\n"
            + "Result 4";
    writeFile(fileName, contents);

    Table table = TableFileParser.parse(new File(tempDir, fileName));
    assertThat(table).isNotNull();
    assertThat(table.getName()).isEqualTo("Sample Table");
    assertThat(table.size()).isEqualTo(4);
    assertThat(table.getLowerEndpoint()).isEqualTo(1);
    assertThat(table.getUpperEndpoint()).isEqualTo(4);
    assertThat(table.get(1)).isEqualTo("");
    assertThat(table.get(2)).isEqualTo("Result 2");
    assertThat(table.get(3)).isEqualTo("Result 3");
    assertThat(table.get(4)).isEqualTo("Result 4");
  }

  @Test
  public void test_parse_w_one_entry() throws IOException {
    String fileName = "sample1.tbl";
    String contents =
        "Sample Table\n"
            + "Result 1";
    writeFile(fileName, contents);

    Table table = TableFileParser.parse(new File(tempDir, fileName));
    assertThat(table).isNotNull();
    assertThat(table.getName()).isEqualTo("Sample Table");
    assertThat(table.size()).isEqualTo(1);
    assertThat(table.getLowerEndpoint()).isEqualTo(1);
    assertThat(table.getUpperEndpoint()).isEqualTo(1);
    assertThat(table.get(1)).isEqualTo("Result 1");
  }

  @Test
  public void test_parse_w_zero_entries() throws IOException {
    String fileName = "sample1.tbl";
    String contents =
        "Sample Table";
    writeFile(fileName, contents);

    assertThatThrownBy(() -> {
      TableFileParser.parse(new File(tempDir, fileName));
    })
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("expected at least one entry");
  }

  @Test
  public void test_parse_w_empty_file() throws IOException {
    String fileName = "sample1.tbl";
    String contents =
        " ";
    writeFile(fileName, contents);

    assertThatThrownBy(() -> {
      TableFileParser.parse(new File(tempDir, fileName));
    })
        .isInstanceOf(NullPointerException.class)
        .hasMessage("name cannot be null");
  }

  private void writeFile(String fileName, String contents) throws IOException {
    try (PrintWriter out = new PrintWriter(new File(tempDir, fileName))) {
      out.println(contents);
    }

  }

}
