package com.resolute.dataset.cloner.engine;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static com.resolute.dataset.cloner.utils.Constants.DEFAULT_MAX_RECS_PER_DELETE;
import static com.resolute.dataset.cloner.utils.Constants.DEFAULT_MAX_RECS_PER_INSERT;
import static com.resolute.jdbc.simple.QueryHandler.toList;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

import java.io.File;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.resolute.database.crawler.model.Edge;
import com.resolute.database.crawler.model.ForeignKeyField;
import com.resolute.database.crawler.model.Graph;
import com.resolute.database.crawler.model.Node;
import com.resolute.dataset.cloner.log.Logger;
import com.resolute.dataset.cloner.utils.Constants;
import com.resolute.dataset.cloner.utils.Key;
import com.resolute.dataset.cloner.utils.KeyMaps;
import com.resolute.dataset.cloner.utils.TempTables;
import com.resolute.jdbc.simple.DataAccessException;
import com.resolute.jdbc.simple.JdbcStatementFactory;

public class DatasetClonerHelper {


  private final Logger log;
  private final Graph schemaGraph;
  private final List<SourceSet> sourceSets;
  private final int tableNamePrefix;
  private final Set<String> superclassWarningsHandled;
  private final DataSource dataSource;
  private final JdbcStatementFactory statementFactory;
  private final boolean debug;
  private final int numberOfCopies;
  private final boolean pureCopyMode;
  private final Logger outputFile;
  private final LifecycleListeners.BeforeAllListener beforeAllListener;
  private final LifecycleListeners.AfterAllListener afterAllListener;
  private final LifecycleListeners.BeforeEachListener beforeEachListener;
  private final LifecycleListeners.AfterEachListener afterEachListener;
  private final int maxRecsPerInsert;
  private final int maxRecsPerDelete;
  private KeyMaps keyMaps;


  public static Builder builder(Graph schemaGraph) {
    return new Builder(schemaGraph);
  }

  private DatasetClonerHelper(Builder builder) {
    this.log = builder.logger != null ? builder.logger : new Logger(builder.logFile);
    this.schemaGraph = builder.schemaGraph;
    this.sourceSets = ImmutableList.copyOf(builder.sourceSets);
    this.tableNamePrefix = (int) (Math.random() * 100_000_000);
    this.superclassWarningsHandled = ImmutableSet.copyOf(builder.superclassWarningsHandled);
    this.dataSource = builder.dataSource;
    this.statementFactory = JdbcStatementFactory.getInstance(builder.dataSource);
    this.debug = builder.debug;
    this.numberOfCopies = builder.numberOfCopies;
    this.pureCopyMode = builder.pureCopyMode;
    this.outputFile = builder.outputFile;
    this.beforeAllListener = builder.beforeAllListener;
    this.afterAllListener = builder.afterAllListener;
    this.beforeEachListener = builder.beforeEachListener;
    this.afterEachListener = builder.afterEachListener;
    this.maxRecsPerInsert = builder.maxRecsPerInsert;
    this.maxRecsPerDelete = builder.maxRecsPerDelete;
    log.println(">>>>TABLE NAME PREFIX: " + tableNamePrefix);
  }

  int getTableNamePrefix() {
    return tableNamePrefix;
  }

  List<SourceSet> getSourceSets() {
    return sourceSets;
  }

  Map<Node, List<Node>> getOrphanedSuperclassNodes() {
    Set<Node> allNodes = sourceSets.stream()
        .flatMap(sourceSet -> sourceSet.getGraph().getNodes().stream())
        .collect(toSet());

    Set<Node> subclassNodes = allNodes.stream()
        .map(node -> schemaGraph.getNode(node.getName()).get())
        .filter(node -> node.isSubclass())
        .collect(toCollection(LinkedHashSet::new));

    Map<Node, List<Node>> superClassRootMap = subclassNodes.stream()
        .collect(groupingBy(Node::getSuperclassRoot, LinkedHashMap::new, Collectors.toList()));

    Set<Node> orphanedSuperClassRoots = superClassRootMap.keySet().stream()
        .filter(node -> !allNodes.contains(node))
        .collect(toSet());

    return superClassRootMap.entrySet().stream()
        .filter(e -> orphanedSuperClassRoots.contains(e.getKey()))
        .filter(e -> !superclassWarningsHandled.contains(e.getKey().getName()))
        .collect(toMap(Entry::getKey, Entry::getValue, (u, v) -> {
          throw new IllegalArgumentException("Duplicate key: " + u);
        }, LinkedHashMap::new));

  }

  KeyMaps getKeyMaps() {
    return keyMaps;
  }

  void execute() {
    long startTime = System.currentTimeMillis();
    if (beforeAllListener != null) {
      beforeAllListener.onEvent();
    }
    try {
      if (!computeDataToBeCloned()) {
        return;
      }
      if (pureCopyMode) {
        System.out.println(
            "The application is in 'pure copy' mode, so no actual clones will be inserted. "
                + "Instead a SQL script will be generated at the following location: "
                + outputFile.getFile().getAbsolutePath());
        outputFile.println(Constants.SET_SESSION_REPLICATION_ROLE_SQL);
        outputFile.println();
      }
      cloneData();
    } finally {
      cleanup();
      if (afterAllListener != null) {
        afterAllListener.onEvent(System.currentTimeMillis() - startTime);
      }
    }
  }

  boolean computeDataToBeCloned() {
    boolean someDataToBeCloned = false;
    for (int i = 0; i < sourceSets.size(); i++) {
      Graph graph = computeGraph(i, sourceSets.get(i));
      List<Node> nodes = graph.topologicalSort();
      someDataToBeCloned =
          computeDataToBeCloned(i + 1, sourceSets.get(i).getSelectSpec(), nodes)
              || someDataToBeCloned;
    }
    return someDataToBeCloned;
  }

  void cloneData() {
    // Merge the graphs from all source sets to get one topologically sorted list of nodes
    Graph graph = sourceSets.get(0).getGraph();
    for (int i = 1; i < sourceSets.size(); i++) {
      graph = graph.union(sourceSets.get(i).getGraph());
    }
    List<Node> nodes = graph.topologicalSort();

    // Merge the FieldMutators from all source sets into a single map
    Map<String, FieldLevelMutators> combinedFieldLevelMutatorsByTable = Maps.newHashMap();
    for (int i = 0; i < sourceSets.size(); i++) {
      for (String tableName : sourceSets.get(i).getTablesWithFieldLevelMutators()) {
        combinedFieldLevelMutatorsByTable.put(tableName,
            sourceSets.get(i).getFieldLevelMutators(tableName).get());
      }
    }

    // Merge the TupleMutators from all source sets into a single map
    Map<String, TupleLevelMutator> combinedTupleLevelMutatorsByTable = Maps.newHashMap();
    for (int i = 0; i < sourceSets.size(); i++) {
      for (String tableName : sourceSets.get(i).getTablesWithTupleLevelMutators()) {
        combinedTupleLevelMutatorsByTable.put(tableName,
            sourceSets.get(i).getTupleLevelMutator(tableName).get());
      }
    }


    for (int copyNumber = 1; copyNumber <= numberOfCopies; copyNumber++) {
      long startTime = System.currentTimeMillis();
      if (beforeEachListener != null) {
        beforeEachListener.onEvent(copyNumber);
      }
      try {
        this.keyMaps = null;
        this.keyMaps = new KeyMaps();
        // Loop through the nodes and perform a clone operation for each
        for (Node node : nodes) {
          FieldLevelMutators fieldLevelMutators = combinedFieldLevelMutatorsByTable
              .computeIfAbsent(node.getName(), tableName -> FieldLevelMutators.builder().build());
          TupleLevelMutator tupleLevelMutator =
              combinedTupleLevelMutatorsByTable.get(node.getName());
          cloneData(node, fieldLevelMutators, tupleLevelMutator, keyMaps, copyNumber);
        }
      } finally {
        if (afterEachListener != null) {
          afterEachListener.onEvent(System.currentTimeMillis() - startTime, copyNumber);
        }
      }
    }
  }

  void cleanup() {
    Graph graph = sourceSets.get(0).getGraph();
    for (int i = 1; i < sourceSets.size(); i++) {
      graph = graph.union(sourceSets.get(i).getGraph());
    }

    RollbackOperation operation = RollbackOperation.forGraph(graph)
        .withDataSource(dataSource)
        .withTableNamePrefix(tableNamePrefix)
        .withDebug(debug)
        .withMaxRecsPerDelete(maxRecsPerDelete)
        .build();

    operation.execute();
  }

  void rollback() {
    // Merge the graphs from all source sets to get one topologically sorted list of nodes
    Graph graph = sourceSets.get(0).getGraph();
    for (int i = 1; i < sourceSets.size(); i++) {
      graph = graph.union(sourceSets.get(i).getGraph());
    }
    List<Node> nodes = graph.topologicalSort().stream()
        .sorted(Comparator.reverseOrder())
        .collect(toList());

    Map<String, Set<Key>> keysInserted = Maps.newHashMap();
    for (Node node : nodes) {
      Set<Key> keySet = keysInserted.computeIfAbsent(node.getName(), t -> Sets.newLinkedHashSet());
      keySet.addAll(keyMaps.getTargetKeys(node.getName()));
    }

    RollbackOperation operation = RollbackOperation.forGraph(graph)
        .withDataSource(dataSource)
        .withKeysInserted(keysInserted)
        .withDebug(debug)
        .build();

    operation.execute();

    // rollback(nodes, keysInserted);
  }

  private Graph computeGraph(int idx, SourceSet sourceSet) {
    Graph graph = sourceSet.getGraph();
    for (int i = 0; i < idx; i++) {
      graph = graph.difference(sourceSets.get(i).getGraph(), false);
    }
    return graph;
  }

  private boolean computeDataToBeCloned(int sourceSetIndex, RootNodeSelectSpecification selectSpec,
      List<Node> nodes) {
    StringBuilder buf = new StringBuilder();
    boolean firstNode = true;
    boolean dataToClone = true;
    for (Node node : nodes) {
      if (firstNode) {
        firstNode = false;
        if (shortCircuitFunctionCall(selectSpec, node)) {
          System.out.println(
              "The root select statement for the source set rooted at " + node.getName()
                  + " returned no rows, so there is nothing to clone!");
          dataToClone = false;
        }
        createTempTable(buf, node);
        insertIntoTempRootTable(selectSpec, buf, node);
        if (node.hasUnaryAssociation()) {
          insertIntoTempTable(buf, node);
        }
      } else if (!node.getPrimaryKey().isEmpty()) {
        createTempTable(buf, node);
        insertIntoTempTable(buf, node);
      }
    }
    if (this.debug) {
      System.out.println("----------------------------------------------");
      System.out.println(
          String.format("SOURCE SET %d - START SQL FOR DATA TO BE CLONED", sourceSetIndex));
      System.out.println("----------------------------------------------");
      System.out.print(buf.toString());
      System.out.println("----------------------------------------------");
      System.out.println(
          String.format("SOURCE SET %d - END SQL FOR DATA TO BE CLONED", sourceSetIndex));
      System.out.println("----------------------------------------------");
    }
    statementFactory.newStatement()
        .withSql(buf.toString())
        .withErrorMessage("A problem occurred while attempting to compute the data to be cloned")
        .execute();
    return dataToClone;
  }

  private void cloneData(Node node, FieldLevelMutators fieldLevelMutators,
      TupleLevelMutator tupleLevelMutator, KeyMaps keyMaps, int copyNumber) {
    CloneOperation operation = CloneOperation.forNode(node)
        .withDataSource(dataSource)
        .withTableNamePrefix(tableNamePrefix)
        .withFieldLevelMutators(fieldLevelMutators)
        .withTupleLevelMutator(tupleLevelMutator)
        .withKeyMaps(keyMaps)
        .withCopyNumber(copyNumber)
        .withDebug(debug)
        .withLog(log)
        .withPureCopyMode(pureCopyMode)
        .withOutputFile(outputFile)
        .withMaxRecsPerInsert(maxRecsPerInsert)
        .build();

    operation.execute();
  }

  private boolean shortCircuitFunctionCall(RootNodeSelectSpecification selectSpec, Node node) {
    if (node.getPrimaryKey().isEmpty()) {
      throw new DataAccessException("Expected the root table to have a primary key");
    }
    String firstPrimaryKeyField = node.getPrimaryKey().get(0);
    List<String> result = statementFactory.newStatement()
        .withSql(selectSpec.getRootSelectStatement(tableNamePrefix))
        .withErrorMessage(
            "A problem occurred while attempting to execute the root select statement")
        .executeQuery(toList(rs -> String.valueOf(rs.getObject(firstPrimaryKeyField))));
    if (result.isEmpty()) {
      return true;
    }
    return false;
  }

  private void createTempTable(StringBuilder buf, Node node) {
    buf.append("    DROP TABLE IF EXISTS " + getTempTableName(node) + ";\n");
    buf.append(
        "    CREATE TABLE " + getTempTableName(node) + " (\n");
    boolean firstField = true;
    for (String keyField : node.getPrimaryKey()) {
      String dataType = node.getField(keyField).get().getDataType().toUpperCase();
      if (firstField) {
        firstField = false;
      } else {
        buf.append(",\n");
      }
      buf.append("      " + keyField + " "
          + dataType + " NOT NULL");
    }
    buf.append(",\n      PRIMARY KEY (");
    firstField = true;
    for (String keyField : node.getPrimaryKey()) {
      if (firstField) {
        firstField = false;
      } else {
        buf.append(", ");
      }
      buf.append(keyField);
    }

    buf.append(")\n    );\n\n");

    if (node.hasUnaryAssociation()) {
      buf.append("    DROP TABLE IF EXISTS " + getTempUnaryTableName(node) + ";\n");
      buf.append("    CREATE TABLE " + getTempUnaryTableName(node) + " (\n");
      firstField = true;
      for (String keyField : node.getPrimaryKey()) {
        String dataType = node.getField(keyField).get().getDataType().toUpperCase();
        if (firstField) {
          firstField = false;
        } else {
          buf.append(",\n");
        }
        buf.append("      " + keyField + " " + dataType + " NOT NULL");
      }
      buf.append(",\n      PRIMARY KEY (");
      firstField = true;
      for (String keyField : node.getPrimaryKey()) {
        if (firstField) {
          firstField = false;
        } else {
          buf.append(", ");
        }
        buf.append(keyField);
      }

      buf.append(")\n    );\n\n");
    }
  }

  private void insertIntoTempRootTable(RootNodeSelectSpecification selectSpec, StringBuilder buf,
      Node node) {
    buf.append("    INSERT INTO " + getTempTableName(node) + " (");
    boolean firstField = true;
    for (String keyField : node.getPrimaryKey()) {
      if (firstField) {
        firstField = false;
      } else {
        buf.append(", ");
      }
      buf.append(keyField);
    }
    buf.append(")\n");
    buf.append("      ").append(selectSpec.getRootSelectStatement(tableNamePrefix)).append(";\n\n");
  }

  private void insertIntoTempTable(StringBuilder buf, Node node) {
    List<Edge> toEdges = node.getToEdges().stream()
        .sorted((e1, e2) -> e1.getFrom().getName().compareTo(e2.getFrom().getName()))
        .collect(toList());
    for (Edge toEdge : toEdges) {
      if (toEdge.isUnaryAssociation()) {
        continue;
      }
      buf.append("    INSERT INTO " + getTempTableName(node) + " (");
      boolean firstField = true;
      for (String keyField : node.getPrimaryKey()) {
        if (firstField) {
          firstField = false;
        } else {
          buf.append(", ");
        }
        buf.append(keyField);
      }
      buf.append(")\n");
      buf.append("      SELECT ");
      firstField = true;
      for (String keyField : node.getPrimaryKey()) {
        if (firstField) {
          firstField = false;
        } else {
          buf.append(", ");
        }
        buf.append("t1." + keyField);
      }
      buf.append("\n        FROM " + node.getName() + " t1");
      int tCount = 2;

      buf.append("\n        JOIN " + getTempTableName(toEdge.getFrom()) + " t"
          + tCount + " ON ");
      firstField = true;
      for (ForeignKeyField keyField : toEdge.getForeignKey().getFields()) {
        if (firstField) {
          firstField = false;
        } else {
          buf.append(" AND ");
        }
        buf.append("t1." + keyField.getToField() + " = t" + tCount + "." + keyField.getFromField());
      }
      tCount++;
      buf.append("\n      ON CONFLICT DO NOTHING;\n\n");
    }


    if (node.hasUnaryAssociation() && !node.getFromEdges().isEmpty()) {
      for (Edge toEdge : toEdges) {
        if (!toEdge.isUnaryAssociation()) {
          continue;
        }
        buf.append("    WITH RECURSIVE children AS (\n");
        buf.append("      SELECT ");
        boolean firstField = true;
        for (String keyField : node.getPrimaryKey()) {
          if (firstField) {
            firstField = false;
          } else {
            buf.append(", ");
          }
          buf.append(keyField);
        }
        buf.append("\n        FROM " + getTempTableName(node) + "\n");
        buf.append("      UNION\n");
        buf.append("      SELECT ");
        firstField = true;
        for (String keyField : node.getPrimaryKey()) {
          if (firstField) {
            firstField = false;
          } else {
            buf.append(", ");
          }
          buf.append("t1." + keyField);
        }
        buf.append("\n        FROM " + node.getName() + " t1\n");
        buf.append("        INNER JOIN children c ON ");
        firstField = true;
        for (ForeignKeyField keyField : toEdge.getForeignKey().getFields()) {
          if (firstField) {
            firstField = false;
          } else {
            buf.append(" AND ");
          }
          buf.append(
              "c." + keyField.getFromField() + " = t1." + keyField.getToField());
        }
        buf.append("\n    ) INSERT INTO " + getTempUnaryTableName(node) + " (");
        firstField = true;
        for (String keyField : node.getPrimaryKey()) {
          if (firstField) {
            firstField = false;
          } else {
            buf.append(", ");
          }
          buf.append(keyField);
        }
        buf.append(")\n");
        buf.append("      SELECT ");
        firstField = true;
        for (String keyField : node.getPrimaryKey()) {
          if (firstField) {
            firstField = false;
          } else {
            buf.append(", ");
          }
          buf.append(keyField);
        }
        buf.append("\n        FROM children");
        buf.append("\n      ON CONFLICT DO NOTHING;\n\n");
      }

      buf.append("    DELETE FROM " + getTempTableName(node) + ";\n\n");
      buf.append("    INSERT INTO " + getTempTableName(node) + " (");
      boolean firstField = true;
      for (String keyField : node.getPrimaryKey()) {
        if (firstField) {
          firstField = false;
        } else {
          buf.append(", ");
        }
        buf.append(keyField);
      }
      buf.append(")\n");
      buf.append("      SELECT ");
      firstField = true;
      for (String keyField : node.getPrimaryKey()) {
        if (firstField) {
          firstField = false;
        } else {
          buf.append(", ");
        }
        buf.append(keyField);
      }
      buf.append("\n        FROM " + getTempUnaryTableName(node)
          + "\n      ON CONFLICT DO NOTHING;\n\n");

    }
  }

  private String getTempTableName(Node node) {
    return TempTables.getTempTableName(tableNamePrefix, node.getName());
  }


  private String getTempUnaryTableName(Node node) {
    return TempTables.getTempUnaryTableName(tableNamePrefix, node.getName());
  }

  public static class Builder implements SourceSetsBuilder {

    private Graph schemaGraph;
    private DataSource dataSource;
    private File logFile = new File("dataset-cloner.log");
    private Logger logger;
    private List<SourceSet> sourceSets = Lists.newArrayList();
    private Set<String> superclassWarningsHandled = Sets.newHashSet();
    private boolean debug = false;
    private int numberOfCopies = 1;
    private boolean pureCopyMode = false;
    private Logger outputFile = new Logger("dataset-cloner.sql");
    private int maxRecsPerInsert = DEFAULT_MAX_RECS_PER_INSERT;
    private int maxRecsPerDelete = DEFAULT_MAX_RECS_PER_DELETE;
    private LifecycleListeners.BeforeAllListener beforeAllListener;
    private LifecycleListeners.AfterAllListener afterAllListener;
    private LifecycleListeners.BeforeEachListener beforeEachListener;
    private LifecycleListeners.AfterEachListener afterEachListener;


    private Builder(Graph schemaGraph) {
      this.schemaGraph = requireNonNull(schemaGraph);
    }

    Builder withDataSource(DataSource dataSource) {
      this.dataSource = requireNonNull(dataSource, "dataSource cannot be null");
      return this;
    }

    Builder withLogFile(String logFile) {
      requireNonNull(logFile, "logFile cannot be null");
      return withLogFile(new File(logFile));
    }

    Builder withLogFile(File logFile) {
      this.logFile = requireNonNull(logFile, "logFile cannot be null");
      return this;
    }

    Builder withLogger(Logger logger) {
      this.logger = requireNonNull(logger, "logger cannot be null");
      return this;
    }

    Builder withOutputFile(String logFile) {
      requireNonNull(logFile, "logFile cannot be null");
      return withOutputFile(new File(logFile));
    }

    Builder withOutputFile(File logFile) {
      requireNonNull(logFile, "logFile cannot be null");
      return withOutputFile(new Logger(logFile));
    }

    Builder withOutputFile(Logger outputFile) {
      this.outputFile = requireNonNull(outputFile, "outputFile cannot be null");
      return this;
    }

    Builder withPureCopyMode(boolean pureCopyMode) {
      this.pureCopyMode = pureCopyMode;
      return this;
    }

    @Override
    public Builder withSourceSet(Consumer<SourceSet.Builder> sourceSetConfigurator) {
      requireNonNull(sourceSetConfigurator, "sourceSetConfigurator cannot be null");
      SourceSet.Builder builder = SourceSet.builder(schemaGraph);
      sourceSetConfigurator.accept(builder);
      this.sourceSets.add(builder.build());
      return this;
    }

    Builder withSuperclassWarningsHandled(Set<String> superclassTables) {
      this.superclassWarningsHandled =
          requireNonNull(superclassTables, "superclassTables cannot be null");
      return this;
    }

    Builder withDebug(boolean debug) {
      this.debug = debug;
      return this;
    }

    Builder withNumberOfCopies(int numberOfCopies) {
      checkArgument(numberOfCopies > 0, "expected at least one copy");
      this.numberOfCopies = numberOfCopies;
      return this;
    }

    Builder withMaxRecsPerInsert(int maxRecsPerInsert) {
      checkArgument(maxRecsPerInsert > 0, "expected a positive integer for maxRecsPerInsert");
      this.maxRecsPerInsert = maxRecsPerInsert;
      return this;
    }

    Builder withMaxRecsPerDelete(int maxRecsPerDelete) {
      checkArgument(maxRecsPerDelete > 0, "expected a positive integer for maxRecsPerDelete");
      this.maxRecsPerDelete = maxRecsPerDelete;
      return this;
    }

    Builder withBeforeAllListener(LifecycleListeners.BeforeAllListener beforeAllListener) {
      this.beforeAllListener = beforeAllListener;
      return this;
    }

    Builder withAfterAllListener(LifecycleListeners.AfterAllListener afterAllListener) {
      this.afterAllListener = afterAllListener;
      return this;
    }

    Builder withBeforeEachListener(
        LifecycleListeners.BeforeEachListener beforeEachListener) {
      this.beforeEachListener = beforeEachListener;
      return this;
    }

    Builder withAfterEachListener(LifecycleListeners.AfterEachListener afterEachListener) {
      this.afterEachListener = afterEachListener;
      return this;
    }

    DatasetClonerHelper build() {
      requireNonNull(dataSource, "dataSource cannot be null");
      checkState(sourceSets.size() > 0, "expected at least one sourceSet");
      return new DatasetClonerHelper(this);
    }

  }
}
