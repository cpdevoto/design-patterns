package com.resolutebi.baseline.expr;

import java.util.Arrays;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

class VariableId <T> implements Comparable<VariableId<T>> {
  static final VariableId<Double> AVG_DAILY_TEMP = new VariableId<>("avg_daily_temp", Double.class);
  static final VariableId<Boolean> WEEK_DAY = new VariableId<>("week_day", Boolean.class);
  
  private static final VariableId<?> [] VALUES = { AVG_DAILY_TEMP, WEEK_DAY };
  private static final Map<String, VariableId<?>> VARIABLE_IDS;
  
  private final String name;
  private final Class<T> type;
  
  static {
    Map<String, VariableId<?>> ids = Maps.newHashMap();
    for (VariableId<?> id : VariableId.VALUES) {
      ids.put(id.name, id);
    }
    VARIABLE_IDS = ImmutableMap.copyOf(ids);
  }
  
  static VariableId<?> [] values () {
    return Arrays.copyOf(VALUES, VALUES.length);
  }
  
  @SuppressWarnings("unchecked")
  static <T> VariableId<T> get(String name) {
    if (name == null) {
      return null;
    }
    return (VariableId<T>) VARIABLE_IDS.get(name.toLowerCase());
  }
   
  private VariableId (String name, Class<T> type) {
    this.name = name;
    this.type = type;
  }

  String getName() {
    return name;
  }

  Class<T> getType() {
    return type;
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
    VariableId<?> other = (VariableId<?>) obj;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return this.name;
  }

  @Override
  public int compareTo(VariableId<T> o) {
    return this.name.compareTo(o.name);
  }
  
  
}
