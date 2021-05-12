package com.resolute.database.crawler.model;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Comparators {
  private static final Comparator<Edge> FROM_EDGE_COMPARATOR = (e1, e2) -> {
    int result = e1.getTo().getName().compareTo(e2.getTo().getName());
    if (result == 0) {
      result = compareByForeignKey(e1, e2);
    }
    return result;
  };

  private static final Comparator<Edge> TO_EDGE_COMPARATOR = (e1, e2) -> {
    int result = e1.getFrom().getName().compareTo(e2.getFrom().getName());
    if (result == 0) {
      result = compareByForeignKey(e1, e2);
    }
    return result;
  };

  public static Comparator<Edge> fromEdgeComparator() {
    return FROM_EDGE_COMPARATOR;
  }

  public static Comparator<Edge> toEdgeComparator() {
    return TO_EDGE_COMPARATOR;
  }

  private static int compareByForeignKey(Edge e1, Edge e2) {
    int result = 0;
    List<String> e1ForeignKeyFields = e1.getForeignKey().getFields().stream()
        .map(ForeignKeyField::getToField)
        .collect(Collectors.toList());
    List<String> e2ForeignKeyFields = e2.getForeignKey().getFields().stream()
        .map(ForeignKeyField::getToField)
        .collect(Collectors.toList());
    for (int i = 0; i < e1ForeignKeyFields.size(); i++) {
      result = e1ForeignKeyFields.get(i).compareTo(e2ForeignKeyFields.get(i));
      if (result != 0) {
        break;
      }
    }
    return result;
  }

}
