# apt-eclipse

**Owner(s):** Carlos Devoto

A custom Gradle plugin that can be used to automatically configure annotation processing in Eclipse. 

This plugin requires Gradle version 4.3 or greater to work.

## Sample Use

To use the ``com.resolute.apt-eclipse`` plugin in your project, add the following lines to your ``build.gradle`` file:

```
buildscript {
        dependencies {
            classpath 'com.resolute:apt-eclipse:3.+'
        }
    }
}

apply plugin: 'com.resolute.apt-eclipse'
```

This will do nothing useful unless you have also configured at least one annotation processor as shown below:

```
dependencies {
    annotationProcessor "com.resolute:pojo-generator-processor:${rbiDepVersion}"
    compileOnly         "com.resolute:pojo-generator-annotations:${rbiDepVersion}"
}
```

# What does it do?

The ``com.resolute.apt-eclipse`` automatically performs the following actions:

  * Creates the ``main/src/generated`` subdirectory within your project root directory.
  * Configures ``main/src/generated`` as the generated sources directory for annotation processors that are executed as part of the ``compileJava`` Gradle task.
  * Configures ``Eclipse`` to run all annotation processors registered as ``annotationProcessor`` dependencies in ``build.gradle`` whenever you save files within your project in Eclipse, or whenever you clean your project in Eclipse.
  * Configures ``main/src/generated`` as the generated sources directory for annotation processors running within Eclipse.
  * Configures ``main/src/generated`` as a source folder within Eclipse.




