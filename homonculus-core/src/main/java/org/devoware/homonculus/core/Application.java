package org.devoware.homonculus.core;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.ConnectException;

import javax.validation.Validator;

import org.devoware.homonculus.config.ConfigurationException;
import org.devoware.homonculus.config.ConfigurationFactory;
import org.devoware.homonculus.config.ConfigurationSourceProvider;
import org.devoware.homonculus.config.EnvironmentVariableSubstitutor;
import org.devoware.homonculus.config.FileConfigurationSourceProvider;
import org.devoware.homonculus.config.SubstitutingSourceProvider;
import org.devoware.homonculus.config.YamlConfigurationFactory;
import org.devoware.homonculus.config.validation.Validators;
import org.devoware.homonculus.core.lifecycle.LifecycleManager;
import org.devoware.homonculus.core.lifecycle.Managed;
import org.devoware.homonculus.core.setup.Environment;
import org.devoware.homonculus.core.shutdown.Terminator;
import org.devoware.homonculus.core.util.Generics;
import org.devoware.homonculus.logging.BootstrapLogging;
import org.devoware.homonculus.logging.ConsoleAppenderFactory;
import org.devoware.homonculus.logging.FileAppenderFactory;
import org.devoware.homonculus.logging.SyslogAppenderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.resolute.jackson.ObjectMappers;

import ch.qos.logback.classic.Level;

public abstract class Application<T extends Configuration> implements Managed {

  private static final Logger appLog = LoggerFactory.getLogger(Application.class);

  private final LifecycleManager lifecycleManager;
  private T configuration;

  public Application() {
    this(null);
  }

  public Application(Runnable stopAction) {
    BootstrapLogging.bootstrap(bootstrapLogLevel());
    ObjectMapper objectMapper = ObjectMappers.create();
    Validator validator = Validators.newValidator();
    MetricRegistry metrics = new MetricRegistry();
    HealthCheckRegistry healthCheckRegistry = new HealthCheckRegistry();
    Environment environment =
        new Environment(objectMapper, validator, metrics, healthCheckRegistry);
    this.lifecycleManager = new LifecycleManager(getTerminationPort(), environment, stopAction);
  }

  public final void run(String[] args) throws Exception {
    try {
      String command = "start";
      if (args.length > 0) {
        command = args[0];
      }
      if (command.equals("start")) {
        try {
          appInit(args);
        } catch (Throwable t) {
          appLog.error(
              String.format("A problem occurred during initialization of the %s application",
                  getName()),
              t);
          return;
        }
        appStart();
      } else if (command.equals("stop")) {
        appStop();
      } else {
        appLog.error("Unrecognized command '" + command + "'.");
        return;
      }
    } catch (Throwable t) {
      t.printStackTrace();
    }
  }

  final void appInit(String[] args) throws Exception {
    configuration = getConfiguration(checkNotNull(args));
    if (configuration != null) {
      configuration.getLoggingFactory().configure(lifecycleManager.getEnvironment().metrics(),
          getName());
    }
    lifecycleManager.getEnvironment().manage(this);
    initialize(configuration, lifecycleManager.getEnvironment());
    UncaughtExceptionHandler handler = (thread, t) -> {
      appLog.error(
          String.format("A problem occurred which will cause the %s application to shutdown",
              getName()),
          t);
      doGracefulShutdown();
    };
    // For some reason, the uncaught exception handler is never invoked on my Mac OSx PC!
    Thread.setDefaultUncaughtExceptionHandler(handler);
    Thread.currentThread().setUncaughtExceptionHandler(handler);
  }

  final void appStart() throws Exception {
    appLog.info(String.format("Attempting to start the %s application...", getName()));
    try {
      lifecycleManager.start();
    } catch (Throwable t) {
      appLog.error(String.format("A problem occurred while attempting to start the %s application.",
          getName()), t);
      try {
        lifecycleManager.stop();
      } finally {
        if (configuration != null) {
          configuration.getLoggingFactory().stop();
        }
      }
    }
  }

  final void appStop() throws Exception {
    appLog.info(String.format("Attempting to stop the %s application...", getName()));
    Terminator terminator = new Terminator(this.lifecycleManager);
    try {
      terminator.terminate();
    } catch (ConnectException ex) {
      appLog.info(String.format(
          "Could not stop the %s application because it does not appear to be running.",
          getName()));
    } catch (Exception ex) {
      appLog.error(String.format("A problem occurred while attempting to stop the %s application",
          getName()), ex);
    }

  }

  public final Class<T> getConfigurationClass() {
    return Generics.getTypeParameter(getClass(), Object.class);
  }

  public int getTerminationPort() {
    return LifecycleManager.DEFAULT_TERMINATION_PORT;
  }

  @Override
  public int getPriority() {
    return 999;
  }

  public abstract String getName();

  protected Level bootstrapLogLevel() {
    return Level.WARN;
  }

  protected abstract void initialize(T configuration, Environment environment) throws Exception;

  private T getConfiguration(String[] args) throws IOException, ConfigurationException {
    if (args.length < 2) {
      throw new RuntimeException(
          "You must specify the path to a configuration file as the second program argument.");
    }
    String configFilePath = args[1];
    appLog.info("Loading configuration file " + configFilePath + "...");
    ConfigurationSourceProvider provider = new FileConfigurationSourceProvider();
    provider = new SubstitutingSourceProvider(provider, new EnvironmentVariableSubstitutor(false));

    ObjectMapper objectMapper = ObjectMappers.create();
    objectMapper.getSubtypeResolver().registerSubtypes(ConsoleAppenderFactory.class,
        FileAppenderFactory.class, SyslogAppenderFactory.class);
    ConfigurationFactory<T> configFactory = new YamlConfigurationFactory<T>(getConfigurationClass(),
        objectMapper, Validators.newValidator());
    T config = configFactory.build(provider, configFilePath);
    appLog.info("Configuration file loaded successfully.");
    return config;
  }

  private void doGracefulShutdown() {
    appLog.info(String.format("Attempting stop the %s application....", getName()));
    if (lifecycleManager != null) {
      try {
        lifecycleManager.stop();
      } catch (Exception e) {
        appLog.error("A problem occurred while attempting to stop the application.", e);
      } finally {
        if (configuration != null) {
          configuration.getLoggingFactory().stop();
        }
      }
    }
  }

}
