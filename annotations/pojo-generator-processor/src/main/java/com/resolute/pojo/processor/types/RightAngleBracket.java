package com.resolute.pojo.processor.types;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.resolute.utils.simple.pojo_generator.DataType;

class RightAngleBracket implements DataType {

  RightAngleBracket() {}

  @Override
  public String getSimpleName() {
    return ">";
  }

  @Override
  public Set<String> getImports() {
    return ImmutableSet.of();
  }

}
