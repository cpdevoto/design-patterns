package org.devoware.homonculus.config;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.devoware.homonculus.config.test.fixtures.CompositeConfiguration;
import org.devoware.homonculus.config.test.fixtures.DatabaseConfiguration;
import org.devoware.homonculus.config.test.fixtures.HostConfiguration;
import org.devoware.homonculus.config.test.fixtures.SimpleConfiguration;
import org.devoware.homonculus.config.test.fixtures.SimpleConfigurationWithValidation;
import org.devoware.homonculus.config.validation.Validators;
import org.junit.Test;

import com.resolute.jackson.ObjectMappers;

public class YamlConfigurationFactoryTest {
  private String path = "test.yml";
  private String simpleConfigString = String.format("firstName: Carlos%n" + "lastName: Devoto");

  private String invalidSimpleConfigString =
      String.format("firstName: Carlos%n" + "middleName: P%n" + "lastName: Devoto");

  private String malformedYamlConfigString =
      String.format("firstName: Carlos" + "lastName: Devoto");

  private String hostConfigString = String.format("host: www.purple.com%n" + "port: 80");

  private String invalidHostConfigString = String.format("host: www.purple.com%n" + "port: yellow");

  private String compositeConfigString = String
      .format("serviceName: MyService%n" + "database:%n" + "  driverClass: org.postgresql.Driver%n"
          + "  user: pg-user%n" + "  password: fake-password");

  private String invalidCompositeConfigString = String
      .format("serviceName: MyService%n" + "database:%n" + "  driverClass: org.postgresql.Driver%n"
          + "  user: pg-user%n" + "  url: jdbc://mydatabase/%n" + "  password: fake-password");

  private ConfigurationSourceProvider provider = mock(ConfigurationSourceProvider.class);


  @Test
  public void test_load_simple_configuration() throws IOException, ConfigurationException {
    when(provider.open(anyString()))
        .thenReturn(new ByteArrayInputStream(simpleConfigString.getBytes(StandardCharsets.UTF_8)));

    ConfigurationFactory<SimpleConfiguration> configFactory =
        new YamlConfigurationFactory<>(SimpleConfiguration.class, ObjectMappers.create());
    SimpleConfiguration config = configFactory.build(provider, path);

    assertThat(config.getFirstName(), equalTo("Carlos"));
    assertThat(config.getLastName(), equalTo("Devoto"));
  }

  @Test
  public void test_load_simple_configuration_with_different_data_types()
      throws IOException, ConfigurationException {
    when(provider.open(anyString()))
        .thenReturn(new ByteArrayInputStream(hostConfigString.getBytes(StandardCharsets.UTF_8)));

    ConfigurationFactory<HostConfiguration> configFactory =
        new YamlConfigurationFactory<>(HostConfiguration.class, ObjectMappers.create());
    HostConfiguration config = configFactory.build(provider, path);

    assertThat(config.getHost(), equalTo("www.purple.com"));
    assertThat(config.getPort(), equalTo(80));
  }

  @Test(expected = ConfigurationParsingException.class)
  public void test_load_invalid_simple_configuration() throws IOException, ConfigurationException {
    when(provider.open(anyString())).thenReturn(
        new ByteArrayInputStream(invalidSimpleConfigString.getBytes(StandardCharsets.UTF_8)));

    ConfigurationFactory<SimpleConfiguration> configFactory =
        new YamlConfigurationFactory<>(SimpleConfiguration.class, ObjectMappers.create());
    configFactory.build(provider, path);
  }

  @Test(expected = ConfigurationParsingException.class)
  public void test_load_invalid_configuration_with_different_data_types()
      throws IOException, ConfigurationException {
    when(provider.open(anyString())).thenReturn(
        new ByteArrayInputStream(invalidHostConfigString.getBytes(StandardCharsets.UTF_8)));

    ConfigurationFactory<HostConfiguration> configFactory =
        new YamlConfigurationFactory<>(HostConfiguration.class, ObjectMappers.create());
    configFactory.build(provider, path);
  }

  @Test(expected = ConfigurationParsingException.class)
  public void test_load_malformed_yaml_configuration() throws IOException, ConfigurationException {
    when(provider.open(anyString())).thenReturn(
        new ByteArrayInputStream(malformedYamlConfigString.getBytes(StandardCharsets.UTF_8)));

    ConfigurationFactory<SimpleConfiguration> configFactory =
        new YamlConfigurationFactory<>(SimpleConfiguration.class, ObjectMappers.create());
    configFactory.build(provider, path);
  }

  @Test(expected = ConfigurationParsingException.class)
  public void test_load_empty_configuration() throws IOException, ConfigurationException {
    when(provider.open(anyString()))
        .thenReturn(new ByteArrayInputStream("".getBytes(StandardCharsets.UTF_8)));

    ConfigurationFactory<SimpleConfiguration> configFactory =
        new YamlConfigurationFactory<>(SimpleConfiguration.class, ObjectMappers.create());
    configFactory.build(provider, path);
  }

  @Test
  public void test_load_composite_configuration() throws IOException, ConfigurationException {
    when(provider.open(anyString())).thenReturn(
        new ByteArrayInputStream(compositeConfigString.getBytes(StandardCharsets.UTF_8)));

    ConfigurationFactory<CompositeConfiguration> configFactory =
        new YamlConfigurationFactory<>(CompositeConfiguration.class, ObjectMappers.create());
    CompositeConfiguration config = configFactory.build(provider, path);

    assertThat(config.getServiceName(), equalTo("MyService"));

    DatabaseConfiguration db = config.getDatabase();
    assertThat(db.getDriverClass(), equalTo("org.postgresql.Driver"));
    assertThat(db.getUser(), equalTo("pg-user"));
    assertThat(db.getPassword(), equalTo("fake-password"));
  }

  @Test(expected = ConfigurationParsingException.class)
  public void test_load_invalid_composite_configuration()
      throws IOException, ConfigurationException {
    when(provider.open(anyString())).thenReturn(
        new ByteArrayInputStream(invalidCompositeConfigString.getBytes(StandardCharsets.UTF_8)));

    ConfigurationFactory<CompositeConfiguration> configFactory =
        new YamlConfigurationFactory<>(CompositeConfiguration.class, ObjectMappers.create());
    configFactory.build(provider, path);
  }

  @Test
  public void test_load_composite_configuration_from_classpath()
      throws IOException, ConfigurationException {
    ConfigurationSourceProvider provider = new ClasspathConfigurationSourceProvider();

    ConfigurationFactory<CompositeConfiguration> configFactory =
        new YamlConfigurationFactory<>(CompositeConfiguration.class, ObjectMappers.create());
    CompositeConfiguration config = configFactory.build(provider, path);

    assertThat(config.getServiceName(), equalTo("MyService"));

    DatabaseConfiguration db = config.getDatabase();
    assertThat(db.getDriverClass(), equalTo("org.postgresql.Driver"));
    assertThat(db.getUser(), equalTo("pg-user"));
    assertThat(db.getPassword(), equalTo("fake-password"));
  }

  @Test(expected = FileNotFoundException.class)
  public void test_load_nonexistent_configuration_from_classpath()
      throws IOException, ConfigurationException {
    ConfigurationSourceProvider provider = new ClasspathConfigurationSourceProvider();

    ConfigurationFactory<CompositeConfiguration> configFactory =
        new YamlConfigurationFactory<>(CompositeConfiguration.class, ObjectMappers.create());
    configFactory.build(provider, "wtf.yml");
  }

  @Test
  public void test_load_composite_configuration_from_file()
      throws IOException, ConfigurationException {
    String path = "src/test/resources/test.yml";
    ConfigurationSourceProvider provider = new FileConfigurationSourceProvider();

    ConfigurationFactory<CompositeConfiguration> configFactory =
        new YamlConfigurationFactory<>(CompositeConfiguration.class, ObjectMappers.create());
    CompositeConfiguration config = configFactory.build(provider, path);

    assertThat(config.getServiceName(), equalTo("MyService"));

    DatabaseConfiguration db = config.getDatabase();
    assertThat(db.getDriverClass(), equalTo("org.postgresql.Driver"));
    assertThat(db.getUser(), equalTo("pg-user"));
    assertThat(db.getPassword(), equalTo("fake-password"));
  }

  @Test(expected = FileNotFoundException.class)
  public void test_load_nonexistent_configuration_from_file()
      throws IOException, ConfigurationException {
    String path = "src/test/resources/wtf.yml";
    ConfigurationSourceProvider provider = new FileConfigurationSourceProvider();

    ConfigurationFactory<CompositeConfiguration> configFactory =
        new YamlConfigurationFactory<>(CompositeConfiguration.class, ObjectMappers.create());
    configFactory.build(provider, path);
  }

  @Test
  public void test_load_simple_configuration_with_validation()
      throws IOException, ConfigurationException {
    when(provider.open(anyString()))
        .thenReturn(new ByteArrayInputStream(simpleConfigString.getBytes(StandardCharsets.UTF_8)));

    ConfigurationFactory<SimpleConfigurationWithValidation> configFactory =
        new YamlConfigurationFactory<>(SimpleConfigurationWithValidation.class,
            ObjectMappers.create(),
            Validators.newValidator());
    SimpleConfigurationWithValidation config = configFactory.build(provider, path);

    assertThat(config.getFirstName(), equalTo("Carlos"));
    assertThat(config.getLastName(), equalTo("Devoto"));
  }

  @Test(expected = ConfigurationValidationException.class)
  public void test_load_invalid_configuration_with_validation()
      throws IOException, ConfigurationException {
    when(provider.open(anyString()))
        .thenReturn(new ByteArrayInputStream("lastName: D".getBytes(StandardCharsets.UTF_8)));

    ConfigurationFactory<SimpleConfigurationWithValidation> configFactory =
        new YamlConfigurationFactory<>(SimpleConfigurationWithValidation.class,
            ObjectMappers.create(),
            Validators.newValidator());
    SimpleConfigurationWithValidation config = configFactory.build(provider, path);

    assertThat(config.getFirstName(), equalTo("Carlos"));
    assertThat(config.getLastName(), equalTo("Devoto"));
  }
}
