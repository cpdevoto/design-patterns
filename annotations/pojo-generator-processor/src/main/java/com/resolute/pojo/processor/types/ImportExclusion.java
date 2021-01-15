package com.resolute.pojo.processor.types;

import static java.util.Objects.requireNonNull;

public class ImportExclusion {
  private final String packageName;
  private final String moduleName;

  public ImportExclusion(String packageName, String moduleName) {
    this.packageName = requireNonNull(packageName, "packageName cannot be null");
    this.moduleName = requireNonNull(moduleName, "moduleName cannot be null");
  }

  public String getPackageName() {
    return packageName;
  }

  public String getModuleName() {
    return moduleName;
  }

}
