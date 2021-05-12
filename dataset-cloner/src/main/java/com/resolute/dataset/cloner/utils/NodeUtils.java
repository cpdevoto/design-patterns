package com.resolute.dataset.cloner.utils;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.resolute.database.crawler.model.Edge;
import com.resolute.database.crawler.model.Field;
import com.resolute.database.crawler.model.Node;

public class NodeUtils {

  private static Set<String> DATA_TYPES_NOT_REQUIRING_QUOTES = ImmutableSet.of(
      "bigint", "boolean", "double precision", "integer", "money", "numeric", "smallint");

  public static Optional<Edge> getForeignKey(Node node, String fieldName) {
    requireNonNull(node, "node cannot be null");
    requireNonNull(fieldName, "fieldName cannot be null");
    return node.getToEdges().stream()
        .filter(edge -> {
          return edge.getForeignKey().getFields().stream()
              .filter(foreignKeyField -> foreignKeyField.getToField().equals(fieldName))
              .findAny()
              .isPresent();
        })
        .findFirst();
  }

  public static List<List<Field>> getUniqueIndeces(Node node, String fieldName) {
    requireNonNull(node, "node cannot be null");
    requireNonNull(fieldName, "fieldName cannot be null");
    return node.getUniqueIndeces().stream()
        .filter(index -> index.stream()
            .filter(indexField -> indexField.getName().equals(fieldName))
            .findAny()
            .isPresent())
        .collect(toList());
  }

  public static boolean hasPrimaryKeyField(Node node, List<Field> fields) {
    requireNonNull(node, "node cannot be null");
    requireNonNull(fields, "fields cannot be null");
    return fields.stream()
        .filter(field -> node.isPrimaryKeyField(field.getName()))
        .findAny()
        .isPresent();
  }

  public static boolean hasForeignKeyField(Node node, List<Field> fields) {
    requireNonNull(node, "node cannot be null");
    requireNonNull(fields, "fields cannot be null");
    return fields.stream()
        .filter(field -> node.isForeignKeyField(field.getName()))
        .findAny()
        .isPresent();
  }

  public static boolean isPartOfUnaryForeignKey(Node node, String fieldName) {
    requireNonNull(node, "node cannot be null");
    requireNonNull(fieldName, "fieldName cannot be null");
    Optional<Edge> foreignKey = getForeignKey(node, fieldName);
    return foreignKey.map(fk -> fk.isUnaryAssociation()).orElse(false);
  }

  public static String toSqlValue(Field field, String value) {
    // apply mutators before calling this!

    // NOTE: The only known data type that this method will not work for is TEXT[]. When you read
    // a column with this data type, it will return '{dog, cat, mouse}'. You will need to use
    // a mutator to convert this into '{"dog", "cat", "mouse"} yourself, or the INSERT will fail.
    // In the future, we can use a regex to detect this pattern and automatically convert it in this
    // method if the data type is 'ARRAY'.
    requireNonNull(field, "field cannot be null");
    if (value == null) {
      return "NULL";
    }
    if (DATA_TYPES_NOT_REQUIRING_QUOTES.contains(field.getDataType())) {
      return value;
    }
    return "'" + value.replace("'", "''") + "'";

  }

  public static boolean requiresQuotes(Field field) {
    requireNonNull(field, "field cannot be null");
    return !DATA_TYPES_NOT_REQUIRING_QUOTES.contains(field.getDataType());
  }

}
