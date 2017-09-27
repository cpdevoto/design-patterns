package com.resolutebi.baseline.expr;

import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class Inputs {

  private final Map<VariableId<?>, Object> inputs;
  
  public static Builder builder() {
    return new Builder();
  }
  
  private Inputs(Builder builder) {
    this.inputs = ImmutableMap.copyOf(builder.inputs);
  }
  
  @SuppressWarnings("unchecked")
  public <T> T getValue(VariableId<T> id) {
    return (T) inputs.get(id);
  }

  public static class Builder {
    private Map<VariableId<?>, Object> inputs = Maps.newHashMap();
    
    private Builder () {}
    
    public <T> Builder withInput (VariableId<T> id, T value) {
      inputs.put(id, value); 
      return this;        
    }
    
    public Inputs build () {
      VariableId<?> [] ids = VariableId.values();
      if (inputs.size() != ids.length) {
        List<VariableId<?>> missingIds = Lists.newArrayList();
        for (VariableId<?> id : ids) {
          if (!inputs.containsKey(id)) {
            missingIds.add(id);
          }
        }
        throw new IllegalStateException("Values are missing for all of the following variables: " + missingIds);
      }
      return new Inputs(this);
    }
    
  }
}