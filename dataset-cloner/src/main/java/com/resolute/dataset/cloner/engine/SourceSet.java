package com.resolute.dataset.cloner.engine;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.resolute.database.crawler.model.Graph;
import com.resolute.dataset.cloner.utils.Key;

public class SourceSet {

  private final RootNodeSelectSpecification selectSpec;
  private final Graph graph;
  private final Map<String, FieldLevelMutators> fieldValueMutatorsByTable;
  private final Map<String, TupleLevelMutator> tupleValueMutatorsByTable;

  static Builder builder(Graph schemaGraph) {
    return new Builder(schemaGraph);
  }

  private SourceSet(Builder builder) {
    this.selectSpec = builder.selectSpec;
    this.graph = builder.graph;
    Map<String, FieldLevelMutators> temp = Maps.newHashMap();
    for (Entry<String, FieldLevelMutators.Builder> entry : builder.fieldLevelMutatorsByTable
        .entrySet()) {
      temp.put(entry.getKey(), entry.getValue().build());
    }
    this.fieldValueMutatorsByTable = ImmutableMap.copyOf(temp);
    this.tupleValueMutatorsByTable = ImmutableMap.copyOf(builder.tupleLevelMutatorsByTable);
  }

  public RootNodeSelectSpecification getSelectSpec() {
    return selectSpec;
  }

  public Graph getGraph() {
    return graph;
  }

  public Set<String> getTablesWithFieldLevelMutators() {
    return fieldValueMutatorsByTable.keySet();
  }

  public Optional<FieldLevelMutators> getFieldLevelMutators(String tableName) {
    requireNonNull(tableName, "tableName cannot be null");
    return Optional.ofNullable(fieldValueMutatorsByTable.get(tableName));
  }

  public Set<String> getTablesWithTupleLevelMutators() {
    return tupleValueMutatorsByTable.keySet();
  }

  public Optional<TupleLevelMutator> getTupleLevelMutator(String tableName) {
    requireNonNull(tableName, "tableName cannot be null");
    return Optional.ofNullable(tupleValueMutatorsByTable.get(tableName));
  }

  public static class Builder {
    private Graph schemaGraph;
    private RootNodeSelectSpecification selectSpec;
    private Graph graph;
    private String rootTableName;
    private final Map<String, FieldLevelMutators.Builder> fieldLevelMutatorsByTable =
        Maps.newHashMap();
    private final Map<String, TupleLevelMutator> tupleLevelMutatorsByTable = Maps.newHashMap();

    private Builder(Graph schemaGraph) {
      this.schemaGraph = requireNonNull(schemaGraph, "schemaGraph cannot be null");
    }

    public Builder withRootSelectStatement(String tableName, String rootSelectStatement) {
      this.rootTableName = requireNonNull(tableName, "tableName cannot be null");
      requireNonNull(rootSelectStatement, "rootSelectStatement cannot be null");
      this.selectSpec = new SqlRootNodeSelectSpecification(rootSelectStatement);
      this.rootTableName = tableName;
      return this;
    }

    public Builder withRootSelectStatement(String tableName, Key key) {
      this.rootTableName = requireNonNull(tableName, "tableName cannot be null");
      requireNonNull(key, "key cannot be null");
      this.selectSpec =
          new KeyBasedRootNodeSelectSpecification(schemaGraph, tableName, key);
      return this;
    }

    public Builder withRootSelectStatement(String tableName,
        RootNodeSelectSpecification selectStatementGenerator) {
      this.rootTableName = requireNonNull(tableName, "tableName cannot be null");
      requireNonNull(selectStatementGenerator, "selectStatementGenerator cannot be null");
      this.selectSpec = selectStatementGenerator;
      this.rootTableName = tableName;
      return this;
    }

    public Builder withGraph(Graph graph) {
      this.graph = requireNonNull(graph, "graph cannot be null");
      return this;
    }

    public Builder withDefaultFieldLevelMutator(String tableName, String fieldName) {
      return withFieldLevelMutator(tableName, fieldName, FieldLevelMutator.DEFAULT);
    }

    public Builder withFieldLevelMutator(String tableName, String fieldName,
        FieldLevelMutator mutator) {
      requireNonNull(tableName, "tableName cannot be null");
      requireNonNull(fieldName, "fieldName cannot be null");
      requireNonNull(mutator, "mutator cannot be null");
      FieldLevelMutators.Builder mutatorBuilder =
          fieldLevelMutatorsByTable.computeIfAbsent(tableName, t -> FieldLevelMutators.builder());
      mutatorBuilder.withFieldLevelMutator(fieldName, mutator);
      return this;
    }

    public Builder withTupleLevelMutator(String tableName,
        Consumer<TupleLevelMutator.Context> mutator) {
      requireNonNull(tableName, "tableName cannot be null");
      requireNonNull(mutator, "mutator cannot be null");
      TupleLevelMutator tupleLevelMutator = TupleLevelMutator.create(mutator);
      this.tupleLevelMutatorsByTable.put(tableName, tupleLevelMutator);
      return this;
    }

    SourceSet build() {
      requireNonNull(selectSpec, "selectSpec cannot be null");
      if (graph == null) {
        graph = schemaGraph.getSubgraphReachableFrom(rootTableName);
      }
      checkArgument(graph.getNodes().size() > 0, "Expected a graph with at least one node");
      String actualRoot = graph.topologicalSort().get(0).getName();
      checkArgument(actualRoot.equals(rootTableName),
          String.format("expected graph to be rooted at table '%s', but was instead rooted at '%s'",
              rootTableName, actualRoot));
      return new SourceSet(this);
    }
  }

}
