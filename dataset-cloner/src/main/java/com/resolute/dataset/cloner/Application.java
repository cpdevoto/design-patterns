package com.resolute.dataset.cloner;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;

import javax.sql.DataSource;

import org.postgresql.ds.PGSimpleDataSource;

import com.google.common.collect.ImmutableSet;
import com.google.common.primitives.Ints;
import com.resolute.database.crawler.DatabaseCrawler;
import com.resolute.database.crawler.model.Graph;
import com.resolute.dataset.cloner.log.Logger;
import com.resolute.utils.simple.ElapsedTimeUtils;

public abstract class Application {

  private static final Set<String> STANDARD_PROPERTIES = ImmutableSet.of(
      "host", "port", "database", "user", "password", "logFile", "numCopies", "debug",
      "pureCopyMode", "outputFile");

  protected Application() {}

  public final void run(String... arguments) throws Exception {
    System.out.println(String.format("Starting %s...", getClass().getSimpleName()));
    // Load properties file
    String propertiesFile;
    if (arguments.length > 0) {
      propertiesFile = arguments[0];
    } else {
      propertiesFile = "dataset-cloner.conf";
    }
    Properties baseProperties = loadProperties(propertiesFile);

    // Compute numCopies from properties file
    Integer numCopies = Ints.tryParse(baseProperties.getProperty("numCopies", "1"));
    checkArgument(numCopies != null && numCopies > 0, "expected a positive integer for numCopies");

    // Compute logger from properties file
    String logFile = baseProperties.getProperty("logFile", "dataset-cloner.log");
    Logger logger = new Logger(logFile);

    // Compute debug from properties file
    Boolean debug = Boolean.parseBoolean(baseProperties.getProperty("debug", "false"));

    // Compute pureCopyMode from properties file
    boolean pureCopyMode =
        Boolean.parseBoolean(baseProperties.getProperty("pureCopyMode", "false"));

    // Compute outputFile from properties file
    String outputFilePath = baseProperties.getProperty("outputFile", "dataset-cloner.sql");
    Logger outputFile = new Logger(outputFilePath);

    // Compute any extra properties from properties file
    Properties properties = new Properties();
    for (Object propNameObject : baseProperties.keySet()) {
      String propName = String.class.cast(propNameObject);
      if (STANDARD_PROPERTIES.contains(propName)) {
        continue;
      }
      properties.setProperty(propName, baseProperties.getProperty(propName));
    }

    // Compute dataSource from properties file
    DataSource dataSource = createDataSource(baseProperties);

    // Compute schemaGraph using dataSource
    DatabaseCrawler dao = DatabaseCrawler.create(dataSource);
    long start = System.currentTimeMillis();
    System.out.println("Starting database graph generation...");
    Graph schemaGraph = dao.getSchemaGraph();

    // Encapsulate all properties into an Environment object
    Environment environment = Environment.builder()
        .withDataSource(dataSource)
        .withLogger(logger)
        .withSchemaGraph(schemaGraph)
        .withNumCopies(numCopies)
        .withDebug(debug)
        .withPureCopyMode(pureCopyMode)
        .withOutputFile(outputFile)
        .withProperties(properties)
        .build();

    // Run the application, passing in the environment object
    long elapsed = System.currentTimeMillis() - start;
    System.out
        .println(
            "Database graph generation completed in " + ElapsedTimeUtils.format(elapsed)
                + "\n");
    run(environment);
  }

  public abstract void run(Environment environment);

  private Properties loadProperties(String propFile) throws IOException {
    Properties props = new Properties();
    try (InputStream in = new FileInputStream(propFile)) {
      props.load(in);
    }
    return props;
  }

  private DataSource createDataSource(Properties props) {
    PGSimpleDataSource dataSource = new PGSimpleDataSource();
    dataSource.setServerName(props.getProperty("host"));
    dataSource.setPortNumber(Integer.parseInt(props.getProperty("port")));
    dataSource.setDatabaseName(props.getProperty("database"));
    dataSource.setUser(props.getProperty("user"));
    dataSource.setPassword(props.getProperty("password"));
    return dataSource;
  }

}
