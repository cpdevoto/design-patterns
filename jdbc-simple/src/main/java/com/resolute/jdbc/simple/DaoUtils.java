package com.resolute.jdbc.simple;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.TimeZone;

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


  private DaoUtils() {}

}
