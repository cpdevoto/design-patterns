package org.devoware.table;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.devoware.table.Table.Builder;

import com.google.common.collect.Range;


enum ParserState {
  GET_TABLE_NAME() {

    @Override
    ParserState processLine(String line, Builder builder) {
      String name = line.trim();
      builder.withName(name);
      return GET_TABLE_ENTRY;
    }

  },
  GET_TABLE_ENTRY() {

    @Override
    ParserState processLine(String line, Builder builder) {
      String[] fields = line.split(",");
      if (fields.length != 2) {
        throw new TableFileParseException(INVALID_ENTRY_MESSAGE, line);
      }
      Range<Integer> range = parseRange(line, fields[0]);
      builder.withEntry(range.lowerEndpoint(), range.upperEndpoint(), fields[1]);
      return GET_TABLE_ENTRY;
    }

    private Range<Integer> parseRange(String line, String rangeString) {
      Matcher m = RANGE.matcher(rangeString.trim());
      if (!m.find()) {
        throw new TableFileParseException(INVALID_ENTRY_MESSAGE, line);
      }
      int lower = Integer.parseInt(m.group(1));
      int upper = m.group(2).equals("00") ? 100 : Integer.parseInt(m.group(2));
      return Range.closed(lower, upper);
    }

  };

  private static final String INVALID_ENTRY_MESSAGE =
      "Invalid format for a table entry; expected: <lower>-<upper>,<value> but found %s";

  private static Pattern RANGE = Pattern.compile("^0*(\\d+)–(0*[^0]\\d*|00)$");

  static ParserState getInitialState() {
    return GET_TABLE_NAME;
  }

  abstract ParserState processLine(String line, Table.Builder builder);

}
