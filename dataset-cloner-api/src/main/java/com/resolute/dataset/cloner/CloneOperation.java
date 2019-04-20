package com.resolute.dataset.cloner;

import static java.util.Objects.requireNonNull;

import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

import javax.sql.RowSetMetaData;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.resolute.jdbc.simple.DataAccessException;
import com.resolute.jdbc.simple.JdbcStatementFactory;
import com.resolute.utils.simple.ArrayUtils;

public class CloneOperation {
  private static Logger log = LoggerFactory.getLogger(CloneOperation.class);

  private final JdbcStatementFactory statementFactory;
  private final String table;
  private final Filter filter;
  private final String selectSql;

  private final Set<String> skippedFields;
  private final KeyMap targetKeyMap;
  private final Map<String, List<KeyMap>> foreignKeys;
  private final Map<String, FieldMutator> mutators;
  private final String pkColumnName;
  private final String insertSql;

  private final RowSetFactory rowSetFactory;


  static Builder builder(JdbcStatementFactory statementFactory, KeyMaps keyMaps, String table) {
    return new Builder(statementFactory, keyMaps, table);
  }

  private CloneOperation(Builder builder) {
    this.statementFactory = builder.statementFactory;
    this.table = builder.table;

    this.filter = builder.selectSpec.getFilter().orElse(null);
    this.selectSql = builder.selectSpec.getSql().orElse(null);

    this.skippedFields = builder.insertSpec.getSkippedFields().orElse(ImmutableSet.of());
    this.pkColumnName = builder.insertSpec.getPkColumnName().orElse(null);
    this.targetKeyMap =
        builder.insertSpec.getPkColumnName().map(pk -> builder.keyMaps.get(table)).orElse(null);
    this.mutators = builder.insertSpec.getMutators().orElse(ImmutableMap.of());
    Map<String, List<KeyMap>> foreignKeys = Maps.newLinkedHashMap();
    foreignKeys.putAll(builder.insertSpec.getForeignKeys().orElse(ImmutableMap.of()));
    if (filter != null) {
      filter.getForeignKeyFilters().stream()
          .forEach(fk -> {
            List<KeyMap> vms = foreignKeys.get(fk.getName());
            if (vms == null) {
              vms = Lists.newArrayList();
              foreignKeys.put(fk.getName(), vms);
            }
            vms.addAll(fk.getKeyMaps());
          });
    }
    this.foreignKeys = ImmutableMap.copyOf(foreignKeys);
    this.insertSql = builder.insertSpec.getSql().orElse(null);

    try {
      this.rowSetFactory = RowSetProvider.newFactory();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }

  }

  private int execute() {
    CachedRowSet rowSet = retrieveRows();
    int rowCount = rowSet == null ? null : rowSet.size();
    if (rowCount > 0) {
      try {
        writeRows(rowSet);
      } catch (SQLException e) {
        throw new DataAccessException(e);
      }
    }
    return rowCount;
  }

  private CachedRowSet retrieveRows() {
    final String sql =
        (selectSql != null ? selectSql : "SELECT * FROM " + table)
            + (filter != null ? " WHERE " + filter.toSql() : "");
    return statementFactory.newStatement()
        .withSql(sql)
        .withErrorMessage("A problem occurred while attempting to retrieve rows from " + table)
        .executeQuery(result -> {
          CachedRowSet rowSet = rowSetFactory.createCachedRowSet();
          rowSet.populate(result.getResultSet());
          return rowSet;
        });
  }

  private void writeRows(CachedRowSet rowSet) throws SQLException {

    RowSetMetaData rsmd = (RowSetMetaData) rowSet.getMetaData();
    int columnCount = rsmd.getColumnCount();

    // Generate the SQL
    String sql = getInsertSql(rsmd, columnCount);

    // Create the column name map
    Map<String, Integer> colNames = Maps.newLinkedHashMap();
    for (int i = 1; i <= columnCount; i++) {
      String columnName = rsmd.getColumnName(i);

      if (skippedFields.contains(columnName)) {
        continue;
      }
      colNames.put(columnName, i);
    }

    // Insert the rows

    int missingMappings;
    int attempts = 0;
    Set<Integer> successfulRows = Sets.newHashSet();
    // @formatter:off
    // This outer do loop is for cases where there is a unary foreign key relationship between the view
    // and itself, in which case foreign mappings may not yet be populated at the time they are needed.
    // We keep trying until we get no missing mapping exceptions, or until we've gone through the row set 10 times.
    // @formatter:on
    do {
      attempts++;
      missingMappings = 0;
      int rowIndex = -1;
      rowSet.first();
      // Loop throw result set and attempt an insert for each row
      do {
        rowIndex++;
        if (successfulRows.contains(rowIndex)) {
          continue;
        }
        try {
          if (targetKeyMap != null) {
            int id = statementFactory.newStatement()
                .withSql(sql)
                .withErrorMessage(
                    "A problem occurred while attempting to insert a row into " + table)
                .prepareStatement(stmt -> prepareStatement(stmt, colNames, rsmd, rowSet))
                .executeQuery(result -> result.toObject((idx, rs) -> rs.getInt("id")));
            targetKeyMap.put(rowSet.getInt("id"), id);
          } else {
            statementFactory.newStatement()
                .withSql(sql)
                .withErrorMessage(
                    "A problem occurred while attempting to insert a row into " + table)
                .prepareStatement(stmt -> prepareStatement(stmt, colNames, rsmd, rowSet))
                .execute();
          }
          successfulRows.add(rowIndex);
        } catch (DataAccessException e) {
          if (e.getCause() instanceof MissingMappingException) {
            missingMappings++;
          } else {
            throw e;
          }
        }
      } while (rowSet.next());
    } while (missingMappings > 0 && attempts < 10);
  }

  private String getInsertSql(RowSetMetaData rsmd, int columnCount) throws SQLException {
    if (insertSql != null) {
      return insertSql + (targetKeyMap != null ? " RETURNING " + pkColumnName : "");
    }
    String sqlTemplate =
        "INSERT INTO {0} ({1}) VALUES ({2})"
            + (targetKeyMap != null ? " RETURNING " + pkColumnName : "");

    String fieldList = "";
    String paramList = "";

    for (int i = 1; i <= columnCount; i++) {
      String columnName = rsmd.getColumnName(i);

      if (skippedFields.contains(columnName)) {
        continue;
      }

      if (!Strings.isNullOrEmpty(fieldList)) {
        fieldList += ", ";
        paramList += ", ";
      }

      fieldList += columnName;
      paramList += "?";
    }

    String sql = MessageFormat.format(sqlTemplate, table, fieldList, paramList);
    return sql;
  }

  private void prepareStatement(PreparedStatement stmt, Map<String, Integer> colNames,
      RowSetMetaData rsmd,
      CachedRowSet rowSet) throws SQLException {
    int i = 1;
    for (String colName : colNames.keySet()) {
      int idx = colNames.get(colName);
      Object value;

      if (foreignKeys.containsKey(colName)) {
        List<KeyMap> fks = foreignKeys.get(colName);
        Object sourceIdObj = rowSet.getObject(colName);
        if (sourceIdObj == null) {
          value = sourceIdObj;
        } else {
          int sourceId = rowSet.getInt(colName);
          Optional<Integer> targetId = fks.stream()
              .map(fk -> fk.getTargetId(sourceId))
              .filter(Objects::nonNull)
              .findFirst();
          value = targetId.orElseThrow(
              () -> new MissingMappingException("Could not find a mapping for the " + colName
                  + " field with value " + sourceId + " in " + fks.get(0).getName()));
        }
      } else {
        value = rowSet.getObject(colName);
      }

      if (mutators.containsKey(colName)) {
        value = mutators.get(colName).mutate(value);
      }

      int nType = rsmd.getColumnType(idx);
      if (nType == java.sql.Types.ARRAY) {
        Array retrievedArray = (Array) value;
        String typeName = retrievedArray.getBaseTypeName();
        Object[] elements = ArrayUtils.toObjectArray(retrievedArray.getArray());
        Array array = stmt.getConnection().createArrayOf(typeName, elements);
        stmt.setArray(i++, array);
      } else {
        stmt.setObject(i++, value);
      }
    }
    if (log.isDebugEnabled()) {
      log.debug(stmt.toString());
    }
  }


  public static class Builder {

    private final JdbcStatementFactory statementFactory;
    private final KeyMaps keyMaps;
    private final String table;
    private SelectSpecification selectSpec;
    private InsertSpecification insertSpec;


    private Builder(JdbcStatementFactory statementFactory, KeyMaps keyMaps, String table) {
      this.statementFactory = requireNonNull(statementFactory, "statementFactory cannot be null");
      this.keyMaps = requireNonNull(keyMaps, "keyMaps cannot be null");
      this.table = requireNonNull(table, "table cannot be null");
      this.selectSpec = SelectSpecification.builder(keyMaps).build();
      this.insertSpec = InsertSpecification.builder(keyMaps, table).build();
    }


    public Builder withSelectSpecification(Consumer<SelectSpecification.Builder> consumer) {
      SelectSpecification.Builder builder = SelectSpecification.builder(keyMaps);
      consumer.accept(builder);
      this.selectSpec = builder.build();
      return this;
    }

    public Builder withInsertSpecification(Consumer<InsertSpecification.Builder> consumer) {
      InsertSpecification.Builder builder = InsertSpecification.builder(keyMaps, table);
      consumer.accept(builder);
      this.insertSpec = builder.build();
      return this;
    }

    public int execute() {
      CloneOperation op = new CloneOperation(this);
      return op.execute();
    }

  }
}
