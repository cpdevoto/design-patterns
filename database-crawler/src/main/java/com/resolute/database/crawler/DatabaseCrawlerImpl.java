package com.resolute.database.crawler;

import static java.util.Objects.requireNonNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.sql.DataSource;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.resolute.database.crawler.model.Field;
import com.resolute.database.crawler.model.ForeignKey;
import com.resolute.database.crawler.model.ForeignKeyField;
import com.resolute.database.crawler.model.Graph;
import com.resolute.database.crawler.model.Node;
import com.resolute.database.crawler.model.UniqueIndex;
import com.resolute.jdbc.simple.JdbcStatementFactory;
import com.resolute.utils.simple.ElapsedTimeUtils;

class DatabaseCrawlerImpl implements DatabaseCrawler {
  private static final String TABLE_NAME = "table_name";
  private static final String CONSTRAINT_TYPE = "constraint_type";
  private static final String CONSTRAINT_NAME = "constraint_name";
  private static final String INDEX_NAME = "index_name";
  private static final String COLUMN_NAME = "column_name";
  private static final String KEY_FIELD = "key_field";
  private static final String DATA_TYPE = "data_type";
  private static final String COLUMN_DEFAULT = "column_default";

  private static final String REFERENCED_COLUMN_NAME = "referenced_column_name";
  private static final String REFERENCED_TABLE_NAME = "referenced_table_name";
  private static final String FK_COLUMN_NAME = "fk_column_name";
  private static final String FK_TABLE_NAME = "fk_table_name";
  private static final String FK_CONSTRAINT_NAME = "fk_constraint_name";

  private final DataSource dataSource;
  private final JdbcStatementFactory statementFactory;

  static DatabaseCrawlerImpl create(DataSource dataSource) {
    return new DatabaseCrawlerImpl(dataSource);
  }

  private DatabaseCrawlerImpl(DataSource dataSource) {
    requireNonNull(dataSource, "dataSource cannot be null");
    this.dataSource = dataSource;
    this.statementFactory = JdbcStatementFactory.getInstance(dataSource);
  }

  @Override
  public JdbcStatementFactory getStatementFactory() {
    return statementFactory;
  }

  @Override
  public boolean testConnection() {
    try (Connection conn = dataSource.getConnection()) {
      return true;
    } catch (SQLException e) {
      return false;
    }
  }

  @Override
  public Graph getSchemaGraph() {
    Graph graph = new Graph();

    long start = System.currentTimeMillis();
    System.out.println("Retrieving tables and unique constraints...");
    final String sql1 =
        "SELECT t.table_name, tc.constraint_type, tc.constraint_name AS constraint_name,\n" +
            "       kcu.column_name AS key_field, c.data_type, c.column_default\n" +
            "FROM information_schema.tables t\n" +
            "LEFT OUTER JOIN information_schema.table_constraints tc\n" +
            "  ON t.table_name = tc.table_name AND \n" +
            "     (tc.constraint_type = 'PRIMARY KEY' OR tc.constraint_type = 'UNIQUE')\n" +
            "LEFT OUTER JOIN information_schema.key_column_usage kcu\n" +
            "  ON kcu.constraint_catalog = tc.constraint_catalog\n" +
            "    AND kcu.constraint_schema = tc.constraint_schema\n" +
            "    AND kcu.constraint_name = tc.constraint_name\n" +
            "LEFT OUTER JOIN information_schema.columns c\n" +
            "  ON t.table_name = c.table_name\n" +
            "    AND c.column_name = kcu.column_name\n" +
            "WHERE t.table_schema = 'public'\n" +
            "  AND t.table_type = 'BASE TABLE'\n" +
            "ORDER BY t.table_name, tc.constraint_name, kcu.ordinal_position;";

    List<Map<String, String>> records = statementFactory.newStatement()
        .withSql(sql1)
        .withErrorMessage(
            "A problem occurred while attempting to retrieve the list of all table names")
        .executeQuery(
            result -> result.toList((idx, rs) -> {
              Map<String, String> record = Maps.newHashMap();
              record.put(TABLE_NAME, rs.getString(TABLE_NAME));
              record.put(CONSTRAINT_TYPE, rs.getString(CONSTRAINT_TYPE));
              record.put(CONSTRAINT_NAME, rs.getString(CONSTRAINT_NAME));
              record.put(KEY_FIELD, rs.getString(KEY_FIELD));
              record.put(DATA_TYPE, rs.getString(DATA_TYPE));
              record.put(COLUMN_DEFAULT, rs.getString(COLUMN_DEFAULT));
              return record;
            }));

    String currentTableName = null;
    String currentConstraintName = null;
    List<Field> currentKeyFields = null;
    List<UniqueIndex> currentUniqueIndeces = null;
    List<Field> currentIndexFields = null;
    for (Map<String, String> record : records) {
      String tableName = record.get(TABLE_NAME);
      String constraintType = record.get(CONSTRAINT_TYPE);
      String constraintName = record.get(CONSTRAINT_NAME);
      String dataType = record.get(DATA_TYPE);
      String keyField = record.get(KEY_FIELD);
      String columnDefault = record.get(COLUMN_DEFAULT);
      if (!tableName.equals(currentTableName)) {
        if (currentConstraintName != null && !currentIndexFields.isEmpty()) {
          currentUniqueIndeces.add(new UniqueIndex(currentConstraintName, currentIndexFields));
        }
        if (currentTableName != null) {
          graph.addNode(currentTableName, currentKeyFields, currentUniqueIndeces);
        }
        currentTableName = tableName;
        currentConstraintName = constraintName;
        currentKeyFields = Lists.newArrayList();
        currentUniqueIndeces = Lists.newArrayList();
        currentIndexFields = Lists.newArrayList();
      }
      if (keyField != null) {
        if ("PRIMARY KEY".equals(constraintType)) {
          if (!constraintName.equals(currentConstraintName)) {
            if (currentConstraintName != null && !currentIndexFields.isEmpty()) {
              currentUniqueIndeces.add(new UniqueIndex(currentConstraintName, currentIndexFields));
            }
            currentConstraintName = constraintName;
            currentIndexFields = Lists.newArrayList();
          }
          currentKeyFields.add(new Field(keyField, dataType, columnDefault));
        } else {
          if (!constraintName.equals(currentConstraintName)) {
            if (currentConstraintName != null && !currentIndexFields.isEmpty()) {
              currentUniqueIndeces.add(new UniqueIndex(currentConstraintName, currentIndexFields));
            }
            currentConstraintName = constraintName;
            currentIndexFields = Lists.newArrayList();
          }
          currentIndexFields.add(new Field(keyField, dataType, columnDefault));
        }
      }
    }
    if (currentIndexFields != null && !currentIndexFields.isEmpty()) {
      currentUniqueIndeces.add(new UniqueIndex(currentConstraintName, currentIndexFields));
    }
    if (currentTableName != null) {
      graph.addNode(currentTableName, currentKeyFields, currentUniqueIndeces);
    }


    long end = System.currentTimeMillis();
    System.out.println(
        "Tables and unique constraints retrieved in " + ElapsedTimeUtils.format(end - start) + ".");


    // We need to wait some amount of time for the database to finish its initialization sequence
    // before executing the next SQL statement
    // or there will be massive contention issues and really bad performance!
    pause(60000);

    start = System.currentTimeMillis();
    System.out.println("Retrieving foreign keys..");
    final String sql2 =
        "SELECT kcu1.constraint_name AS fk_constraint_name,\n" +
            "       kcu1.table_name AS fk_table_name,\n" +
            "       kcu1.column_name AS fk_column_name,\n" +
            "       kcu1.ordinal_position AS fk_ordinal_position,\n" +
            "       kcu2.table_name AS referenced_table_name,\n" +
            "       kcu2.column_name AS referenced_column_name          \n" +
            "FROM information_schema.referential_constraints rc\n" +
            "JOIN information_schema.key_column_usage kcu1\n" +
            "  ON kcu1.constraint_catalog = rc.constraint_catalog\n" +
            "    AND kcu1.constraint_schema = rc.constraint_schema\n" +
            "  AND kcu1.constraint_name = rc.constraint_name\n" +
            "JOIN information_schema.key_column_usage kcu2\n" +
            "  ON kcu2.constraint_catalog = rc.unique_constraint_catalog\n" +
            "    AND kcu2.constraint_schema = rc.unique_constraint_schema\n" +
            "  AND kcu2.constraint_name = rc.unique_constraint_name\n" +
            "    AND kcu2.ordinal_position = kcu1.ordinal_position\n" +
            "ORDER BY  kcu2.table_schema, kcu2.table_name, kcu2.constraint_name, kcu1.table_schema, kcu1.table_name, kcu2.ordinal_position, kcu2.column_name";

    records = statementFactory.newStatement()
        .withSql(sql2)
        .withErrorMessage(
            "A problem occurred while attempting to retrieve the list of all foreign key references")
        .executeQuery(result -> result.toList((idx, rs) -> {
          Map<String, String> record = Maps.newHashMap();
          record.put(FK_CONSTRAINT_NAME, rs.getString(FK_CONSTRAINT_NAME));
          record.put(FK_TABLE_NAME, rs.getString(FK_TABLE_NAME));
          record.put(FK_COLUMN_NAME, rs.getString(FK_COLUMN_NAME));
          record.put(REFERENCED_TABLE_NAME, rs.getString(REFERENCED_TABLE_NAME));
          record.put(REFERENCED_COLUMN_NAME, rs.getString(REFERENCED_COLUMN_NAME));
          return record;
        }));
    end = System.currentTimeMillis();
    System.out.println("Foreign keys retrieved in " + ElapsedTimeUtils.format(end - start) + ".");

    currentConstraintName = null;
    String currentFromTable = null;
    String currentToTable = null;
    List<ForeignKeyField> currentFields = null;
    for (Map<String, String> record : records) {
      String constraintName = record.get(FK_CONSTRAINT_NAME);
      String toTable = record.get(FK_TABLE_NAME);
      String toColumn = record.get(FK_COLUMN_NAME);
      String fromTable = record.get(REFERENCED_TABLE_NAME);
      String fromColumn = record.get(REFERENCED_COLUMN_NAME);
      if (!constraintName.equals(currentConstraintName)) {
        if (currentConstraintName != null) {
          graph.addEdge(currentFromTable, currentToTable, new ForeignKey(currentFields));
        }
        currentConstraintName = constraintName;
        currentFromTable = fromTable;
        currentToTable = toTable;
        currentFields = Lists.newArrayList();
      }
      currentFields.add(new ForeignKeyField(fromColumn, toColumn));
    }
    if (currentConstraintName != null) {
      graph.addEdge(currentFromTable, currentToTable, new ForeignKey(currentFields));
    }


    start = System.currentTimeMillis();
    System.out.println("Retrieving table fields...");
    final String sql3 =
        "SELECT t.table_name, c.column_name, c.data_type, c.column_default\n" +
            "FROM information_schema.tables t\n" +
            "JOIN information_schema.columns c\n" +
            "  ON t.table_name = c.table_name\n" +
            "WHERE t.table_schema = 'public'\n" +
            "  AND t.table_type = 'BASE TABLE'\n" +
            "ORDER BY t.table_name, c.ordinal_position;";

    records = statementFactory.newStatement()
        .withSql(sql3)
        .withErrorMessage(
            "A problem occurred while attempting to retrieve the list of all table columns")
        .executeQuery(
            result -> result.toList((idx, rs) -> {
              Map<String, String> record = Maps.newHashMap();
              record.put(TABLE_NAME, rs.getString(TABLE_NAME));
              record.put(COLUMN_NAME, rs.getString(COLUMN_NAME));
              record.put(DATA_TYPE, rs.getString(DATA_TYPE));
              record.put(COLUMN_DEFAULT, rs.getString(COLUMN_DEFAULT));
              return record;
            }));
    end = System.currentTimeMillis();
    System.out.println("Table columns retrieved in " + ElapsedTimeUtils.format(end - start) + ".");

    currentTableName = null;
    List<Field> currentColumns = null;
    for (Map<String, String> record : records) {
      String tableName = record.get(TABLE_NAME);
      String columnName = record.get(COLUMN_NAME);
      String dataType = record.get(DATA_TYPE);
      String columnDefault = record.get(COLUMN_DEFAULT);
      if (!tableName.equals(currentTableName)) {
        if (currentTableName != null) {
          Optional<Node> node = graph.getNode(currentTableName);
          if (node.isPresent()) {
            node.get().addFields(currentColumns);
          }
        }
        currentTableName = tableName;
        currentColumns = Lists.newArrayList();
      }
      currentColumns.add(new Field(columnName, dataType, columnDefault));
    }
    if (currentColumns != null) {
      Optional<Node> node = graph.getNode(currentTableName);
      if (node.isPresent()) {
        node.get().addFields(currentColumns);
      }
    }

    // The preceding code retrieved all unique constraints, but it did not retrieve any of the
    // unique indeces!
    start = System.currentTimeMillis();
    System.out.println("Retrieving unique indeces...");
    final String sql4 =
        "SELECT \n" +
            " tbl.relname AS table_name,\n" +
            " c.relname AS index_name, \n" +
            " a.attname AS column_name \n" +
            "FROM pg_class c\n" +
            "JOIN pg_attribute a ON a.attrelid = c.oid\n" +
            "JOIN pg_index i ON i.indexrelid = c.oid\n" +
            "JOIN pg_class tbl on tbl.oid = i.indrelid\n" +
            "JOIN pg_namespace n ON n.oid = tbl.relnamespace\n" +
            "WHERE n.nspname = 'public' AND c.reltype = 0 AND i.indisunique = TRUE AND i.indisprimary = FALSE\n"
            +
            "ORDER BY tbl.relname, c.relname, a.attnum;";
    records = statementFactory.newStatement()
        .withSql(sql4)
        .withErrorMessage(
            "A problem occurred while attempting to retrieve the list of all unique indeces")
        .executeQuery(
            result -> result.toList((idx, rs) -> {
              Map<String, String> record = Maps.newHashMap();
              record.put(TABLE_NAME, rs.getString(TABLE_NAME));
              record.put(INDEX_NAME, rs.getString(INDEX_NAME));
              record.put(COLUMN_NAME, rs.getString(COLUMN_NAME));
              return record;
            }));

    currentTableName = null;
    String currentIndexName = null;
    List<String> currentIndex = null;
    for (Map<String, String> record : records) {
      String tableName = record.get(TABLE_NAME);
      String indexName = record.get(INDEX_NAME);
      String columnName = record.get(COLUMN_NAME);
      if (!tableName.equals(currentTableName)) {
        if (currentTableName != null) {
          Optional<Node> node = graph.getNode(currentTableName);
          if (node.isPresent()) {
            node.get().addUniqueIndex(currentIndexName, currentIndex);
          }
        }
        currentTableName = tableName;
        currentIndexName = indexName;
        currentIndex = Lists.newArrayList();
      }
      if (!indexName.equals(currentIndexName)) {
        Optional<Node> node = graph.getNode(currentTableName);
        if (node.isPresent()) {
          node.get().addUniqueIndex(currentIndexName, currentIndex);
        }
        currentIndexName = indexName;
        currentIndex = Lists.newArrayList();
      }
      currentIndex.add(columnName);
    }
    if (currentIndex != null) {
      Optional<Node> node = graph.getNode(currentTableName);
      if (node.isPresent()) {
        node.get().addUniqueIndex(currentIndexName, currentIndex);
      }
    }

    return graph;
  }

  private void pause(long waitTime) {
    /*
     * The wait time is not improving the overall performance of the migration build!!
     */
    // try {
    // System.out.println(
    // "Waiting " + ElapsedTimeUtils.format(waitTime) + " before executing SQL code...");
    // Thread.sleep(waitTime);
    // } catch (InterruptedException e) {
    // }
  }


}
