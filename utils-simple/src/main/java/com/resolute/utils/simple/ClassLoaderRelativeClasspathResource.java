package com.resolute.utils.simple;

import static java.util.Objects.requireNonNull;

import java.io.InputStream;
import java.net.URL;

class ClassLoaderRelativeClasspathResource implements ClasspathResource {
  private final ClassLoader classLoader;
  private final String name;

  ClassLoaderRelativeClasspathResource(ClassLoader classLoader, String name) {
    this.classLoader = requireNonNull(classLoader, "klass cannot be null");
    this.name = requireNonNull(name, "name cannot be null");
  }

  @Override
  public InputStream getResourceAsStream() {
    return classLoader.getResourceAsStream(name);
  }

  @Override
  public URL getResource() {
    return classLoader.getResource(name);
  }

}
