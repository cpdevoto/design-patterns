package com.resolute.pojo.processor.types;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.resolute.utils.simple.pojo_generator.DataType;

class LeftSquareBracket implements DataType {

  LeftSquareBracket() {}

  @Override
  public String getSimpleName() {
    return "[";
  }

  @Override
  public Set<String> getImports() {
    return ImmutableSet.of();
  }
}
