# homonculus-database

**Owner(s):** Carlos Devoto

The **homonculus-database** library encapsulates functionality needed to automatically create a data source that manages a pool of connections
to a relational database from a YAML configuration file.  The **homonculus-database** library is intended to be used in conjunction with a
**[homonculus](https://github.com/cpdevoto/devoware-utils/tree/master/homonculus-core)** application, and it includes a Dagger 2 module
which allows for the creation of a singleton ManagedPooledDataSource object that can be easily injected into other objects that require
access to a data source.

##Configuration class
To introduce a data source into your existing **homonculus** application, modify your main configuration class to include a ```database```
field as shown below:
```java
package com.doradosystems.simpleapp.config;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.devoware.homonculus.database.DataSourceFactory;
import org.devoware.homonculus.validators.validation.PortRange;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SimpleApplicationConfiguration {

  @NotNull
  @JsonProperty
  private String hostName;

  @PortRange
  @JsonProperty
  private int port;

  @Valid
  @NotNull
  @JsonProperty
  private DataSourceFactory database;

  public String getHostName() {
    return hostName;
  }

  public int getPort() {
    return port;
  }

  public DataSourceFactory getDatabase() {
    return database;
  }
}
```
##YAML configuration file

Once you have updated your configuration class, you can modify your YAML configuration file as shown below to configure the data source:

```yaml
# the name of the host to connect to
hostName: localhost

# the port to connect to
port: 8080

database:
  # the name of your JDBC driver
  driverClass: org.postgresql.Driver

  # the username
  user: cdevoto

  # the password
  password: 

  # the JDBC URL
  url: jdbc:postgresql://localhost:5432/cdevoto

  # any properties specific to your JDBC driver:
  properties:
    charSet: UTF-8

  # the maximum amount of time to wait on an empty pool before throwing an exception
  maxWaitForConnection: 1s

  # the SQL query to run when validating a connection's liveness
  validationQuery: "/* Database Health Check */ SELECT 1"

  # the initial number of connections
  initialSize: 1

  # the minimum number of connections to keep open
  minSize: 1

  # the maximum number of connections to keep open
  maxSize: 3

  # whether or not idle connections should be validated
  checkConnectionWhileIdle: true

  # the amount of time to sleep between runs of the idle connection validation, abandoned cleaner and idle pool resizing
  evictionInterval: 10s

  # the minimum amount of time an connection must sit idle in the pool before it is eligible for eviction
  minIdleTime: 1 minute
```
