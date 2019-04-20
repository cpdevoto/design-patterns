# homonculus-core

**Owner(s):** Carlos Devoto

The **homonculus-core** library provides a generalized framework for building java stand-alone services and microservices.  All applications built with the **homonculus** framework include the following features.

* When the application is initialized, it creates a socket server that listens for a termination sequence.  When the application is stopped,
it creates a client socket which transmits the termination sequence, thereby triggering a graceful shutdown.  In this way, **homonculus**
adds graceful shutdown semantics to an application that could otherwise only be terminated abruptly using ```kill``` or ```CTRL+C```.

* When the application is initialized, it passes an ```Environment``` object to the application class which can be used to register 
```Managed``` object with the lifecycle manager.  When the application is started, the lifecycle manager will invoke the ```start()``` of
each ```Managed``` object. Likewise, when the application is stopped, the lifecycle manager will invoke the ```stop()``` method of each
```Managed``` object.

* When the application is initialized, the framework will automatically convert the YAML configuration file specified as a command-line argument
into a corresponding Java configuration class created by you.  If the configuration class includes any validation constraints defined as
annotations, the framework will automatically attempt to validate the configuration object against these constraints, reporting any violations
it encounters and aborting the application if any violations are found (see **[Hibernate Validator](http://hibernate.org/validator/)**).

* The ```Environment``` object exposes several subcomponents needed by many Java applications. These include:
  * **Metric Registry**:  Allows application developers to define counters, timers, metrics, gauges, and histograms that are automatically
  exposed as JMX MBeans for purposes of monitoring different aspects of the application's behaviour (see **[Metrics](http://metrics.dropwizard.io/3.1.0/)**).
  * **Healthcheck Registry**: Allows application developers to define custom healthchecks that can optionally be exposed as MBeans or tested
  at regular intervals.  You might define a custom healthcheck, for instance, to test connectivity to your database at regular intervals or
  expose an MBean method that can be used to test connectivity to the database remotely(see **[Hibernate Validator](http://hibernate.org/validator/)**).
  * **Object Mapper**: An instance of a Jackson object mapper that can be used to convert Java objects to JSON and vice versa
  (see **[FasterXML/Jackson](https://github.com/FasterXML/jackson)**).
  * **Validator**: An instance of a validator object that can be used to validate objects annotated with validation constraints based on
  the Java Validation API (see **[Hibernate Validator](http://hibernate.org/validator/)**).
  * **Executor Service Builder**: A builder object that can be used to easily configure and create scheduled and basic pooled executor
  services.

## Creating a homonculus application
Since the **homonculus** framework builds on the **bootstrap** library, all standards for building **bootstrap** applications will need to 
be observed (see **[homonculus-bootstrap](https://github.com/cpdevoto/devoware-utils/tree/master/homonculus-bootstrap)**). The following additional
standards will also need to be observed.

###YAML configuration file
Every **homonculus** application is expected to include a YAML-formatted configuration file in the ```config``` directory.  All of your
application-specific configurations should be included in this file.  For instance, if you are building a simple application which
needs to connect to a server on a specific port, you might create the following ```simple-app.yml``` file in your ```config``` directory:

```yaml
# the name of the host to connect to
hostName: localhost

# the port to connect to
port: 8080
```
##Configuration class
The **homonculus** framework uses the [homonculus-config](https://github.com/cpdevoto/devoware-utils/tree/master/homonculus-config)
library in order to automatically transform your YAML configuration file into an instance of a Java class that you have created.  If the configuration class includes any validation constraints defined as
annotations, the framework will automatically attempt to validate the configuration object against these constraints, reporting any violations
it encounters and aborting the application if any violations are found (see **[Hibernate Validator](http://hibernate.org/validator/)**). 
Additional annotations and validators are provided in the **[homonculus-validators](https://github.com/cpdevoto/devoware-utils/tree/master/homonculus-validators)** library.
Continuing with the example defined above, let's define a configuration class to go with our ```simple-app.yml``` configuration file:

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
When the application is initialized, an instance of the ```SimpleApplication``` class will be created, and each of its properties that
is annotated with the ```JsonProperty``` annotation will be populated with the correspondingly named value from the ```simple-app.yml```
file. An exception will be thrown if the ```simple-app.yml``` file is missing a ```hostName``` property since the ```hostName``` field
within the configuration class is annotated with the ```NotNull``` annotation.  An exception will also be thrown if the value of the
```port``` property within the ```simple-app.yml``` file is not between 1 and 65535 since the ```port``` field within the configuration
class is annotated with the ```PortRange``` annotation.

##Application class
In addition to a configuration class, each **homonculus** application must include an application class which extends from the 
```org.devoware.homonculus.Application``` abstract base class.  This is what the application class might look like for our ```simple-app```:

```java
package com.doradosystems.simpleapp;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.devoware.homonculus.core.Application;
import org.devoware.homonculus.core.lifecycle.Managed;
import org.devoware.homonculus.core.setup.Environment;
import org.devoware.homonculus.validators.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.doradosystems.simpleapp.config.SimpleApplicationConfiguration;

public class SimpleApplication extends Application<SimpleApplicationConfiguration> {
  private static final Logger log = LoggerFactory.getLogger(SimpleApplication.class);

  private ScheduledExecutorService executorService;
  private SimpleApplicationConfiguration config;
  private Meter serverCalls;

  @Override
  public String getName() {
    return "simple-app";
  }

  @Override
  protected void initialize(SimpleApplicationConfiguration config, Environment env) {
    this.config = config;
    this.executorService = env.scheduledExecutorService("Simple Application")
        .shutdownTime(Duration.seconds(5)).build();
    this.serverCalls =
        env.metrics().meter(MetricRegistry.name(SimpleApplication.class, "server-calls"));
    env.manage(new Managed() {

      @Override
      public void start() throws Exception {
        log.info("Managed resource start() was called");
      }

      @Override
      public void stop() throws Exception {
        log.info("Managed resource stop() was called");
      }

    });
  }

  @Override
  public void start() throws Exception {
    this.executorService.scheduleAtFixedRate(() -> {
      log.info("Calling server at " + config.getHostName() + ":" + config.getPort());
      serverCalls.mark();
    } , 0, 1, TimeUnit.MINUTES);
  }
}
```
Note that the ```Application``` superclass must be parameterized with your configuration class.  This is how the framework
knows to create an instance of your configuration class from the YAML configuration file. When creating your application class, you must always override the ```getName()``` and ```initialize(T config, Environment env)``` methods.  Typically, you use the ```initialize(config, env)``` method to construct objects, to wire them together, and to register any managed resources. The ```getName()``` method must return the name of your application as it should appear in diagnostic outputs.

Other methods that may be overridden from the ```Application``` superclass include the ```start()```, ```stop()```, and ```getTerminationPort()``` methods.  The ```start()``` method is where you should place any logic to be executed when the application is starting. The ```stop()``` is for any application-level cleanup operations that you might need to perform. If you do not override
the ```getTerminationPort()```, your application will, by default, listen for a termination sequence on port 51335.  You can change this
default by returning a different value from the ```getTerminationPort()``` method.  You could, for instance, configure your application
to read the termination port from a JVM argument using ```System.getProperty()```.

## Gradle build
The following Gradle script shows all of the dependencies needed to run a **homonculus** application.  It also includes some new tasks
and task extensions that are be used to:
* Automatically generate Eclipse launch configurations to start and stop your application whenever you run ```gradle cleanEclipse eclipse --refresh-dependencies```
* Automatically create a distributable version of the application as a tar.gz file within the ```build/dist``` directory whenever you run ```gradle clean build archive```

```groovy
apply plugin: 'java'
apply plugin: 'eclipse'

group = 'com.doradosystems'
version = 1.0
sourceCompatibility = 1.8
ext.appName = 'Simple App'
ext.appClass = 'com.doradosystems.simpleapp.SimpleApplication'

repositories {
    mavenCentral()
    maven { url 'https://github.com/cpdevoto/maven-repository/raw/master/' }
}

dependencies {
    runtime     'org.devoware:homonculus-bootstrap:1.0'

    compile     'org.slf4j:log4j-over-slf4j:1.7.21',
                'org.slf4j:jcl-over-slf4j:1.7.21',
                'ch.qos.logback:logback-core:1.1.7',
                'ch.qos.logback:logback-classic:1.1.7',
                'com.google.guava:guava:19.0',
                'com.fasterxml.jackson.core:jackson-core:2.8.1',
                'com.fasterxml.jackson.core:jackson-databind:2.8.1',
                'com.fasterxml.jackson.core:jackson-annotations:2.8.1',
                'com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.8.1',
                'org.apache.commons:commons-lang3:3.4',
                'javax.validation:validation-api:1.1.0.Final',
                'org.hibernate:hibernate-validator:5.2.4.Final',
                'commons-beanutils:commons-beanutils-core:1.8.3',
                'org.glassfish:javax.el:3.0.0',
                'org.devoware:homonculus-core:1.0',
                'org.devoware:homonculus-config:1.0',
                'org.devoware:homonculus-validators:1.0',
                 'io.dropwizard.metrics:metrics-core:3.1.0',
                'io.dropwizard.metrics:metrics-healthchecks:3.1.0',
                'io.dropwizard.metrics:metrics-jvm:3.1.0'
    
    compileOnly 'com.google.code.findbugs:annotations:3.0.1'       
                
             
    testCompile 'junit:junit:4.12', 
                'org.mockito:mockito-core:1.10.19',
                'org.hamcrest:hamcrest-all:1.3'     
}

task deleteLibraries(type: Delete) {
   doFirst{
      file('lib').mkdirs()
   }
   delete file('lib').listFiles(), file('logs')
}

task copyLibraries(type: Copy) {
   doFirst{
      file('logs').mkdirs()
   }
   from configurations.compile, configurations.runtime
   into 'lib'
}

eclipse.project {
   buildCommand 'edu.umd.cs.findbugs.plugin.eclipse.findbugsBuilder'
   natures 'edu.umd.cs.findbugs.plugin.eclipse.findbugsNature'
}

tasks.cleanEclipseJdt {
   doFirst {
       delete file("bin/${appName} - Start.launch"),
	          file("bin/${appName} - Stop.launch")
   }
}


eclipse {
  classpath {
    defaultOutputDir = file('classes')
    downloadSources = true
  }
}

eclipse.classpath.file {
    whenMerged { classpath ->
        def sourcePaths = []
        classpath.entries.each {entry ->
           if (entry.hasProperty('sourcePath') && entry.sourcePath.hasProperty('path')) {
             sourcePaths.add(entry.sourcePath.path)
           }
        }
        def sourcePathsXml = ""
        sourcePaths.each {path ->
          sourcePathsXml += """&lt;container memento=&quot;&amp;lt;?xml version=&amp;quot;1.0&amp;quot; encoding=&amp;quot;UTF-8&amp;quot; standalone=&amp;quot;no&amp;quot;?&amp;gt;&amp;#10;&amp;lt;archive detectRoot=&amp;quot;true&amp;quot; path=&amp;quot;${path}&amp;quot;/&amp;gt;&amp;#10;&quot; typeId=&quot;org.eclipse.debug.core.containerType.externalArchive&quot;/&gt;&#10;"""
        }

        def launchConfig1 = 
	    file("bin/${appName} - Start.launch")
	       
	    launchConfig1.text = """\
			<?xml version="1.0" encoding="UTF-8" standalone="no"?>
			<launchConfiguration type="org.eclipse.jdt.launching.localJavaApplication">
			<listAttribute key="org.eclipse.debug.core.MAPPED_RESOURCE_PATHS">
			<listEntry value="/${project.name}"/>
			</listAttribute>
			<listAttribute key="org.eclipse.debug.core.MAPPED_RESOURCE_TYPES">
			<listEntry value="4"/>
			</listAttribute>
			<booleanAttribute key="org.eclipse.jdt.launching.ATTR_USE_START_ON_FIRST_THREAD" value="true"/>
			<stringAttribute key="org.eclipse.debug.core.source_locator_id" value="org.eclipse.jdt.launching.sourceLocator.JavaSourceLookupDirector"/>
			<stringAttribute key="org.eclipse.debug.core.source_locator_memento" value="&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot; standalone=&quot;no&quot;?&gt;&#10;&lt;sourceLookupDirector&gt;&#10;&lt;sourceContainers duplicates=&quot;false&quot;&gt;&#10;${sourcePathsXml}&lt;container memento=&quot;&amp;lt;?xml version=&amp;quot;1.0&amp;quot; encoding=&amp;quot;UTF-8&amp;quot; standalone=&amp;quot;no&amp;quot;?&amp;gt;&amp;#10;&amp;lt;javaProject name=&amp;quot;${project.name}&amp;quot;/&amp;gt;&amp;#10;&quot; typeId=&quot;org.eclipse.jdt.launching.sourceContainer.javaProject&quot;/&gt;&#10;&lt;container memento=&quot;&amp;lt;?xml version=&amp;quot;1.0&amp;quot; encoding=&amp;quot;UTF-8&amp;quot; standalone=&amp;quot;no&amp;quot;?&amp;gt;&amp;#10;&amp;lt;default/&amp;gt;&amp;#10;&quot; typeId=&quot;org.eclipse.debug.core.containerType.default&quot;/&gt;&#10;&lt;/sourceContainers&gt;&#10;&lt;/sourceLookupDirector&gt;&#10;"/>
			<listAttribute key="org.eclipse.jdt.launching.CLASSPATH">
			<listEntry value="&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot; standalone=&quot;no&quot;?&gt;&#10;&lt;runtimeClasspathEntry containerPath=&quot;org.eclipse.jdt.launching.JRE_CONTAINER/org.eclipse.jdt.internal.debug.ui.launcher.StandardVMType/JavaSE-1.8/&quot; javaProject=&quot;${project.name}&quot; path=&quot;1&quot; type=&quot;4&quot;/&gt;&#10;"/>
			<listEntry value="&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot; standalone=&quot;no&quot;?&gt;&#10;&lt;runtimeClasspathEntry internalArchive=&quot;/${project.name}/lib/homonculus-bootstrap-1.0.jar&quot; path=&quot;3&quot; type=&quot;2&quot;/&gt;&#10;"/>
			</listAttribute>
			<booleanAttribute key="org.eclipse.jdt.launching.DEFAULT_CLASSPATH" value="false"/>
			<stringAttribute key="org.eclipse.jdt.launching.MAIN_TYPE" value="org.devoware.homonculus.bootstrap.Bootstrap"/>
			<stringAttribute key="org.eclipse.jdt.launching.PROGRAM_ARGUMENTS" value="start ${project.name}.yml"/>
			<stringAttribute key="org.eclipse.jdt.launching.PROJECT_ATTR" value="${project.name}"/>
			<stringAttribute key="org.eclipse.jdt.launching.VM_ARGUMENTS" value="-Dbootstrap.class=${appClass} -Duser.timezone=GMT -Dlog.dir=../logs -Dlog.extra.appender=FILE"/>
			<stringAttribute key="org.eclipse.jdt.launching.WORKING_DIRECTORY" value="\${workspace_loc:${project.name}/bin}"/>
			</launchConfiguration>
	   """.stripIndent()

	   def launchConfig2 = 
	       file("bin/${appName} - Stop.launch")
	       
	    launchConfig2.text = """\
			<?xml version="1.0" encoding="UTF-8" standalone="no"?>
			<launchConfiguration type="org.eclipse.jdt.launching.localJavaApplication">
			<listAttribute key="org.eclipse.debug.core.MAPPED_RESOURCE_PATHS">
			<listEntry value="/${project.name}"/>
			</listAttribute>
			<listAttribute key="org.eclipse.debug.core.MAPPED_RESOURCE_TYPES">
			<listEntry value="4"/>
			</listAttribute>
			<booleanAttribute key="org.eclipse.jdt.launching.ATTR_USE_START_ON_FIRST_THREAD" value="true"/>
			<stringAttribute key="org.eclipse.debug.core.source_locator_id" value="org.eclipse.jdt.launching.sourceLocator.JavaSourceLookupDirector"/>
			<stringAttribute key="org.eclipse.debug.core.source_locator_memento" value="&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot; standalone=&quot;no&quot;?&gt;&#10;&lt;sourceLookupDirector&gt;&#10;&lt;sourceContainers duplicates=&quot;false&quot;&gt;&#10;${sourcePathsXml}&lt;container memento=&quot;&amp;lt;?xml version=&amp;quot;1.0&amp;quot; encoding=&amp;quot;UTF-8&amp;quot; standalone=&amp;quot;no&amp;quot;?&amp;gt;&amp;#10;&amp;lt;javaProject name=&amp;quot;${project.name}&amp;quot;/&amp;gt;&amp;#10;&quot; typeId=&quot;org.eclipse.jdt.launching.sourceContainer.javaProject&quot;/&gt;&#10;&lt;container memento=&quot;&amp;lt;?xml version=&amp;quot;1.0&amp;quot; encoding=&amp;quot;UTF-8&amp;quot; standalone=&amp;quot;no&amp;quot;?&amp;gt;&amp;#10;&amp;lt;default/&amp;gt;&amp;#10;&quot; typeId=&quot;org.eclipse.debug.core.containerType.default&quot;/&gt;&#10;&lt;/sourceContainers&gt;&#10;&lt;/sourceLookupDirector&gt;&#10;"/>
			<listAttribute key="org.eclipse.jdt.launching.CLASSPATH">
			<listEntry value="&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot; standalone=&quot;no&quot;?&gt;&#10;&lt;runtimeClasspathEntry containerPath=&quot;org.eclipse.jdt.launching.JRE_CONTAINER/org.eclipse.jdt.internal.debug.ui.launcher.StandardVMType/JavaSE-1.8/&quot; javaProject=&quot;${project.name}&quot; path=&quot;1&quot; type=&quot;4&quot;/&gt;&#10;"/>
			<listEntry value="&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot; standalone=&quot;no&quot;?&gt;&#10;&lt;runtimeClasspathEntry internalArchive=&quot;/${project.name}/lib/homonculus-bootstrap-1.0.jar&quot; path=&quot;3&quot; type=&quot;2&quot;/&gt;&#10;"/>
			</listAttribute>
			<booleanAttribute key="org.eclipse.jdt.launching.DEFAULT_CLASSPATH" value="false"/>
			<stringAttribute key="org.eclipse.jdt.launching.MAIN_TYPE" value="org.devoware.homonculus.bootstrap.Bootstrap"/>
			<stringAttribute key="org.eclipse.jdt.launching.PROGRAM_ARGUMENTS" value="stop"/>
			<stringAttribute key="org.eclipse.jdt.launching.PROJECT_ATTR" value="${project.name}"/>
			<stringAttribute key="org.eclipse.jdt.launching.VM_ARGUMENTS" value="-Dbootstrap.class=${appClass} -Duser.timezone=GMT -Dlog.dir=../logs -Dlog.extra.appender=FILE"/>
			<stringAttribute key="org.eclipse.jdt.launching.WORKING_DIRECTORY" value="\${workspace_loc:${project.name}/bin}"/>
			</launchConfiguration>
	   """.stripIndent()
    }
}

task createPackage(dependsOn: build) {
    doLast {
        File dist = mkdir("${buildDir}/dist")
        File tmp = mkdir("${buildDir}/tmp/dist/${project.name}-${project.version}")
        tasks.withType(Jar).each { archiveTask ->
            copy {
                from archiveTask.archivePath
                into file("${tmp}/lib")
            }
        }
        copy {
            from configurations.compile, configurations.runtime
            into file("${tmp}/lib")
        }
        copy {
            from 'bin'
            into file("${tmp}/bin")
            exclude { details ->
                details.file.name.endsWith('.launch')
            }
        }
        copy {
            from 'config'
            into file("${tmp}/config")
        }
        mkdir("${tmp}/logs")
    }
}

task archive(dependsOn: createPackage, type: Tar) {
    File dist = file("${buildDir}/dist")
    File tmp = file("${buildDir}/tmp/dist")

    baseName = "${project.name}-${project.version}"
    destinationDir = dist
    from tmp
    compression = Compression.GZIP
}

tasks.clean.dependsOn(deleteLibraries)
tasks.cleanEclipse.dependsOn(deleteLibraries)
tasks.build.dependsOn(copyLibraries) 
tasks.eclipse.dependsOn(copyLibraries) 
```
When creating your own application, you will need to modify the values for the **```ext.appName```** and the **```ext.appClass```** variables. In the future, it would be nice to externalize some of this common logic into a custom Gradle plugin.
