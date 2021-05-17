package org.devoware.table;

import static java.util.Objects.requireNonNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TableFileParser {

  private final InputStream in;

  public static Table parse(File tableFile) {
    requireNonNull(tableFile);
    try (InputStream in = new FileInputStream(tableFile)) {
      TableFileParser parser = new TableFileParser(in);
      return parser.parse();
    } catch (IOException e) {
      throw new TableFileParseException(e);
    }
  }

  public static Table parse(InputStream in) {
    TableFileParser parser = new TableFileParser(in);
    try {
      return parser.parse();
    } catch (IOException e) {
      throw new TableFileParseException(e);
    }

  }

  private TableFileParser(InputStream in) {
    this.in = requireNonNull(in, "input stream cannot be null");
  }

  private Table parse() throws IOException {
    Table.Builder tableBuilder = Table.builder();
    ParserState state = ParserState.getInitialState();
    int lineNumber = 0;
    try (BufferedReader buf = new BufferedReader(new InputStreamReader(in))) {
      for (String line = buf.readLine(); line != null; line = buf.readLine()) {
        if (line.trim().isEmpty()) {
          continue;
        }
        state = state.processLine(lineNumber++, line, tableBuilder);
      }
      return tableBuilder.build();
    }
  }


}
