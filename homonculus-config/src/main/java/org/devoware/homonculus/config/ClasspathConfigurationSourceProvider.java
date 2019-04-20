package org.devoware.homonculus.config;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * An implementation of {@link ConfigurationSourceProvider} that reads the configuration from the
 * classpath.
 */
public class ClasspathConfigurationSourceProvider implements ConfigurationSourceProvider {
  private final ClassLoader cl;

  public ClasspathConfigurationSourceProvider() {
    this(ClasspathConfigurationSourceProvider.class.getClassLoader());
  }

  public ClasspathConfigurationSourceProvider(ClassLoader cl) {
    this.cl = checkNotNull(cl);
  }

  @Override
  public InputStream open(String path) throws IOException {
    InputStream input = cl.getResourceAsStream(path);
    if (input == null) {
      throw new FileNotFoundException("Classpath resource " + path + " could not be found.");
    }
    return input;
  }
}
