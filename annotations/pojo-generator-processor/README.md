# pojo-generator-processor

**Owner(s):** Carlos Devoto

An annotation processor that can be used to generate classes from POJO specifications.

Here is the ``build.gradle`` file for clients:

```
dependencies {
    annotationProcessor "com.resolute:pojo-generator-processor:${rbiDepVersion}" // JAR containing processor class
    compileOnly         "com.resolute:pojo-generator-annotations:${rbiDepVersion}" // JAR containing annotations 
}

// Change the default directory where the generated files will be written to ${projectDir}/src/main/generated
compileJava {
  options.annotationProcessorGeneratedSourcesDirectory = file("${projectDir}/src/main/generated")
}

// Add the ${projectDir}/src/main/generated as a source folder in Eclipse
eclipse {
    classpath {
        file.whenMerged { cp ->
            cp.entries.add( new org.gradle.plugins.ide.eclipse.model.SourceFolder('src/main/generated', null) )
        }
    }
}
```
The best practice seems to be to put the annotations in one library and the processor in another.

You should also update your ``.gitignore`` file to include the following line:

```
src/main/generated
```

To generate a set of POJOs within a specific package create a class that has the ``@PojoModule`` annotation as shown below.

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
After you set up the client project in Eclipse, you will need to perform the following steps in order invoke the annotation processor from within your IDE:

  * Within Eclipse, make sure that you have added the Gradle Nature your project by right-clicking on the node corresponding to the project in Project Explorer and selecting ``Configure > Add Gradle Nature`` from the resulting context menu.
  * From the main menu in Eclipse select ``Run > Run Configurations...`` to launch the Run Configurations dialog.
  * Within left tree pane of the Run Configurations dialog, right-click on the ``Gradle Project`` node and select ``New configuration`` from the resulting context menu.
  * Within the right pane of the Run Configurations dialog, select the ``Gradle Tasks`` tab, and set the following properties:
    * **Name:** Give your run configuration any name you want; I always use the name of the project.
    * **Gradle Tasks:** ``compileJava``
    * **Working Directory:** Click on the ``Workspace...`` button, and select your client project from the resulting dialog.
    * Make sure that ``Show Execution View`` and ``Show Console`` are both checked.
  *  Within the right pane of the Run Configurations dialog, select the ``Project Settings`` tab, and set the following properties:
    * **Gradle distribution:** Make sure the ``Gradle Wrapper`` radio button is selected.
  *  Within the right pane of the Run Configurations dialog, select the ``Arguments`` tab, and set the following properties:
    * **Program Arguments:** ``--refresh-dependencies`` (WARNING: This can severely slow your build down, so only do it if you need to pull down a more recent copy of a dependency)
    
  * Within the right pane of the Run Configurations dialog, click on the ``Run`` button that appears on the bottom-right corner.
  * After running the Gradle tasks, right-click on the project node within the Package Explorer and select ``Refresh`` within the result context menu.
  * Use this newly created Run Configuration any time you want to invoke the annotation processor from within your IDE.
  
If you wish to make modifications to the generated file:

  * Move the generated file from the ``src/main/generated`` directory to the equivalent package within the ``src/main/java`` directory.
  * Find the class containing the ``@PojoModule`` annotation, and just comment out the ``@Pojo`` annotation for the POJO specification in question. 
  * Later on, if you want to regenerate the class, delete the generated file from the ``src/main/java`` directory, uncomment the ``@Pojo`` annotation, and relaunch your Gradle Run Configuration.  

  
        
