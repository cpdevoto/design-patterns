package com.resolute.database.crawler.model;

import static com.google.common.base.Preconditions.checkArgument;
import static com.resolute.database.crawler.model.Comparators.fromEdgeComparator;
import static com.resolute.database.crawler.model.Comparators.toEdgeComparator;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class Node implements Comparable<Node> {
  private final String name;
  private final List<Field> primaryKey;
  private final List<String> primaryKeyFieldNames;
  private final Set<String> primaryKeyFieldNameSet;
  private final List<UniqueIndex> uniqueIndeces;
  private final Map<String, UniqueIndex> uniqueIndexMap;
  private final Set<String> uniqueIndexFieldNameSet;
  private final List<Edge> fromEdges = Lists.newLinkedList();
  private final List<Edge> toEdges = Lists.newLinkedList();
  private final Set<String> foreignKeyFieldNameSet = Sets.newHashSet();
  private final Map<String, Field> fields = Maps.newLinkedHashMap();


  // Node(String name, List<String> primaryKey) {
  // this(name, primaryKey.stream().map(pk -> new Field(pk, "", null)).collect(toList()),
  // ImmutableList.of());
  // }
  Node(Node node) {
    this(node.name, node.primaryKey, node.uniqueIndeces, node.getFields());
  }

  Node(String name, List<String> primaryKey) {
    this(name, primaryKey.stream().map(k -> new Field(k, "", null)).collect(toList()),
        ImmutableList.of());
  }

  Node(String name, List<Field> primaryKey, List<UniqueIndex> uniqueIndeces) {
    this(name, primaryKey, uniqueIndeces, ImmutableList.of());
  }

  Node(String name, List<Field> primaryKey, List<UniqueIndex> uniqueIndeces, List<Field> fields) {
    this.name = requireNonNull(name, "name cannot not null");
    this.primaryKey = ImmutableList.copyOf(requireNonNull(primaryKey, "primaryKey cannot be null"));
    this.primaryKeyFieldNames = ImmutableList.copyOf(this.primaryKey.stream()
        .map(Field::getName).collect(toList()));
    this.primaryKeyFieldNameSet =
        ImmutableSet.copyOf(this.primaryKeyFieldNames.stream().collect(toSet()));

    requireNonNull(uniqueIndeces, "uniqueIndeces cannot be null");
    this.uniqueIndeces = Lists.newArrayList(uniqueIndeces);
    this.uniqueIndexFieldNameSet = uniqueIndeces.stream()
        .map(UniqueIndex::getFields)
        .flatMap(index -> index.stream())
        .map(Field::getName)
        .collect(toSet());
    this.uniqueIndexMap = uniqueIndeces.stream()
        .collect(toMap(UniqueIndex::getName, Function.identity()));

    this.addFields(fields);
  }


  void addFromEdge(Edge edge) {
    fromEdges.add(edge);
  }

  void addToEdge(Edge edge) {
    toEdges.add(edge);
    foreignKeyFieldNameSet.addAll(edge.getForeignKey().getFields().stream()
        .map(ForeignKeyField::getToField)
        .collect(toSet()));
  }

  public void addFields(List<Field> fields) {
    fields.forEach(field -> this.fields.put(field.getName(), field));
  }

  public void addUniqueIndex(String indexName, List<String> uniqueIndex) {
    requireNonNull(indexName, "indexName cannot be null");
    requireNonNull(uniqueIndex, "uniqueIndex cannot be null");
    // First we need to check whether we already have this index defined!
    if (uniqueIndexMap.containsKey(indexName)) {
      return;
    }

    List<Field> indexFields = uniqueIndex.stream()
        .peek(fieldName -> {
          if (!fields.containsKey(fieldName)) {
            System.out.println("WARNING: Could not find field " + fieldName + " in table " + name
                + ". Skipping unique index " + uniqueIndex + "!");
          }
        })
        .filter(fieldName -> fields.containsKey(fieldName))
        .map(fieldName -> fields.get(fieldName))
        .collect(toList());

    if (indexFields.size() < uniqueIndex.size()) {
      // The index has one or more fields that have probably been renamed, so we will skip it
      // If this causes problems, you should create a unique constraint instead of a unique index.
      return;
    }
    UniqueIndex ui = new UniqueIndex(indexName, indexFields);
    uniqueIndeces.add(ui);
    uniqueIndexMap.put(indexName, ui);
    uniqueIndex.forEach(fieldName -> uniqueIndexFieldNameSet.add(fieldName));
  }

  public String getName() {
    return name;
  }

  public List<String> getPrimaryKey() {
    return primaryKeyFieldNames;
  }

  public List<Field> getPrimaryKeyFields() {
    return primaryKey;
  }

  public List<List<Field>> getUniqueIndeces() {
    return uniqueIndeces.stream()
        .map(UniqueIndex::getFields)
        .collect(toList());
  }

  List<UniqueIndex> getUniqueIndecesWithNames() {
    return uniqueIndeces;
  }

  public List<Edge> getFromEdges() {
    return fromEdges.stream()
        .sorted(fromEdgeComparator())
        .collect(toList());
  }

  public List<Edge> getToEdges() {
    return toEdges.stream()
        .sorted(toEdgeComparator())
        .collect(toList());
  }

  public List<Field> getFields() {
    return fields.values().stream()
        .collect(toList());
  }

  public List<String> getFieldNames() {
    return fields.keySet().stream()
        .collect(toList());
  }

  public Optional<Field> getField(String fieldName) {
    requireNonNull(fieldName, "fieldName cannot be null");
    return Optional.ofNullable(fields.get(fieldName));
  }

  public boolean isSuperclass() {
    return fromEdges.stream()
        .filter(Edge::isInheritanceAssociation)
        .findAny()
        .isPresent();
  }

  public boolean isSubclass() {
    return toEdges.stream()
        .filter(Edge::isInheritanceAssociation)
        .findAny()
        .isPresent();
  }

  public boolean hasUnaryAssociation() {
    return fromEdges.stream()
        .filter(edge -> edge.isUnaryAssociation())
        .findAny()
        .isPresent();
  }

  public Node getSuperclassRoot() {
    checkArgument(isSubclass(), "node must be a subclass");
    Node current = this;
    while (current.isSubclass()) {
      List<Edge> inheritanceEdges = current.toEdges.stream()
          .filter(Edge::isInheritanceAssociation)
          .collect(Collectors.toList());
      if (inheritanceEdges.size() > 1) {
        throw new AssertionError(
            "subclass " + current.getName() + " has more than one superclass!");
      }
      current = inheritanceEdges.get(0).getFrom();
    }
    return current;
  }

  public boolean isPrimaryKeyField(String fieldName) {
    return primaryKeyFieldNameSet.contains(fieldName);
  }

  public boolean isUniqueIndexField(String fieldName) {
    return uniqueIndexFieldNameSet.contains(fieldName);
  }

  public boolean isForeignKeyField(String fieldName) {
    return foreignKeyFieldNameSet.contains(fieldName);
  }

  public Optional<String> getDataType(String fieldName) {
    requireNonNull(fieldName);
    Field f = fields.get(fieldName);
    if (f == null) {
      return Optional.empty();
    }
    return Optional.of(f.getDataType());
  }

  public Optional<String> getDefaultValue(String fieldName) {
    requireNonNull(fieldName);
    Field f = fields.get(fieldName);
    if (f == null) {
      return Optional.empty();
    }
    return f.getDefaultValue();
  }


  @Override
  public String toString() {
    return "Node [name=" + name + ", primaryKey=" + primaryKey + ", fromEdges=" + getFromEdges()
        + ", toEdges=" + getToEdges() + "]";
  }

  @Override
  public int compareTo(Node o) {
    return name.compareTo(o.name);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
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
    Node other = (Node) obj;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    return true;
  }

}
