package com.resolute.jdbc.simple;

import static java.util.Objects.requireNonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.stream.Collectors;

import javax.sql.DataSource;

public class DaoUtils {
  public static ThreadLocal<Calendar> UTC_CAL = new ThreadLocal<Calendar>() {
    @Override
    protected Calendar initialValue() {
      return Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    }
  };


  public static Long timestampToLong(ResultSet rs, String columnId) throws SQLException {
    Long result = null;
    Timestamp timestamp =
        rs.getTimestamp(columnId, UTC_CAL.get());
    if (timestamp != null) {
      result = timestamp.getTime();
    }
    return result;
  }

  @Deprecated
  public static Long timestampToLong(ResultSet rs, String columnId, TimeZone timezone)
      throws SQLException {
    // The timezone parameter is completely irrelevant when converting a timestamp to epoch millis!
    return timestampToLong(rs, columnId);
  }

  public static LocalDateTime timestampToLocalDate(ResultSet rs, String column)
      throws SQLException {
    return timestampToLocalDate(rs, column, TimeZone.getTimeZone("UTC"));
  }

  public static LocalDateTime timestampToLocalDate(ResultSet rs, String column, TimeZone timezone)
      throws SQLException {
    LocalDateTime result = null;
    Timestamp timestamp =
        rs.getTimestamp(column, UTC_CAL.get());
    if (timestamp != null) {
      result = Instant.ofEpochMilli(timestamp.getTime()).atZone(ZoneId.of(timezone.getID()))
          .toLocalDateTime();
    }
    return result;
  }

  public static Timestamp localDateToTimestamp(TimeZone timezone, LocalDateTime localDate) {
    if (timezone == null) {
      timezone = TimeZone.getDefault();
    }
    if (localDate == null) {
      return null;
    }
    Instant instant = localDate.atZone(timezone.toZoneId()).toInstant();
    return Timestamp.from(instant);
  }


  public static Integer getInt(ResultSet rs, String columnId) throws SQLException {
    Integer result = rs.getInt(columnId);
    if (rs.wasNull()) {
      result = null;
    }
    return result;
  }

  public static Long getLong(ResultSet rs, String columnId) throws SQLException {
    Long result = rs.getLong(columnId);
    if (rs.wasNull()) {
      result = null;
    }
    return result;
  }

  public static Double getDouble(ResultSet rs, String columnId) throws SQLException {
    Double result = rs.getDouble(columnId);
    if (rs.wasNull()) {
      result = null;
    }
    return result;
  }

  public static Boolean getBoolean(ResultSet rs, String columnId) throws SQLException {
    Boolean result = rs.getBoolean(columnId);
    if (rs.wasNull()) {
      result = null;
    }
    return result;
  }

  public static void executeSqlScript(DataSource dataSource, String fileName)
      throws IOException, SQLException {
    requireNonNull(dataSource, "dataSource cannot be null");
    requireNonNull(fileName, "fileName cannot be null");
    execSqlScript(dataSource, null, fileName);
  }

  public static void executeSqlScript(DataSource dataSource, Class<?> fileLocatorClass,
      String fileName) throws IOException, SQLException {
    requireNonNull(dataSource, "dataSource cannot be null");
    requireNonNull(fileLocatorClass, "fileLocatorClass cannot be null");
    requireNonNull(fileName, "fileName cannot be null");
    execSqlScript(dataSource, fileLocatorClass, fileName);
  }

  private static void execSqlScript(DataSource dataSource, Class<?> fileLocatorClass,
      String fileName) throws IOException, SQLException {
    try (Connection connection = dataSource.getConnection();
        java.sql.Statement statement = connection.createStatement();) {

      String sqlfileContent = null;
      if (fileLocatorClass != null) {
        try (InputStream inputStream = fileLocatorClass.getResourceAsStream(fileName)) {
          sqlfileContent = new BufferedReader(new InputStreamReader(inputStream)).lines()
              .collect(Collectors.joining("\n"));
        }
      } else {
        try (InputStream inputStream =
            DaoUtils.class.getClassLoader().getResourceAsStream(fileName)) {
          sqlfileContent = new BufferedReader(new InputStreamReader(inputStream)).lines()
              .collect(Collectors.joining("\n"));
        }
      }

      if (sqlfileContent != null) {
        statement.execute(sqlfileContent);
      }

    }

  }

  private DaoUtils() {}

}
