package com.resolute.dataset.cloner.app.integration;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.stream.Collectors.joining;
import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.shaded.com.google.common.collect.Lists;

import com.google.common.collect.Maps;
import com.resolute.database.crawler.model.Graph;
import com.resolute.dataset.cloner.app.testutils.InitTableSize;
import com.resolute.dataset.cloner.app.testutils.Records;
import com.resolute.dataset.cloner.utils.TempTables;
import com.resolute.jdbc.simple.DataAccessException;
import com.resolute.jdbc.simple.JdbcStatementFactory;
import com.resolute.jdbc.simple.QueryHandler;

public abstract class AbstractDatabaseTest {

  public static DataSource dataSource;
  public static JdbcStatementFactory statementFactory;
  public static Graph schemaGraph;


  @BeforeAll
  public static void setupSqlTest() {
    dataSource = IntegrationTestSuite.getDataSource();
    statementFactory = JdbcStatementFactory.getInstance(dataSource);
    schemaGraph = IntegrationTestSuite.getSchemaGraph();
  }

  public String getTempTableName(int tableNamePrefix, String tableName) {
    return TempTables.getTempTableName(tableNamePrefix, tableName);
  }


  public String getTempUnaryTableName(int tableNamePrefix, String tableName) {
    return TempTables.getTempUnaryTableName(tableNamePrefix, tableName);
  }

  public Records select(String table) {
    try {
      try (Connection conn = dataSource.getConnection()) {
        try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM " + table)) {
          try (ResultSet rs = stmt.executeQuery()) {
            List<Map<String, String>> records = Lists.newArrayList();
            while (rs.next()) {
              Map<String, String> record = Maps.newLinkedHashMap();
              for (int i = 1; i < rs.getMetaData().getColumnCount() + 1; i++) {
                String key = rs.getMetaData().getColumnName(i);
                String value = String.valueOf(rs.getObject(i));
                record.put(key, value);
              }
              records.add(record);
            }
            return new Records(records);
          }
        }
      }
    } catch (SQLException e) {
      throw new DataAccessException(e);
    }

  }

  public Records select(String table, String... fields) {
    checkArgument(fields.length > 0, "Expected at least one field");

    String fieldList = Arrays.stream(fields)
        .collect(joining(", "));

    StringBuilder sql = new StringBuilder("SELECT ")
        .append(fieldList)
        .append(" FROM ").append(table)
        .append(" ORDER BY ")
        .append(fieldList);

    return new Records(statementFactory.newStatement()
        .withSql(sql.toString())
        .withErrorMessage(
            "A problem occurred while attempting to retrieve the contents of " + table)
        .executeQuery(result -> result.toList((idx, rs) -> {
          Map<String, String> record = Maps.newLinkedHashMap();
          for (String field : fields) {
            record.put(field, rs.getString(field));
          }
          return record;
        })));
  }

  public boolean tableExists(String tableName) {

    final String sql =
        "SELECT table_name FROM information_schema.tables WHERE table_schema = 'public' AND table_type = 'BASE TABLE' AND table_name = ?";

    String result = statementFactory.newStatement()
        .withSql(sql)
        .withErrorMessage(
            "A problem occurred while attempting to check if table " + tableName + " exists")
        .prepareStatement(stmt -> stmt.setString(1, tableName))
        .executeQuery(QueryHandler.toObject(rs -> rs.getString("table_name")));

    if (result == null) {
      return false;
    }
    return true;
  }

  public Map<String, String> assertPresent(Optional<Map<String, String>> optRecord) {
    assertThat(optRecord)
        .isNotNull()
        .isPresent();
    return optRecord.get();
  }

  public void assertAdditionalRecs(String table, Records records, int additionalRecs) {
    int initialSize = InitTableSize.get(table);
    assertAdditionalRecs(initialSize, records, additionalRecs);
  }

  public void assertAdditionalRecs(int initialSize, Records records, int additionalRecs) {
    assertThat(records.size()).isEqualTo(initialSize + additionalRecs);
  }

}
