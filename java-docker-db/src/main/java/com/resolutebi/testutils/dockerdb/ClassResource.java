package com.resolutebi.testutils.dockerdb;

import java.util.Optional;

class ClassResource {
  private final Optional<Class<?>> fileLocatorClass;
  private final String fileName;

  ClassResource(String fileName) {
    this.fileLocatorClass = Optional.empty();
    this.fileName = fileName;
  }

  ClassResource(Class<?> fileLocatorClass, String fileName) {
    this.fileLocatorClass = Optional.of(fileLocatorClass);
    this.fileName = fileName;
  }

  Optional<Class<?>> getFileLocatorClass() {
    return fileLocatorClass;
  }

  String getFileName() {
    return fileName;
  }

}
