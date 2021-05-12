package com.resolute.database.crawler.model;

import static java.util.Objects.requireNonNull;

public class IgnoredEdge {
  private final String from;
  private final String to;
  private final ForeignKey foreignKey;

  public static IgnoredEdge create(Edge edge) {
    requireNonNull(edge, "edge cannot be null");
    return new IgnoredEdge(edge.getFrom().getName(), edge.getTo().getName(), edge.getForeignKey());
  }

  public IgnoredEdge(String from, String to, ForeignKey foreignKey) {
    this.from = requireNonNull(from, "from cannot be null");
    this.to = requireNonNull(to, "to cannot be null");
    this.foreignKey = requireNonNull(foreignKey, "foreignKey");
  }

  public String getFrom() {
    return from;
  }

  public String getTo() {
    return to;
  }

  public ForeignKey getForeignKey() {
    return foreignKey;
  }

  public boolean isUnaryAssociation() {
    return from.equals(to);
  }


  @Override
  public String toString() {
    return "IgnoredEdge [from=" + from + ", to=" + to + ", foreignKeys=" + foreignKey
        + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((foreignKey == null) ? 0 : foreignKey.hashCode());
    result = prime * result + ((from == null) ? 0 : from.hashCode());
    result = prime * result + ((to == null) ? 0 : to.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    IgnoredEdge other = (IgnoredEdge) obj;
    if (foreignKey == null) {
      if (other.foreignKey != null)
        return false;
    } else if (!foreignKey.equals(other.foreignKey))
      return false;
    if (from == null) {
      if (other.from != null)
        return false;
    } else if (!from.equals(other.from))
      return false;
    if (to == null) {
      if (other.to != null)
        return false;
    } else if (!to.equals(other.to))
      return false;
    return true;
  }


}
