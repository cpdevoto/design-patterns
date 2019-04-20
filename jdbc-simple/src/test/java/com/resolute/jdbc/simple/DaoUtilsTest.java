package com.resolute.jdbc.simple;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;

import org.junit.Test;

import static org.junit.Assert.assertThat;

import static org.hamcrest.CoreMatchers.equalTo;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class DaoUtilsTest {

  @Test
  public void testTimestampToLong() throws SQLException {
    long epochMillis = 1517586567129L;
    Timestamp ts = Timestamp.from(Instant.ofEpochMilli(epochMillis));
    ResultSet rs = mock(ResultSet.class);
    when(rs.getTimestamp(eq("create_date"), any())).thenReturn(ts);
    assertThat(DaoUtils.timestampToLong(rs, "create_date"), equalTo(epochMillis));
  }

  @Test
  public void testTimestampToLocalDateTime() throws SQLException {
    long epochMillis = 1517586567129L;
    Timestamp ts = Timestamp.from(Instant.ofEpochMilli(epochMillis));
    ResultSet rs = mock(ResultSet.class);
    when(rs.getTimestamp(eq("create_date"), any())).thenReturn(ts);

    assertThat(
        DaoUtils.timestampToLocalDate(rs, "create_date"),
        equalTo(LocalDateTime.parse("2018-02-02T15:49:27.129")));

    assertThat(
        DaoUtils.timestampToLocalDate(rs, "create_date", TimeZone.getTimeZone("America/New_York")),
        equalTo(LocalDateTime.parse("2018-02-02T10:49:27.129")));
  }


}
