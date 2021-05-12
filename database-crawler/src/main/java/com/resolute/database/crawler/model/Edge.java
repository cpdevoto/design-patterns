package com.resolute.database.crawler.model;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.stream.Collectors;

public class Edge {
  private final Node from;
  private final Node to;
  private final ForeignKey foreignKey;

  Edge(Node from, Node to, ForeignKey foreignKey) {
    this.from = requireNonNull(from, "from cannot be null");
    this.to = requireNonNull(to, "to cannot be null");
    this.foreignKey = requireNonNull(foreignKey, "foreignKey");
    this.from.addFromEdge(this);
    this.to.addToEdge(this);
  }

  public Node getFrom() {
    return from;
  }

  public Node getTo() {
    return to;
  }

  public ForeignKey getForeignKey() {
    return foreignKey;
  }

  public boolean isUnaryAssociation() {
    return from.getName().equals(to.getName());
  }

  public boolean isInheritanceAssociation() {
    List<String> foreignKeyFields = foreignKey.getFields().stream()
        .map(ForeignKeyField::getToField)
        .collect(Collectors.toList());
    return to.getPrimaryKey().equals(foreignKeyFields);
  }

  @Override
  public String toString() {
    return "Edge [from=" + from.getName() + ", to=" + to.getName() + ", foreignKeys=" + foreignKey
        + "]";
  }


}
