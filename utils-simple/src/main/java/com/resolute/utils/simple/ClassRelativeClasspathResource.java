package com.resolute.utils.simple;

import static java.util.Objects.requireNonNull;

import java.io.InputStream;
import java.net.URL;

class ClassRelativeClasspathResource implements ClasspathResource {
  private final Class<?> klass;
  private final String name;

  ClassRelativeClasspathResource(Class<?> klass, String name) {
    this.klass = requireNonNull(klass, "klass cannot be null");
    this.name = requireNonNull(name, "name cannot be null");
  }

  @Override
  public InputStream getResourceAsStream() {
    return klass.getResourceAsStream(name);
  }

  @Override
  public URL getResource() {
    return klass.getResource(name);
  }

}
