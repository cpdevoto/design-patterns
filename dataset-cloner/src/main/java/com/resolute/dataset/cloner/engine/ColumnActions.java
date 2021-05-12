package com.resolute.dataset.cloner.engine;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.resolute.database.crawler.model.Field;
import com.resolute.database.crawler.model.Node;
import com.resolute.dataset.cloner.utils.NodeUtils;

class ColumnActions {

  private static final ImmutableSet<String> TEXT_TYPES =
      ImmutableSet.of("text", "character varying");
  private final Node node;
  private final FieldLevelMutators mutators;
  private final Map<String, ColumnAction> columnActions;

  static ColumnActions create(Node node, FieldLevelMutators mutators, boolean pureCopyMode) {
    return new ColumnActions(node, mutators, pureCopyMode);
  }

  private ColumnActions(Node node, FieldLevelMutators mutators, boolean pureCopyMode) {
    this.node = requireNonNull(node, "node cannot be null");
    this.mutators = requireNonNull(mutators, "mutators cannot be null");
    Map<String, ColumnAction> temp = Maps.newHashMap();
    for (Field field : node.getFields()) {
      ColumnAction action = pureCopyMode ? ColumnAction.COPY : calculateActionFor(field);
      temp.put(field.getName(), action);
    }
    this.columnActions = ImmutableMap.copyOf(temp);
  }

  ColumnAction get(String fieldName) {
    requireNonNull(fieldName, "fieldName cannot be null");
    return columnActions.get(fieldName);
  }

  private ColumnAction calculateActionFor(Field field) {
    String fieldName = field.getName();
    ColumnAction action = ColumnAction.COPY; // by default, we will COPY
    if (node.isForeignKeyField(fieldName)) {
      if (node.hasUnaryAssociation() && NodeUtils.isPartOfUnaryForeignKey(node, fieldName)) {
        action = ColumnAction.RESOLVE_AT_ROW_LEVEL;
      } else {
        action = ColumnAction.FK_LOOKUP;
      }
    } else if (node.isUniqueIndexField(fieldName) || node.isPrimaryKeyField(fieldName)) {
      List<List<Field>> indeces = NodeUtils.getUniqueIndeces(node, fieldName);
      if (node.isPrimaryKeyField(fieldName)) {
        indeces.add(node.getPrimaryKeyFields());
      }
      boolean allIndecesHaveForeignKey = true;
      for (List<Field> index : indeces) {
        if (!NodeUtils.hasForeignKeyField(node, index)) {
          allIndecesHaveForeignKey = false;
          break;
        }
      }
      if (allIndecesHaveForeignKey) {
        // Check if any of the indeces has a unary foreign key; if so, we need to resolve at runtime
        action = ColumnAction.COPY;
        if (node.hasUnaryAssociation()) {
          outer: for (List<Field> index : indeces) {
            for (Field indexField : index) {
              if (NodeUtils.isPartOfUnaryForeignKey(node, indexField.getName())) {
                action = ColumnAction.RESOLVE_AT_ROW_LEVEL;
                break outer;
              }
            }
          }
        } else if (mutators.get(fieldName).isPresent()) {
          action = ColumnAction.MUTATE;
        }
      } else if (mutators.get(fieldName).isPresent()) {
        action = ColumnAction.MUTATE;
      } else if (field.getDefaultValue().isPresent()) {
        action = ColumnAction.OMIT;
      } else if (TEXT_TYPES.contains(field.getDataType())) {
        action = ColumnAction.MUTATE;
      }
    } else if (mutators.get(fieldName).isPresent()) {
      action = ColumnAction.MUTATE;
    }
    return action;
  }


}
