package com.resolute.database.crawler.model;

import java.util.List;

import com.google.common.collect.ImmutableList;

public class UniqueIndex {

  private final String name;
  private final List<Field> fields;

  public UniqueIndex(String name, List<Field> fields) {
    this.name = name;
    this.fields = ImmutableList.copyOf(fields);
  }

  public String getName() {
    return name;
  }

  public List<Field> getFields() {
    return fields;
  }

}
