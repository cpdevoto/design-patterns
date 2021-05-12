package com.resolute.dataset.cloner.engine;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;

import java.util.List;
import java.util.Optional;

import com.resolute.database.crawler.model.Graph;
import com.resolute.database.crawler.model.Node;
import com.resolute.dataset.cloner.utils.Key;

class KeyBasedRootNodeSelectSpecification implements RootNodeSelectSpecification {

  private final String sql;

  KeyBasedRootNodeSelectSpecification(Graph schemaGraph, String tableName, Key key) {
    requireNonNull(schemaGraph, "schemaGraph cannot be null");
    requireNonNull(tableName, "tableName cannot be null");
    requireNonNull(key, "key cannot be null");
    Optional<Node> optNode = schemaGraph.getNode(tableName);
    checkArgument(optNode.isPresent(), "invalid tableName");
    List<String> keyFields = optNode.get().getPrimaryKey();
    checkArgument(keyFields.size() > 0, "the specified table has no primary key");
    List<String> fieldNames = key.getFieldNames();

    String fieldList = keyFields.stream().collect(joining(", "));
    checkArgument(keyFields.equals(fieldNames), String.format(
        "expected a key with the following fields: %s", keyFields.stream().collect(joining(", "))));

    String whereClause = fieldNames.stream()
        .map(fieldName -> fieldName + " = " + key.getFieldValue(fieldName))
        .collect(joining(" AND "));

    StringBuilder buf = new StringBuilder("SELECT ").append(fieldList).append(" FROM ")
        .append(tableName).append(" WHERE ").append(whereClause).append(";");

    this.sql = buf.toString();
  }

  @Override
  public String getRootSelectStatement(int tableNamePrefix) {
    return sql;
  }

}
