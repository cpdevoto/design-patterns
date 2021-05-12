package com.resolute.dataset.cloner.engine;

import static com.google.common.base.Preconditions.checkArgument;
import static com.resolute.dataset.cloner.utils.Constants.DEFAULT_MAX_RECS_PER_INSERT;
import static com.resolute.dataset.cloner.utils.Constants.SET_SESSION_REPLICATION_ROLE_SQL;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toSet;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.resolute.database.crawler.model.Edge;
import com.resolute.database.crawler.model.Field;
import com.resolute.database.crawler.model.ForeignKeyField;
import com.resolute.database.crawler.model.Node;
import com.resolute.dataset.cloner.log.Logger;
import com.resolute.dataset.cloner.utils.Key;
import com.resolute.dataset.cloner.utils.KeyMaps;
import com.resolute.dataset.cloner.utils.NodeUtils;
import com.resolute.dataset.cloner.utils.TempTables;
import com.resolute.jdbc.simple.DataAccessException;

class CloneOperation {
  private final Node node;
  private final DataSource dataSource;
  private final int tableNamePrefix;
  private final KeyMaps keyMaps;
  private final Logger log;
  private final FieldLevelMutators mutators;
  private final TupleLevelMutator tupleLevelMutator;
  private final boolean insertMode;
  private final boolean pureCopyMode;
  private final Logger outputFile;
  private final ColumnActions actions;
  private final boolean debug;
  private final int copyNumber;
  private final int maxRecsPerInsert;

  static Builder forNode(Node node) {
    return new Builder(node);
  }

  private CloneOperation(Builder builder) {
    this.node = builder.node;
    this.dataSource = builder.dataSource;
    this.tableNamePrefix = builder.tableNamePrefix;
    this.keyMaps = builder.keyMaps;
    this.log = builder.log;
    this.mutators = builder.mutators;
    this.tupleLevelMutator = builder.tupleLevelMutator;
    this.insertMode = builder.insertMode;
    this.pureCopyMode = builder.pureCopyMode;
    this.outputFile = builder.outputFile;
    this.actions = ColumnActions.create(node, mutators, pureCopyMode);
    this.debug = builder.debug;
    this.copyNumber = builder.copyNumber;
    this.maxRecsPerInsert = builder.maxRecsPerInsert;
  }

  void execute() {

    if (debug) {
      System.out.println("----------------------------------------------");
      System.out.println(
          String.format("CLONE OPERATION FOR TABLE %s", node.getName()));
      System.out.println("----------------------------------------------");

    }

    if (log != null) {
      log.println(">>>>INSERT INTO TABLE: " + node.getName());
    }

    Set<Map<String, String>> keys = Sets.newHashSet();
    if (node.hasUnaryAssociation()) {
      keys = selectKeysInTempTable();
    }

    List<Map<String, String>> records = select();
    if (records.isEmpty()) {
      return;
    }

    System.out
        .println(String.format("Cloning %d record(s) in table %s", records.size(), node.getName()));

    if (!insertMode) {
      outputFile.println(String.format("-- Inserting records into table %s", node.getName()));
      outputFile.println();
    }

    Optional<InsertStatement> optInsertStatement;
    Set<Key> visitedKeys = Sets.newHashSet();
    Integer lastNumVisited = 0;
    do {
      List<List<Map<String, String>>> partitionedRecords =
          Lists.partition(records, maxRecsPerInsert);
      for (List<Map<String, String>> currentRecords : partitionedRecords) {
        optInsertStatement = generateInsertStatement(currentRecords, keys, visitedKeys, copyNumber);
        if (!optInsertStatement.isPresent()) {
          if (debug) {
            System.out.println("There were no records to insert!");
          }
          continue;
        }
        InsertStatement insertStatement = optInsertStatement.get();
        if (debug) {
          System.out.println(insertStatement.getSql());
          System.out.println();
        }
        if (!insertMode) {
          outputFile.println(insertStatement.getSql());
          outputFile.println();
        } else {

          List<Key> generatedKeys = insert(insertStatement);

          for (int i = 0; i < insertStatement.getInsertedRecordKeys().size(); i++) {
            Key sourceKey = insertStatement.getInsertedRecordKeys().get(i);
            Key targetKey = generatedKeys.get(i);
            keyMaps.put(node.getName(), sourceKey, targetKey);
          }
        }
      }
      if (visitedKeys.size() == lastNumVisited && lastNumVisited < records.size()) {
        throw new IllegalStateException(
            "We seem to be stuck in a loop while inserting records into table " + node.getName()
                + "!  Stopping after " + lastNumVisited + " records");
      }
      lastNumVisited = visitedKeys.size();
    } while (visitedKeys.size() < records.size());
  }

  private List<Key> insert(InsertStatement insertStatement) {
    try {
      try (Connection conn = dataSource.getConnection()) {
        try (PreparedStatement stmt = conn.prepareStatement(SET_SESSION_REPLICATION_ROLE_SQL)) {
          stmt.execute();
        }
        try (PreparedStatement stmt = conn.prepareStatement(insertStatement.getSql())) {
          try (ResultSet rs = stmt.executeQuery()) {
            List<Key> records = Lists.newArrayList();
            while (rs.next()) {
              Key.Builder keyBuilder = Key.builder();
              for (String key : node.getPrimaryKey()) {
                Object objValue = rs.getObject(key);
                String value = (objValue == null ? null : String.valueOf(objValue));
                keyBuilder.withFieldValue(key, value);
              }
              Key key = keyBuilder.build();
              if (log != null) {
                logInsertedKey(key);
              }
              records.add(key);
            }
            return records;
          }
        }
      }
    } catch (SQLException e) {
      throw new DataAccessException(e);
    }
  }

  private Optional<InsertStatement> generateInsertStatement(List<Map<String, String>> records,
      Set<Map<String, String>> keys, Set<Key> visitedKeys, int copyNumber) {
    StringBuilder buf = new StringBuilder();
    buf.append("INSERT INTO ")
        .append(node.getName())
        .append(" (");
    boolean firstField = true;
    for (String fieldName : node.getFieldNames()) {
      ColumnAction action = actions.get(fieldName);
      if (action == ColumnAction.OMIT) {
        continue;
      }
      if (firstField) {
        firstField = false;
      } else {
        buf.append(", ");
      }
      buf.append("\"").append(fieldName).append("\"");
    }
    buf.append(") VALUES\n    ");
    boolean firstRecord = true;
    boolean atLeastOneRecordInserted = false;
    List<Key> insertedRecordKeys = Lists.newArrayList();
    for (Map<String, String> record : records) {
      Key key = getKey(record);
      if (visitedKeys.contains(key)) {
        continue;
      }
      Optional<String> sql = generateInsertStatement(record, keys, copyNumber);
      if (!sql.isPresent()) {
        continue;
      }
      visitedKeys.add(key);
      atLeastOneRecordInserted = true;
      Key insertedRecordKey = getKey(record);
      insertedRecordKeys.add(insertedRecordKey);
      if (firstRecord) {
        firstRecord = false;
      } else {
        buf.append(",\n    ");
      }
      buf.append(sql.get());
    }
    buf.append(" RETURNING ");
    firstField = true;
    for (String keyField : node.getPrimaryKey()) {
      if (firstField) {
        firstField = false;
      } else {
        buf.append(", ");
      }
      buf.append(keyField);
    }
    buf.append(";");
    String sql = buf.toString();
    if (!atLeastOneRecordInserted) {
      return Optional.empty();
    }
    return Optional.of(InsertStatement.builder()
        .withSql(sql)
        .withInsertedRecordKeys(insertedRecordKeys)
        .build());
  }

  private Key getKey(Map<String, String> record) {
    Key.Builder keyBuilder = Key.builder();
    for (String keyField : node.getPrimaryKey()) {
      keyBuilder.withFieldValue(keyField, record.get(keyField));
    }
    return keyBuilder.build();
  }

  private Optional<String> generateInsertStatement(Map<String, String> record,
      Set<Map<String, String>> keys, int copyNumber) {
    Map<Field, String> fieldValues = Maps.newLinkedHashMap();
    for (Field field : node.getFields()) {
      String fieldName = field.getName();
      ColumnAction action = actions.get(fieldName);
      if (action == ColumnAction.OMIT) {
        continue;
      }
      if (action == ColumnAction.RESOLVE_AT_ROW_LEVEL) {
        action = getRowLevelAction(keys, record, field);
      }
      String value = record.get(fieldName);
      if (action == ColumnAction.COPY || value == null) {
        // Do nothing!
      } else if (action == ColumnAction.MUTATE) {
        value = mutators.get(fieldName, FieldLevelMutator.DEFAULT).mutate(tableNamePrefix,
            copyNumber, value);
      } else if (action == ColumnAction.FK_LOOKUP) {
        Edge foreignKey = NodeUtils.getForeignKey(node, fieldName).get();
        Key.Builder keyBuilder = Key.builder();
        for (ForeignKeyField keyField : foreignKey.getForeignKey().getFields()) {
          keyBuilder.withFieldValue(keyField.getFromField(), record.get(keyField.getToField()));
        }
        Key sourceKey = keyBuilder.build();
        Optional<Key> optTargetKey =
            keyMaps.getTargetKey(foreignKey.getFrom().getName(), sourceKey);
        if (!optTargetKey.isPresent()) {
          if (NodeUtils.isPartOfUnaryForeignKey(node, fieldName)) {
            return Optional.empty();
          } else {
            // This can happen for instance in the following scenario:
            // test1_tbl clone set: (id=1)
            // test4_tbl clone set: (id=1, test1_id=1, parent_id=NULL), (id=2, test1_id=2,
            // parent_id=1)
            // In this case, the lookup for a record in the test1_tbl clone set with an id of will
            // fail, so we should just treat it as a COPY action.
            // Do nothing!
          }
        } else {
          Key targetKey = optTargetKey.get();
          String fromField = foreignKey.getForeignKey().getFields().stream()
              .filter(f -> fieldName.equals(f.getToField()))
              .map(ForeignKeyField::getFromField)
              .findAny()
              .get();
          value = targetKey.getFieldValue(fromField);
        }
      }
      fieldValues.put(field, value);
    }
    if (tupleLevelMutator != null) {
      fieldValues = tupleLevelMutator.mutate(fieldValues);
    }
    StringBuilder buf = new StringBuilder("  (");
    boolean firstField = true;
    for (Entry<Field, String> entry : fieldValues.entrySet()) {
      if (firstField) {
        firstField = false;
      } else {
        buf.append(", ");
      }
      String value = NodeUtils.toSqlValue(entry.getKey(), entry.getValue());
      buf.append(value);

    }
    buf.append(")");
    return Optional.of(buf.toString());
  }

  private ColumnAction getRowLevelAction(Set<Map<String, String>> keys, Map<String, String> record,
      Field field) {
    ColumnAction action = null;
    String fieldName = field.getName();
    List<Edge> foreignKeys = computeForeignKeysForRowLevelAction(fieldName);

    for (Edge edge : foreignKeys) {
      Map<String, String> key = Maps.newHashMap();
      for (ForeignKeyField foreignKeyField : edge.getForeignKey().getFields().stream()
          .collect(Collectors.toList())) {
        key.put(foreignKeyField.getFromField(), record.get(foreignKeyField.getToField()));
      }
      if (keys.contains(key)) {
        if (NodeUtils.isPartOfUnaryForeignKey(node, fieldName)) {
          action = ColumnAction.FK_LOOKUP;
        } else {
          action = ColumnAction.COPY;
        }
      } else {
        if (NodeUtils.isPartOfUnaryForeignKey(node, fieldName)) {
          action = ColumnAction.COPY;
        } else if (field.getDataType().equals("text")
            || field.getDataType().equals("character varying")) {
          action = ColumnAction.MUTATE;
        } else {
          action = ColumnAction.COPY;
        }
      }
    }
    return action;
  }

  private List<Edge> computeForeignKeysForRowLevelAction(String fieldName) {
    // Either the field is (a) part of a foreign key that has a unary association, or (b) it is part
    // of one or more unique indeces that contain a foreign key field that is part of a foreign key
    // that has a unary association. In either case we need to find the foreign keys in question.
    List<Edge> foreignKeys = Lists.newArrayList();
    if (NodeUtils.isPartOfUnaryForeignKey(node, fieldName)) {
      Edge foreignKey = NodeUtils.getForeignKey(node, fieldName).get();
      foreignKeys.add(foreignKey);
    }
    if (foreignKeys.isEmpty()) {
      // the field is part of one or more unique indeces
      for (List<Field> index : NodeUtils.getUniqueIndeces(node, fieldName)) {
        for (Field indexField : index) {
          if (NodeUtils.isPartOfUnaryForeignKey(node, indexField.getName())) {
            Edge foreignKey = NodeUtils.getForeignKey(node, indexField.getName()).get();
            foreignKeys.add(foreignKey);
          }
        }
      }
    }
    return foreignKeys;
  }

  private List<Map<String, String>> select() {
    String tableName = node.getName();
    StringBuilder buf = new StringBuilder();
    buf.append("SELECT t2.* FROM ")
        .append(TempTables.getTempTableName(tableNamePrefix, tableName))
        .append(" t1 JOIN ").append(tableName).append(" t2 ON ");
    boolean firstField = true;
    for (String keyField : node.getPrimaryKey()) {
      if (firstField) {
        firstField = false;
      } else {
        buf.append(" AND ");
      }
      buf.append("t1.\"").append(keyField).append("\" = t2.\"").append(keyField).append("\"");
    }
    buf.append(" ORDER BY ");
    firstField = true;
    for (String keyField : node.getPrimaryKey()) {
      if (firstField) {
        firstField = false;
      } else {
        buf.append(", ");
      }
      buf.append("t2.\"").append(keyField).append("\"");
    }
    buf.append(";");

    String sql = buf.toString();
    try {
      try (Connection conn = dataSource.getConnection()) {
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
          try (ResultSet rs = stmt.executeQuery()) {
            List<Map<String, String>> records = Lists.newArrayList();
            while (rs.next()) {
              Map<String, String> record = Maps.newLinkedHashMap();
              for (int i = 1; i < rs.getMetaData().getColumnCount() + 1; i++) {
                String key = rs.getMetaData().getColumnName(i);
                Object objValue = rs.getObject(i);
                String value = (objValue == null ? null : String.valueOf(objValue));
                record.put(key, value);
              }
              records.add(record);
            }
            return records;
          }
        }
      }
    } catch (SQLException e) {
      throw new DataAccessException(e);
    }

  }

  private Set<Map<String, String>> selectKeysInTempTable() {
    String tableName = node.getName();
    StringBuilder buf = new StringBuilder();
    buf.append("SELECT * FROM ")
        .append(TempTables.getTempTableName(tableNamePrefix, tableName))
        .append(";");

    String sql = buf.toString();
    try {
      try (Connection conn = dataSource.getConnection()) {
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
          try (ResultSet rs = stmt.executeQuery()) {
            List<Map<String, String>> records = Lists.newArrayList();
            while (rs.next()) {
              Map<String, String> record = Maps.newLinkedHashMap();
              for (int i = 1; i < rs.getMetaData().getColumnCount() + 1; i++) {
                String key = rs.getMetaData().getColumnName(i);
                Object objValue = rs.getObject(i);
                String value = (objValue == null ? null : String.valueOf(objValue));
                record.put(key, value);
              }
              records.add(record);
            }
            return records.stream().collect(toSet());
          }
        }
      }
    } catch (SQLException e) {
      throw new DataAccessException(e);
    }

  }



  private void logInsertedKey(Key key) {
    boolean firstField = true;
    for (String keyField : node.getPrimaryKey()) {
      if (firstField) {
        firstField = false;
      } else {
        log.print(",");
      }
      log.print(key.getFieldValue(keyField));
    }
    log.println();
  }



  static class Builder {
    private final Node node;
    private DataSource dataSource;
    private Integer tableNamePrefix;
    private KeyMaps keyMaps;
    private Logger log;
    private FieldLevelMutators mutators = FieldLevelMutators.NONE;
    private TupleLevelMutator tupleLevelMutator;
    private boolean debug = false;
    private int copyNumber = 1;
    private int maxRecsPerInsert = DEFAULT_MAX_RECS_PER_INSERT;
    private boolean insertMode = true;
    private boolean pureCopyMode = false;
    private Logger outputFile = new Logger("dataset-cloner.sql");

    private Builder(Node node) {
      this.node = requireNonNull(node, "node cannot be null");
    }

    Builder with(Consumer<Builder> consumer) {
      requireNonNull(consumer, "consumer cannot be null");
      consumer.accept(this);
      return this;
    }

    Builder withDataSource(DataSource dataSource) {
      this.dataSource = requireNonNull(dataSource, "dataSource cannot be null");
      return this;
    }

    Builder withTableNamePrefix(int tableNamePrefix) {
      this.tableNamePrefix = tableNamePrefix;
      return this;
    }

    Builder withKeyMaps(KeyMaps keyMaps) {
      this.keyMaps = requireNonNull(keyMaps, "keyMaps cannot be null");
      return this;
    }

    Builder withLog(Logger log) {
      this.log = requireNonNull(log, "log cannot be null");
      return this;
    }

    Builder withPureCopyMode(boolean pureCopyMode) {
      // if set to true, data in source tables will be copied exactly as is, without making
      // any modifications to the field values. Since this will cause unique constraint violations,
      // insert mode will automatically be set to false to prevent the script from actually being
      // executed.
      this.pureCopyMode = pureCopyMode;
      if (pureCopyMode) {
        this.insertMode = false;
      } else {
        this.insertMode = true;
      }
      return this;
    }

    Builder withOutputFile(Logger outputFile) {
      this.outputFile = requireNonNull(outputFile, "outputFile cannot be null");
      return this;
    }


    Builder withFieldLevelMutators(FieldLevelMutators mutators) {
      if (this.mutators == null) {
        this.mutators = FieldLevelMutators.NONE;
      }
      this.mutators = mutators;
      return this;
    }

    Builder withTupleLevelMutator(TupleLevelMutator mutator) {
      this.tupleLevelMutator = mutator;
      return this;
    }

    Builder withTupleLevelMutator(Consumer<TupleLevelMutator.Context> mutator) {
      requireNonNull(mutator, "mutator cannot be null");
      this.tupleLevelMutator = TupleLevelMutator.create(mutator);
      return this;
    }

    Builder withDebug(boolean debug) {
      this.debug = debug;
      return this;
    }

    Builder withCopyNumber(int copyNumber) {
      this.copyNumber = copyNumber;
      return this;
    }

    Builder withMaxRecsPerInsert(int maxRecsPerInsert) {
      checkArgument(maxRecsPerInsert > 0, "expected a positive integer for maxRecsPerInsert");
      this.maxRecsPerInsert = maxRecsPerInsert;
      return this;
    }

    CloneOperation build() {
      requireNonNull(dataSource, "dataSource cannot be null");
      requireNonNull(tableNamePrefix, "tableNamePrefix cannot be null");
      requireNonNull(keyMaps, "keyMaps cannot be null");
      return new CloneOperation(this);

    }


  }
}
