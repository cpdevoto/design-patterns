package com.resolute.dataset.cloner.log;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.resolute.dataset.cloner.log.LogFile.Builder;
import com.resolute.dataset.cloner.utils.Key;

enum ParserState {
  GET_TABLE_NAME_PREFIX() {

    @Override
    ParserState processLine(String line, Builder builder) {
      Matcher m = TABLE_NAME_PREFIX.matcher(line);
      if (!m.matches()) {
        throw new LogFileParseException("Expected a table name prefix");
      }
      String prefix = m.group(1).trim();
      try {
        int tableNamePrefix = Integer.parseInt(prefix);
        builder.putTableNamePrefix(tableNamePrefix);
      } catch (NumberFormatException e) {
        throw new LogFileParseException(String.format("Invalid table name prefix: %s", prefix));
      }
      return GET_CURRENT_TABLE;
    }

  },
  GET_CURRENT_TABLE() {

    @Override
    ParserState processLine(String line, Builder builder) {
      Matcher m = INSERT_INTO_TABLE.matcher(line);
      if (!m.matches()) {
        throw new LogFileParseException("Expected a table header containing a table name");
      }
      String currentTable = m.group(1).trim();
      builder.setCurrentTable(currentTable);
      return GET_KEY;
    }

  },
  GET_KEY() {

    @Override
    ParserState processLine(String line, Builder builder) {
      Matcher m = INSERT_INTO_TABLE.matcher(line);
      if (m.matches()) {
        return GET_CURRENT_TABLE.processLine(line, builder);
      }
      String[] values = line.split(",");
      Optional<List<String>> optKeyFields = builder.getPrimaryKey();
      if (!optKeyFields.isPresent()) {
        throw new LogFileParseException(
            String.format("Unrecognized table name: %s", builder.getCurrentTable()));
      }
      List<String> keyFields = optKeyFields.get();
      if (values.length != keyFields.size()) {
        throw new LogFileParseException(
            String.format("Expected key '%s' to have %d values", line, keyFields.size()));
      }
      Key.Builder keyBuilder = Key.builder();
      for (int i = 0; i < keyFields.size(); i++) {
        String fieldName = keyFields.get(i);
        String value = values[i];
        keyBuilder.withFieldValue(fieldName, value);
      }
      Key key = keyBuilder.build();
      builder.putKey(key);
      return this;
    }

  };

  private static Pattern TABLE_NAME_PREFIX = Pattern.compile("^>>>>TABLE NAME PREFIX: (.+)$");
  private static Pattern INSERT_INTO_TABLE = Pattern.compile("^>>>>INSERT INTO TABLE: (.+)$");

  public static ParserState getInitialState() {
    return GET_TABLE_NAME_PREFIX;
  }

  abstract ParserState processLine(String line, LogFile.Builder builder);
}
