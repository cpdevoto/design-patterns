package com.resolute.dataset.cloner.log;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.resolute.database.crawler.model.Graph;
import com.resolute.database.crawler.model.Node;
import com.resolute.dataset.cloner.utils.Key;

public class LogFile {
  private final Integer tableNamePrefix;
  private final Map<String, Set<Key>> keysInserted;

  static Builder builder(Graph graph, int executionThreshold, Consumer<LogFile> consumer) {
    return new Builder(graph, executionThreshold, consumer);
  }

  private LogFile(Builder builder) {
    this.tableNamePrefix = builder.getTableNamePrefix();
    Map<String, Set<Key>> temp = Maps.newLinkedHashMap();
    for (Entry<String, Set<Key>> entry : builder.getKeysInserted().entrySet()) {
      temp.put(entry.getKey(), ImmutableSet.copyOf(entry.getValue()));
    }
    this.keysInserted = ImmutableMap.copyOf(temp);
  }

  public Integer getTableNamePrefix() {
    return tableNamePrefix;
  }

  public Map<String, Set<Key>> getKeysInserted() {
    return keysInserted;
  }

  static class Builder {
    private final Graph graph;
    private final int executionThreshold;
    private final Consumer<LogFile> consumer;
    private int numKeys = 0;
    private Integer tableNamePrefix;
    private Map<String, Set<Key>> keysInserted = Maps.newLinkedHashMap();
    private String currentTable;

    private Builder(Graph graph, int executionThreshold, Consumer<LogFile> consumer) {
      this.graph = requireNonNull(graph, "graph cannot be null");
      this.consumer = requireNonNull(consumer, "consumer cannot be null");
      checkArgument(executionThreshold > 0, "expected a positive integer executionThreshold");
      this.executionThreshold = executionThreshold;
    }

    public String getCurrentTable() {
      checkState(currentTable != null, "currentTable cannot be null");
      return currentTable;
    }

    public Optional<List<String>> getPrimaryKey() {
      checkState(currentTable != null, "currentTable cannot be null");
      Optional<Node> node = graph.getNode(currentTable);
      return node.map(n -> Optional.of(n.getPrimaryKey())).orElse(Optional.empty());
    }

    public Builder putTableNamePrefix(int tableNamePrefix) {
      this.tableNamePrefix = tableNamePrefix;
      return this;
    }

    public Builder setCurrentTable(String currentTable) {
      this.currentTable = requireNonNull(currentTable, "currentTable cannot be null");
      return this;
    }

    public Builder putKey(Key key) {
      requireNonNull(key, "key cannot be null");
      checkState(currentTable != null, "currentTable cannot be null");
      Set<Key> keys = keysInserted.computeIfAbsent(currentTable, t -> Sets.newLinkedHashSet());
      keys.add(key);
      numKeys += 1;
      if (numKeys >= executionThreshold) {
        LogFile logFile = new LogFile(this);
        consumer.accept(logFile);
        numKeys = 0;
        tableNamePrefix = null;
        keysInserted = Maps.newLinkedHashMap();
      }
      return this;
    }

    public Integer getTableNamePrefix() {
      return tableNamePrefix;
    }

    public Map<String, Set<Key>> getKeysInserted() {
      return keysInserted;
    }


    LogFile build() {
      return new LogFile(this);
    }

  }

}
