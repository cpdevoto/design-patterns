package org.devoware.homonculus.logging.utils;

import java.io.IOException;

import org.devoware.homonculus.config.ConfigurationException;
import org.devoware.homonculus.config.ConfigurationFactory;
import org.devoware.homonculus.config.ConfigurationSourceProvider;
import org.devoware.homonculus.config.FileConfigurationSourceProvider;
import org.devoware.homonculus.config.YamlConfigurationFactory;
import org.devoware.homonculus.config.validation.Validators;
import org.devoware.homonculus.logging.ConsoleAppenderFactory;
import org.devoware.homonculus.logging.FileAppenderFactory;
import org.devoware.homonculus.logging.SyslogAppenderFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.resolute.jackson.ObjectMappers;

public class ConfigurationUtils {

  public static <T> T loadConfiguration(Class<T> configClass, String filePath)
      throws IOException, ConfigurationException {
    ConfigurationSourceProvider provider = new FileConfigurationSourceProvider();

    ObjectMapper objectMapper = ObjectMappers.create();
    objectMapper.getSubtypeResolver().registerSubtypes(ConsoleAppenderFactory.class,
        FileAppenderFactory.class, SyslogAppenderFactory.class);
    ConfigurationFactory<T> configFactory =
        new YamlConfigurationFactory<>(configClass, objectMapper,
            Validators.newValidator());
    T config = configFactory.build(provider, filePath);
    return config;
  }

  private ConfigurationUtils() {}

}
