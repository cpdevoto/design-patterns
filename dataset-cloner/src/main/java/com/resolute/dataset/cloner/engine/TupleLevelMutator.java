package com.resolute.dataset.cloner.engine;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

import com.google.common.collect.Maps;
import com.resolute.database.crawler.model.Field;

public class TupleLevelMutator {

  private final Consumer<Context> consumer;

  static TupleLevelMutator create(Consumer<Context> consumer) {
    return new TupleLevelMutator(consumer);
  }

  private TupleLevelMutator(Consumer<Context> consumer) {
    this.consumer = requireNonNull(consumer, "consumer cannot be null");
  }

  Map<Field, String> mutate(Map<Field, String> fieldValues) {
    Context context = new Context(fieldValues);
    consumer.accept(context);
    return context.getFieldValues();
  }

  public static class Context {
    private final Map<String, Field> fieldsByName;
    private final Map<String, String> fieldValuesByName;

    private Context(Map<Field, String> fieldValues) {
      requireNonNull(fieldValues, "fieldValues cannot be null");
      this.fieldsByName = Maps.newLinkedHashMap();
      this.fieldValuesByName = Maps.newLinkedHashMap();
      for (Entry<Field, String> entry : fieldValues.entrySet()) {
        fieldsByName.put(entry.getKey().getName(), entry.getKey());
        fieldValuesByName.put(entry.getKey().getName(), entry.getValue());
      }
    }

    public Field getFieldDefinition(String fieldName) {
      requireNonNull(fieldName, "fieldName cannot be null");
      checkArgument(fieldsByName.containsKey(fieldName), "invalid fieldName");
      return fieldsByName.get(fieldName);
    }

    public String getValue(String fieldName) {
      requireNonNull(fieldName, "fieldName cannot be null");
      checkArgument(fieldValuesByName.containsKey(fieldName), "invalid fieldName");
      return fieldValuesByName.get(fieldName);
    }

    public String setValue(String fieldName, Object value) {
      requireNonNull(fieldName, "fieldName cannot be null");
      checkArgument(fieldValuesByName.containsKey(fieldName), "invalid fieldName");
      return fieldValuesByName.put(fieldName, String.valueOf(value));
    }

    private Map<Field, String> getFieldValues() {
      Map<Field, String> temp = Maps.newLinkedHashMap();
      for (Entry<String, String> entry : fieldValuesByName.entrySet()) {
        temp.put(fieldsByName.get(entry.getKey()), entry.getValue());
      }
      return temp;
    }
  }

}
