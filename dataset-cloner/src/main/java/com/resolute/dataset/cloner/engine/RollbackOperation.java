package com.resolute.dataset.cloner.engine;

import static com.google.common.base.Preconditions.checkArgument;
import static com.resolute.dataset.cloner.utils.Constants.DEFAULT_MAX_RECS_PER_DELETE;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import javax.sql.DataSource;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.resolute.database.crawler.model.Field;
import com.resolute.database.crawler.model.Graph;
import com.resolute.database.crawler.model.Node;
import com.resolute.dataset.cloner.log.LogFileParser;
import com.resolute.dataset.cloner.utils.Constants;
import com.resolute.dataset.cloner.utils.Key;
import com.resolute.dataset.cloner.utils.NodeUtils;
import com.resolute.jdbc.simple.JdbcStatementFactory;

public class RollbackOperation {

  private final Graph graph;
  private final DataSource dataSource;
  private final JdbcStatementFactory statementFactory;
  private final Integer tableNamePrefix;
  private final Map<String, Set<Key>> keysInserted;
  private final boolean debug;
  private final int maxRecsPerDelete;

  public static Builder forGraph(Graph graph) {
    return new Builder(graph);
  }

  private RollbackOperation(Builder builder) {
    this.graph = builder.graph;
    this.dataSource = builder.dataSource;
    this.statementFactory = JdbcStatementFactory.getInstance(dataSource);
    this.tableNamePrefix = builder.tableNamePrefix;
    this.keysInserted = builder.keysInserted;
    this.debug = builder.debug;
    this.maxRecsPerDelete = builder.maxRecsPerDelete;
  }

  public void execute() {
    List<Node> nodes = Lists.newArrayList(graph.topologicalSort());
    Collections.reverse(nodes);
    if (keysInserted != null) {
      rollbackInsertedRows(nodes, keysInserted);
    } else if (tableNamePrefix != null) {
      cleanupTables(nodes);
    }
  }

  private void rollbackInsertedRows(List<Node> nodes, Map<String, Set<Key>> keysInserted) {
    if (this.debug) {
      System.out.println("----------------------------------------------");
      System.out.println("ROLLBACK");
      System.out.println("----------------------------------------------");
      System.out.println();
    }
    for (Node node : nodes) {
      Set<Key> keys = keysInserted.get(node.getName());
      if (keys == null || keys.isEmpty()) {
        continue;
      }
      System.out
          .println(
              String.format("Rolling back %d record(s) in table %s", keys.size(), node.getName()));
      List<Key> keyList = keys.stream().collect(toList());
      List<List<Key>> partitionedKeyList =
          Lists.partition(keyList, maxRecsPerDelete);
      for (List<Key> currentKeys : partitionedKeyList) {
        Optional<String> optSql = generateRollbackStatement(node, currentKeys);
        if (!optSql.isPresent()) {
          continue;
        }
        String sql = optSql.get();
        if (this.debug) {
          System.out.println(sql);
          System.out.println();
        }
        rollback(sql);
      }
    }
  }

  private Optional<String> generateRollbackStatement(Node node, List<Key> keys) {
    if (keys.isEmpty()) {
      return Optional.empty();
    }
    StringBuilder buf = new StringBuilder();
    buf.append("DELETE FROM ")
        .append(node.getName())
        .append(" t1 WHERE EXISTS (\n")
        .append("  SELECT TRUE FROM ")
        .append(node.getName())
        .append(" t2\n")
        .append("   WHERE ");
    boolean firstField = true;
    for (String keyField : node.getPrimaryKey()) {
      if (firstField) {
        firstField = false;
      } else {
        buf.append(" AND ");
      }
      buf.append("t1.\"")
          .append(keyField)
          .append("\" = t2.\"")
          .append(keyField)
          .append("\"");
    }
    buf.append("\n     AND (");
    firstField = true;
    for (String keyField : node.getPrimaryKey()) {
      if (firstField) {
        firstField = false;
      } else {
        buf.append(", ");
      }
      buf.append("\"").append(keyField).append("\"");
    }
    buf.append(") IN (\n");
    boolean firstKey = true;
    for (Key key : keys) {
      if (firstKey) {
        firstKey = false;
      } else {
        buf.append("),\n");
      }
      buf.append("      (");
      firstField = true;
      for (Field keyField : node.getPrimaryKeyFields()) {
        if (firstField) {
          firstField = false;
        } else {
          buf.append(", ");
        }
        String value = NodeUtils.toSqlValue(keyField, key.getFieldValue(keyField.getName()));
        buf.append(value);
      }
    }
    buf.append(")\n     )\n");
    buf.append(");");
    return Optional.of(buf.toString());
  }

  private void rollback(String sql) {
    statementFactory.newStatement()
        .withErrorMessage("A problem occurred while attempting to rollback a clone operation")
        .executeMultipleStatements(conn -> {

          statementFactory.newStatement()
              .withSql(Constants.SET_SESSION_REPLICATION_ROLE_SQL)
              .executeWithConnection(conn);

          statementFactory.newStatement()
              .withSql(sql)
              .executeWithConnection(conn);
        });

  }

  private void cleanupTables(List<Node> nodes) {
    StringBuilder buf = new StringBuilder();
    for (Node node : nodes) {
      buf.append("DROP TABLE IF EXISTS temp_" + tableNamePrefix + "_" + node.getName() + ";\n");
      if (node.hasUnaryAssociation()) {
        buf.append(
            "DROP TABLE IF EXISTS temp_unary_" + tableNamePrefix + "_" + node.getName() + ";\n");
      }
    }
    if (this.debug) {
      System.out.println("----------------------------------------------");
      System.out.println("CLEANUP TEMP TABLES");
      System.out.println("----------------------------------------------");
      System.out.print(buf.toString());
      System.out.println();
    }
    statementFactory.newStatement()
        .withSql(buf.toString())
        .withErrorMessage("A problem occurred while attempting to clean up data after cloning")
        .execute();

  }


  public static class Builder {

    private static final int DEFAULT_EXEC_THRESHOLD = 5000;
    private final Graph graph;
    private DataSource dataSource;
    private Integer tableNamePrefix;
    private Map<String, Set<Key>> keysInserted;
    private boolean debug = false;
    private int maxRecsPerDelete = DEFAULT_MAX_RECS_PER_DELETE;

    private Builder(Graph graph) {
      this.graph = requireNonNull(graph, "graph cannot be null");
    }

    public Builder withDataSource(DataSource dataSource) {
      this.dataSource = requireNonNull(dataSource, "dataSource cannot be null");
      return this;

    }

    public Builder withTableNamePrefix(Integer tableNamePrefix) {
      this.tableNamePrefix = tableNamePrefix;
      return this;
    }

    public Builder withKeysInserted(Map<String, Set<Key>> keysInserted) {
      requireNonNull(keysInserted, "keysInserted cannot be null");
      Map<String, Set<Key>> temp = Maps.newHashMap();
      for (Entry<String, Set<Key>> entry : keysInserted.entrySet()) {
        Set<Key> keys = ImmutableSet.copyOf(entry.getValue());
        temp.put(entry.getKey(), keys);
      }
      this.keysInserted = ImmutableMap.copyOf(temp);
      return this;
    }

    public Builder withDebug(boolean debug) {
      this.debug = debug;
      return this;
    }

    Builder withMaxRecsPerDelete(int maxRecsPerDelete) {
      checkArgument(maxRecsPerDelete > 0, "expected a positive integer for maxRecsPerDelete");
      this.maxRecsPerDelete = maxRecsPerDelete;
      return this;
    }

    public RollbackOperation build() {
      requireNonNull(dataSource, "dataSource cannot be null");
      return new RollbackOperation(this);
    }

    public void executeFromLogFile(String file) {
      executeFromLogFile(file, DEFAULT_EXEC_THRESHOLD);
    }

    public void executeFromLogFile(File file) {
      executeFromLogFile(file, DEFAULT_EXEC_THRESHOLD);
    }

    public void executeFromLogFile(String file, int executionThreshold) {
      requireNonNull(file, "file cannot be null");
      executeFromLogFile(new File(file), executionThreshold);
    }

    public void executeFromLogFile(File file, int executionThreshold) {
      requireNonNull(file, "file cannot be null");
      requireNonNull(dataSource, "dataSource cannot be null");
      LogFileParser parser = LogFileParser.forGraph(graph);

      parser.parse(file, executionThreshold, logFile -> {
        RollbackOperation op = RollbackOperation.forGraph(graph)
            .withDataSource(dataSource)
            .withDebug(debug)
            .withTableNamePrefix(logFile.getTableNamePrefix())
            .withKeysInserted(logFile.getKeysInserted())
            .build();

        op.execute();
      });
    }
  }
}
