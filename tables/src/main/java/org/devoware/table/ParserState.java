package org.devoware.table;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.devoware.table.Table.Builder;

import com.google.common.collect.Range;


enum ParserState {
  GET_TABLE_NAME() {

    @Override
    ParserState processLine(int lineNumber, String line, Builder builder) {
      String name = line.trim();
      builder.withName(name);
      return GET_TABLE_ENTRY;
    }

  },
  GET_TABLE_ENTRY() {

    @Override
    ParserState processLine(int lineNumber, String line, Builder builder) {
      Range<Integer> range;
      String value;
      int firstComma = line.indexOf(',');
      if (firstComma == -1) {
        range = Range.singleton(lineNumber);
        value = line;
      } else {
        Optional<Range<Integer>> optRange = parseRange(line, line.substring(0, firstComma));
        if (optRange.isPresent()) {
          range = optRange.get();
          int valueStart = firstComma + 1;
          if (valueStart < line.length()) {
            value = line.substring(valueStart);
          } else {
            value = "";
          }
        } else {
          // The substring before the first is not an integer, so treat the whole line as a value;
          range = Range.singleton(lineNumber);
          value = line;
        }
      }
      builder.withEntry(range.lowerEndpoint(), range.upperEndpoint(), value);
      return GET_TABLE_ENTRY;
    }

    private Optional<Range<Integer>> parseRange(String line, String rangeString) {
      Matcher m = RANGE.matcher(rangeString.trim());
      if (!m.find()) {
        Matcher m2 = RANGE2.matcher(rangeString.trim());
        if (!m2.find()) {
          return Optional.empty();
        }
        int value = rangeString.equals("00") ? 100 : Integer.parseInt(m2.group(1));
        return Optional.of(Range.singleton(value));
      }
      int lower = Integer.parseInt(m.group(1));
      int upper = m.group(2).equals("00") ? 100 : Integer.parseInt(m.group(2));
      return Optional.of(Range.closed(lower, upper));
    }

  };

  private static final String INVALID_ENTRY_MESSAGE =
      "Invalid format for a table entry; expected: <lower>-<upper>,<value> or <int>,<value> but found %s";

  private static Pattern RANGE = Pattern.compile("^0*(\\d+)[–-](0*[^0]\\d*|00)$");
  private static Pattern RANGE2 = Pattern.compile("^0*(\\d+)$");

  static ParserState getInitialState() {
    return GET_TABLE_NAME;
  }

  abstract ParserState processLine(int lineNumber, String line, Table.Builder builder);

}
