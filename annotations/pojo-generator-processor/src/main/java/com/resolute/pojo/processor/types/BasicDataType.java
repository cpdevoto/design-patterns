package com.resolute.pojo.processor.types;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.resolute.utils.simple.pojo_generator.DataType;

class BasicDataType implements DataType {
  private final String packageName;
  private final String simpleName;

  BasicDataType(ImportExclusion importExclusion, String value) {
    int lastDot = value.lastIndexOf('.');
    if (lastDot == -1) {
      this.simpleName = value;
      this.packageName = null;
    } else {
      this.simpleName = value.substring(lastDot + 1);
      String packageName = value.substring(0, lastDot);
      if (importExclusion != null &&
          (packageName.equals(importExclusion.getPackageName()) ||
              packageName.equals(
                  importExclusion.getPackageName() + "." + importExclusion.getModuleName()))) {
        this.packageName = null;
      } else {
        this.packageName = packageName;
      }
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
