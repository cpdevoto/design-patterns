package com.resolute.utils.simple.pojo_generator;

import static java.util.Objects.requireNonNull;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

class SimpleDataType implements DataType {

  private String simpleName;

  public SimpleDataType(String simpleName) {
    this.simpleName = requireNonNull(simpleName, "simpleName cannot be null");
  }

  @Override
  public String getSimpleName() {
    return simpleName;
  }

  @Override
  public Set<String> getImports() {
    return ImmutableSet.of();
  }



}
