package com.resolute.dataset.cloner;

import static java.util.Objects.requireNonNull;

import java.io.File;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Consumer;

import javax.sql.DataSource;

import com.resolute.database.crawler.model.Graph;
import com.resolute.dataset.cloner.log.Logger;

public class Environment {
  private final DataSource dataSource;
  private final Logger logger;
  private final int numCopies;
  private final Properties properties;
  private final Graph schemaGraph;
  private final boolean debug;
  private final boolean pureCopyMode;
  private final Logger outputFile;

  public static Builder builder() {
    return new Builder();
  }

  private Environment(Builder builder) {
    this.dataSource = builder.dataSource;
    this.logger = builder.logger;
    this.numCopies = builder.numCopies;
    this.properties = builder.properties;
    this.schemaGraph = builder.schemaGraph;
    this.debug = builder.debug;
    this.pureCopyMode = builder.pureCopyMode;
    this.outputFile = builder.outputFile;
  }

  public DataSource getDataSource() {
    return dataSource;
  }

  public Logger getLogger() {
    return logger;
  }

  public int getNumCopies() {
    return numCopies;
  }

  public boolean getDebug() {
    return debug;
  }

  public Properties getProperties() {
    return properties;
  }

  public Graph getSchemaGraph() {
    return schemaGraph;
  }

  public boolean getPureCopyMode() {
    return pureCopyMode;
  }

  public Logger getOutputFile() {
    return outputFile;
  }

  @Override
  public int hashCode() {
    return Objects.hash(dataSource, debug, logger, numCopies, properties, schemaGraph);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Environment other = (Environment) obj;
    return Objects.equals(dataSource, other.dataSource) && debug == other.debug
        && Objects.equals(logger, other.logger) && numCopies == other.numCopies
        && Objects.equals(properties, other.properties)
        && Objects.equals(schemaGraph, other.schemaGraph);
  }

  @Override
  public String toString() {
    return "Environment [dataSource=" + dataSource + ", logger=" + logger + ", numCopies="
        + numCopies + ", properties=" + properties + ", schemaGraph=" + schemaGraph + ", debug="
        + debug + "]";
  }

  public static class Builder {
    private Graph schemaGraph;
    private DataSource dataSource;
    private Logger logger = new Logger(new File("dataset-cloner.log"));
    private int numCopies = 1;
    private Properties properties = new Properties();
    private boolean debug = false;
    private boolean pureCopyMode = false;
    private Logger outputFile = new Logger(new File("dataset-cloner.sql"));


    private Builder() {}

    public Builder with(Consumer<Builder> consumer) {
      requireNonNull(consumer, "consumer cannot be null");
      consumer.accept(this);
      return this;
    }

    public Builder withDataSource(DataSource dataSource) {
      requireNonNull(dataSource, "dataSource cannot be null");
      this.dataSource = dataSource;
      return this;
    }

    public Builder withLogger(Logger logger) {
      requireNonNull(logger, "logger cannot be null");
      this.logger = logger;
      return this;
    }

    public Builder withNumCopies(int numCopies) {
      this.numCopies = numCopies;
      return this;
    }

    public Builder withDebug(boolean debug) {
      this.debug = debug;
      return this;
    }

    public Builder withOutputFile(Logger outputFile) {
      requireNonNull(outputFile, "outputFile cannot be null");
      this.outputFile = outputFile;
      return this;
    }

    public Builder withPureCopyMode(boolean pureCopyMode) {
      this.pureCopyMode = pureCopyMode;
      return this;
    }

    public Builder withProperties(Properties properties) {
      requireNonNull(properties, "properties cannot be null");
      this.properties = properties;
      return this;
    }

    public Builder withSchemaGraph(Graph schemaGraph) {
      requireNonNull(schemaGraph, "schemaGraph cannot be null");
      this.schemaGraph = schemaGraph;
      return this;
    }

    public Environment build() {
      requireNonNull(schemaGraph, "schemaGraph cannot be null");
      requireNonNull(dataSource, "dataSource cannot be null");
      return new Environment(this);
    }
  }
}
