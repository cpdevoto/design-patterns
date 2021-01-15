package com.resolute.pojo.processor.types;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toCollection;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.resolute.utils.simple.pojo_generator.DataType;

class CompositeDataType implements DataType {
  private final List<DataType> children;

  CompositeDataType(List<DataType> children) {
    this.children = ImmutableList.copyOf(children);
  }

  @Override
  public String getSimpleName() {
    return children.stream()
        .map(DataType::getSimpleName)
        .collect(joining());
  }

  @Override
  public Set<String> getImports() {
    return children.stream()
        .flatMap(child -> child.getImports().stream())
        .collect(toCollection(LinkedHashSet::new));
  }

}
