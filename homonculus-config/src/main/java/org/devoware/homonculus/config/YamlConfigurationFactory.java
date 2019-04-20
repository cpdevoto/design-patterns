package org.devoware.homonculus.config;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.fasterxml.jackson.databind.node.TreeTraversingParser;
import com.fasterxml.jackson.dataformat.yaml.JacksonYAMLParseException;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.snakeyaml.error.MarkedYAMLException;

@SuppressWarnings("deprecation")
public class YamlConfigurationFactory<T> implements ConfigurationFactory<T> {
  private static final Logger log = LoggerFactory.getLogger(YamlConfigurationFactory.class);

  private final Class<T> clazz;
  private final ObjectMapper mapper;
  private final YAMLFactory yamlFactory;
  private final Optional<Validator> validator;


  public YamlConfigurationFactory(Class<T> clazz, ObjectMapper mapper) {
    this(clazz, mapper, null);
  }

  public YamlConfigurationFactory(Class<T> clazz, ObjectMapper mapper, Validator validator) {
    this.clazz = checkNotNull(clazz);
    checkNotNull(mapper);
    this.mapper = mapper.copy().enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    this.yamlFactory = new YAMLFactory();
    if (validator == null) {
      this.validator = Optional.empty();
    } else {
      this.validator = Optional.of(validator);
    }
  }

  @Override
  public T build(ConfigurationSourceProvider provider, String path)
      throws IOException, ConfigurationException {
    log.info("Loading configuration from " + path + "...");
    try (InputStream input = provider.open(requireNonNull(path))) {
      final JsonNode node = mapper.readTree(yamlFactory.createParser(input));

      if (node == null) {
        throw ConfigurationParsingException
            .builder("Configuration at " + path + " must not be empty").build(path);
      }

      try {
        final T config = mapper.readValue(new TreeTraversingParser(node), clazz);
        validate(path, config);
        log.info("Configuration successfully loaded from " + path + ".");
        return config;
      } catch (UnrecognizedPropertyException e) {
        final List<String> properties =
            e.getKnownPropertyIds().stream().map(Object::toString).collect(Collectors.toList());
        throw ConfigurationParsingException.builder("Unrecognized field").setFieldPath(e.getPath())
            .setLocation(e.getLocation()).addSuggestions(properties)
            .setSuggestionBase(e.getPropertyName()).setCause(e).build(path);
      } catch (InvalidFormatException e) {
        final String sourceType = e.getValue().getClass().getSimpleName();
        final String targetType = e.getTargetType().getSimpleName();
        throw ConfigurationParsingException.builder("Incorrect type of value")
            .setDetail("is of type: " + sourceType + ", expected: " + targetType)
            .setLocation(e.getLocation()).setFieldPath(e.getPath()).setCause(e).build(path);
      } catch (JsonMappingException e) {
        throw ConfigurationParsingException.builder("Failed to parse configuration")
            .setDetail(e.getMessage()).setFieldPath(e.getPath()).setLocation(e.getLocation())
            .setCause(e).build(path);
      }

    } catch (JacksonYAMLParseException e) {
      final ConfigurationParsingException.Builder builder = ConfigurationParsingException
          .builder("Malformed YAML").setCause(e).setDetail(e.getMessage());

      if (e instanceof MarkedYAMLException) {
        builder.setLocation(((MarkedYAMLException) e).getProblemMark());
      }

      throw builder.build(path);
    }
  }

  private void validate(String path, T config) throws ConfigurationValidationException {
    if (validator.isPresent()) {
      final Set<ConstraintViolation<T>> violations = validator.get().validate(config);
      if (!violations.isEmpty()) {
        throw new ConfigurationValidationException(path, violations);
      }
    }
  }

}
