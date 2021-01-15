package com.resolute.utils.simple;

import java.io.InputStream;
import java.net.URL;

public interface ClasspathResource {

  public static ClasspathResource create(Class<?> klass, String name) {
    return new ClassRelativeClasspathResource(klass, name);
  }

  public static ClasspathResource create(ClassLoader classLoader, String name) {
    return new ClassLoaderRelativeClasspathResource(classLoader, name);
  }

  public static ClasspathResource create(String name) {
    return new ClassRelativeClasspathResource(String.class, name);
  }

  public InputStream getResourceAsStream();

  public URL getResource();

}
