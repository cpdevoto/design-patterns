# pojo-generator-processor

**Owner(s):** Carlos Devoto

An annotation processor that can be used to generate classes from POJO specifications.  

To use incorporate this annotation processor into your build, you need Gradle 4.3+.

## Usage


Here is the ``build.gradle`` file for clients:

```
buildscript {
  repositories {
    dependencies {
      classpath "com.resolute:apt-eclipse:3.+"
    }
  }
}

apply plugin: 'com.resolute.apt-eclipse'

dependencies {
    annotationProcessor "com.resolute:pojo-generator-processor:${rbiDepVersion}" // JAR containing processor class
    compileOnly         "com.resolute:pojo-generator-annotations:${rbiDepVersion}" // JAR containing annotations 
    
    // If you want to use the @Pojo annotation with json = true, you should include the following dependency:
    implementation      "com.resolute:jackson-utils-simple:${rbiDepVersion}"
}
```

The best practice seems to be to put the annotations in one library and the processor in another.

To set up your project in Eclipse, you will need to run ``./gradlew cleanEclipse eclipse --refresh-dependencies``.  If you have compilation errors once you import the project, just run ``Project > Clean`` for the project in question to force annotation processing for all source files.  You can add a Gradle nature to your Eclipse project by right-clicking on it in the Package Explorer, and then selecting ``Configure > Add Gradle Nature``.

You should also update your ``.gitignore`` file to include the following lines:

```
src/main/generated
.factorypath
```

To generate a set of POJOs within a specific package, create a class within that package that has the ``@PojoModule`` annotation as shown below (Note that **data members which are arrays are not properly supported**! The behavior of a generated class that contains arrays is not guaranteed):

```java
package com.resolute.user;

import java.util.Map;
import java.util.List;
import com.resolute.pojo.annotations.PojoModule;
import com.resolute.pojo.annotations.Pojo;
import com.resolute.pojo.annotations.Required;

@PojoModule
public class UserModel {

  @Pojo(json = true)
  class Manager {
    @Required
    String username;
    String firstname;
    String lastname;
    @Required
    int numEmployees;
  }

  @Pojo
  class Employee {
    @Required
    String username;
    String firstname;
    String lastname;
    Map<Integer, List<String>> roles;
    int [] ids;
  }
}
```
After you set up the client project in Eclipse, your annotation processor will run automatically any time you save changes to a file containing the ``@PojoModule`` annotation.
  
If you wish to make modifications to the generated file:

  * Move the generated file from the ``src/main/generated`` directory to the equivalent package within the ``src/main/java`` directory.
  * Find the class containing the ``@PojoModule`` annotation, and just comment out the ``@Pojo`` annotation for the POJO specification in question. 
  * Later on, if you want to regenerate the class, delete the generated file from the ``src/main/java`` directory, uncomment the ``@Pojo`` annotation, and save your changes.
  
## Gradle Configuration Without com.resolute.apt-eclipse Plugin

Here is the ``build.gradle`` file for clients if you do not use the ``com.resolute.apt-eclipse`` Gradle plugin. I have included it here to help understand what the plugin is doing.

```
// Eclipse Integration --> Running ./gradlew cleanEclipse eclipse will automatically enable your annotation processor in Eclipse! 
plugins {
  id 'net.ltgt.apt-eclipse' version '0.21'   
}

apply plugin: 'eclipse'
// End Eclipse Integration

dependencies {
    annotationProcessor "com.resolute:pojo-generator-processor:${rbiDepVersion}" // JAR containing processor class
    compileOnly         "com.resolute:pojo-generator-annotations:${rbiDepVersion}" // JAR containing annotations 
    
    // If you want to use the @Pojo annotation with json = true, you should include the following dependency:
    implementation      "com.resolute:jackson-utils-simple:${rbiDepVersion}"
}

// Change the default directory where the generated files will be written to ${projectDir}/src/main/generated
mkdir "${projectDir}/src/main/generated"

compileJava {
  options.annotationProcessorGeneratedSourcesDirectory = file("${projectDir}/src/main/generated")
}

//EclipseIntegration -> Add the ${projectDir}/src/main/generated as a source folder in Eclipse,
// and also as the generated sources directory in Eclipse.
eclipse {
  classpath {
    file.whenMerged { cp ->
      cp.entries.add( new org.gradle.plugins.ide.eclipse.model.SourceFolder('src/main/generated', null) )
    }
  }
  jdt {
    apt {
      genSrcDir = file("${projectDir}/src/main/generated")
    }
  }
}
```
    

  
        
