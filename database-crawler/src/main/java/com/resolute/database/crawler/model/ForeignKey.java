package com.resolute.database.crawler.model;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class ForeignKey {

  private final List<ForeignKeyField> fields;

  public ForeignKey(List<ForeignKeyField> fields) {
    requireNonNull(fields, "fields cannot be null");
    Set<ForeignKeyField> visited = Sets.newHashSet();
    List<ForeignKeyField> temp = Lists.newArrayList();
    fields.forEach(field -> {
      if (visited.contains(field)) {
        return;
      }
      visited.add(field);
      temp.add(field);
    });
    this.fields = ImmutableList.copyOf(temp);
  }

  public List<ForeignKeyField> getFields() {
    return fields;
  }

  @Override
  public String toString() {
    return fields.toString();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((fields == null) ? 0 : fields.hashCode());
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
    ForeignKey other = (ForeignKey) obj;
    if (fields == null) {
      if (other.fields != null)
        return false;
    } else if (!fields.equals(other.fields))
      return false;
    return true;
  }

}
