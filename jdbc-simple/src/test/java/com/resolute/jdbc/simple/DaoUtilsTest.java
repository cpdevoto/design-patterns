package com.resolute.jdbc.simple;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.TimeZone;

import javax.sql.DataSource;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.resolute.jdbc.simple.fixtures.Foo;
import com.resolute.testutils.postgres.DataSourceBuilder;


@Testcontainers
public class DaoUtilsTest {

  @Container
  private static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:9.6.12");

  @Test
  public void testTimestampToLong() throws SQLException {
    long epochMillis = 1517586567129L;
    Timestamp ts = Timestamp.from(Instant.ofEpochMilli(epochMillis));
    ResultSet rs = mock(ResultSet.class);
    when(rs.getTimestamp(eq("create_date"), any())).thenReturn(ts);
    assertThat(DaoUtils.timestampToLong(rs, "create_date")).isEqualTo(epochMillis);
  }

  @Test
  public void testTimestampToLocalDateTime() throws SQLException {
    long epochMillis = 1517586567129L;
    Timestamp ts = Timestamp.from(Instant.ofEpochMilli(epochMillis));
    ResultSet rs = mock(ResultSet.class);
    when(rs.getTimestamp(eq("create_date"), any())).thenReturn(ts);

    assertThat(
        DaoUtils.timestampToLocalDate(rs, "create_date"))
            .isEqualTo(LocalDateTime.parse("2018-02-02T15:49:27.129"));

    assertThat(
        DaoUtils.timestampToLocalDate(rs, "create_date", TimeZone.getTimeZone("America/New_York")))
            .isEqualTo(LocalDateTime.parse("2018-02-02T10:49:27.129"));
  }


  @Test
  public void testExecuteSqlScript() throws IOException, SQLException {
    executeDatabaseFunction(dataSource -> {
      try {
        DaoUtils.executeSqlScript(dataSource, "com/resolute/jdbc/simple/daoutils-test-data.sql");

        List<Foo> foos = retrieveFoos(dataSource);

        assertFoos(foos, createFoo(1, "Foo1"));
      } finally {
        executeSql(dataSource, "DROP TABLE IF EXISTS foo_tbl");
      }
    });
  }

  @Test
  public void testExecuteSqlScriptWithFileLocatorClass() throws IOException, SQLException {
    executeDatabaseFunction(dataSource -> {
      try {
        DaoUtils.executeSqlScript(dataSource, DaoUtilsTest.class, "daoutils-test-data.sql");

        List<Foo> foos = retrieveFoos(dataSource);

        assertFoos(foos, createFoo(1, "Foo1"));
      } finally {
        executeSql(dataSource, "DROP TABLE IF EXISTS foo_tbl");
      }
    });
  }


  // ------------------------
  // Helper Methods
  // ------------------------

  private void executeDatabaseFunction(DatabaseFunction function)
      throws IOException, SQLException {
    DataSource dataSource = createDataSource();
    function.execute(dataSource);
  }

  private DataSource createDataSource() {
    DataSource dataSource = DataSourceBuilder.newInstance()
        .withHost(postgres.getHost())
        .withPort(postgres.getFirstMappedPort())
        .withDatabase(postgres.getDatabaseName())
        .withUsername(postgres.getUsername())
        .withPassword(postgres.getPassword())
        .build();
    return dataSource;
  }

  private List<Foo> retrieveFoos(DataSource dataSource) throws SQLException {
    List<Foo> foos = Lists.newArrayList();
    executeSqlQuery(dataSource, "SELECT id, name FROM foo_tbl", rs -> {
      Foo foo = Foo.builder()
          .withId(rs.getInt("id"))
          .withName(rs.getString("name"))
          .build();
      foos.add(foo);
    });
    return foos;
  }

  private void executeSql(DataSource dataSource, String sql)
      throws SQLException {
    try (Connection conn = dataSource.getConnection()) {
      try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.execute();
      }
    }
  }

  private void executeSqlQuery(DataSource dataSource, String sql, RowProcessor rowProcessor)
      throws SQLException {
    try (Connection conn = dataSource.getConnection()) {
      try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        try (ResultSet rs = stmt.executeQuery()) {
          while (rs.next()) {
            rowProcessor.process(rs);
          }
        }
      }
    }
  }

  private void assertFoos(List<Foo> actualFoos, Foo... expectedFoos) {
    assertThat(actualFoos).isNotNull();
    assertThat(actualFoos.size()).isEqualTo(1);
    assertThat(actualFoos).containsExactly(expectedFoos);
  }

  private Foo createFoo(int id, String name) {
    return Foo.builder()
        .withId(id)
        .withName(name)
        .build();
  }

  @FunctionalInterface
  private interface RowProcessor {
    public void process(ResultSet rs) throws SQLException;
  }


  @FunctionalInterface
  private interface DatabaseFunction {
    public void execute(DataSource dataSource) throws IOException, SQLException;
  }


}
