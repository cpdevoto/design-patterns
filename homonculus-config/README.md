# homonculus-config

**Owner(s):** Carlos Devoto

The **homonculus-config** library encapsulates logic used to automatically bind a YAML configuration file to a corresponding
[Jackson](https://github.com/FasterXML/jackson) annotated configuation class of your own design.  The library also includes functionality
to allow for configuration classes with validation annotations to be validated in accordance with the 
[JSR 303 - Bean Validation](http://beanvalidation.org/1.0/spec/) specification.  This library is a variation of the [DropWizard](http://www.dropwizard.io/1.0.0/docs/)
configuration library adapted for standalone Java service applications which do not include a Jersey/Jetty container (see [homonculus](https://github.com/cpdevoto/devoware-utils/tree/master/homonculus-core)).

## Using the homonculus-config library

### YAML configuration file

If you are building a simple application which needs to connect to a server on a specific port, 
you might create the following ```simple-app.yml``` file in your ```config``` directory:

```yaml
# the name of the host to connect to
hostName: localhost

# the port to connect to
port: 8080
```
### Configuration class

You use the **homonculus-config**
library in order to automatically transform your YAML configuration file into an instance of a Java class that you have created.  
If the configuration class includes any validation constraints defined as
annotations, you can validate the configuration object against these constraints, reporting any violations
encountered. Continuing with the example defined above, let's define a configuration class to go with our ```simple-app.yml``` configuration file:

```java
package com.doradosystems.simpleapp.config;

import javax.validation.constraints.NotNull;

import org.devoware.homonculus.validators.validation.PortRange;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SimpleApplicationConfiguration {

  @NotNull
  @JsonProperty
  private String hostName;
  
  @PortRange
  @JsonProperty
  private int port;

  public String getHostName() {
    return hostName;
  }

  public int getPort() {
    return port;
  }
  
}
```
When the homonculus-config APIs are used, an instance of the ```SimpleApplication``` class will be created, and each of its properties that
is annotated with the ```JsonProperty``` annotation will be populated with the correspondingly named value from the ```simple-app.yml```
file. An exception will be thrown if the ```simple-app.yml``` file is missing a ```hostName``` property since the ```hostName``` field
within the configuration class is annotated with the ```NotNull``` annotation.  An exception will also be thrown if the value of the
```port``` property within the ```simple-app.yml``` file is not between 1 and 65535 since the ```port``` field within the configuration
class is annotated with the ```PortRange``` annotation.

### Programmatic transformation and validation
Programmatically transform the ```simple-app.yml``` YAML configuration file into a SimpleApplicationConfiguration object and validate
this object as follows:

```
package com.doradosystems.simpleapp.config;

import java.io.IOException;

import javax.validation.Validator;

import org.devoware.homonculus.config.ConfigurationException;
import org.devoware.homonculus.config.ConfigurationFactory;
import org.devoware.homonculus.config.ConfigurationSourceProvider;
import org.devoware.homonculus.config.FileConfigurationSourceProvider;
import org.devoware.homonculus.config.YamlConfigurationFactory;
import org.devoware.homonculus.config.validation.Validators;

import com.doradosystems.simpleapp.config.SimpleApplicationConfiguration;

import com.fasterxml.jackson.databind.ObjectMapper;

public class SimpleApplication {

  public static void main(String[] args) throws IOException, ConfigurationException {
    
    ObjectMapper objectMapper = new ObjectMapper();
    Validator validator = Validators.newValidator();
    
    ConfigurationSourceProvider provider = new FileConfigurationSourceProvider();

    ConfigurationFactory<SimpleApplicationConfiguration> configFactory =
        new YamlConfigurationFactory<SimpleApplicationConfiguration>(
            SimpleApplicationConfiguration.class, objectMapper, validator);
    
    SimpleApplicationConfiguration config = configFactory.build(provider, "simple-app.yml");
    
    System.out.println(config);
  }

}
```
