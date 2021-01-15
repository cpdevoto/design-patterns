package com.resolute.pojo.processor.types;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.resolute.utils.simple.pojo_generator.DataType;

class BasicDataType implements DataType {
  private final String packageName;
  private final String simpleName;

  BasicDataType(String value) {
    int lastDot = value.lastIndexOf('.');
    if (lastDot == -1) {
      this.simpleName = value;
      this.packageName = null;
    } else {
      this.simpleName = value.substring(lastDot + 1);
      this.packageName = value.substring(0, lastDot);

    }
  }

  @Override
  public Set<String> getImports() {
    if (packageName == null || "java.lang".equals(packageName)) {
      return ImmutableSet.of();
    }
    return ImmutableSet.of(packageName + "." + simpleName);
  }

  @Override
  public String getSimpleName() {
    return simpleName;
  }

}
