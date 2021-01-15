# ryanharter-annotation-processor

**Owner(s):** Carlos Devoto

Source code for the annotation processor tutorial found [here](https://www.youtube.com/watch?v=IPlDL4EsY08).

Here is the ``build.gradle`` file for clients:

```
// Eclipse Integration --> Running ./gradlew cleanEclipse eclipse will automatically enable your annotation processor in Eclipse! 
plugins {
  id 'net.ltgt.apt' version '0.21'
  id 'net.ltgt.apt-eclipse' version '0.21'   
}

apply plugin: 'eclipse'
// End Eclipse Integration

dependencies {
    annotationProcessor "com.resolute:ryanharter-annotation-processor:${rbiDepVersion}" // JAR containing processor class
    compileOnly         "com.resolute:ryanharter-annotation-processor:${rbiDepVersion}" // JAR containing annotations 
}

// Change the default directory where the generated files will be written to ${projectDir}/src/main/generated
compileJava {
  options.annotationProcessorGeneratedSourcesDirectory = file("${projectDir}/src/main/generated")
}

// EclipseIntegration -> Add the ${projectDir}/src/main/generated as a source folder in Eclipse
eclipse {
    classpath {
        file.whenMerged { cp ->
            cp.entries.add( new org.gradle.plugins.ide.eclipse.model.SourceFolder('src/main/generated', null) )
        }
    }
}
```
The best practice seems to be to put the annotations in one library and the processor in another.

You should also update your ``.gitignore`` file to include the following lines:

```
src/main/generated
.factorypath
```
To generate a Builder companion class for a specific POJO class, create the POJO class so that it has the ``@PojoModule`` annotation as shown below:

```
package com.resolute.user;

import com.ryanharter.example.annotations.Builder;

@Builder
public class User {
  String username;
  String firstname;
  String lastname;
  int age;

  public String getUsername() {
    return username;
  }

  public String getFirstname() {
    return firstname;
  }

  public String getLastname() {
    return lastname;
  }

  public int getAge() {
    return age;
  }
}
```
After you set up the client project in Eclipse, your annotation processor will run automatically any time you save changes to a file containing the ``@Builder`` annotation.
  
If you wish to make modifications to the generated file:

  * Move the generated file from the ``src/main/generated`` directory to the equivalent package within the ``src/main/java`` directory.
  * Find the class containing the ``@Builder`` annotation, and just comment out the ``@Builder`` annotation. 
  * Later on, if you want to regenerate the class, delete the generated file from the ``src/main/java`` directory, uncomment the ``@Builder`` annotation, and save your changes.  

  
        
