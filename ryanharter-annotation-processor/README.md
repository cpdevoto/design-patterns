# ryanharter-annotation-processor

**Owner(s):** Carlos Devoto

Source code for the annotation processor tutorial found [here](https://www.youtube.com/watch?v=IPlDL4EsY08).

Here is the ``build.gradle`` file for clients:

```
dependencies {
    annotationProcessor "com.resolute:ryanharter-annotation-processor:${rbiDepVersion}" // JAR containing processor class
    compileOnly         "com.resolute:ryanharter-annotation-processor:${rbiDepVersion}" // JAR containing annotations 
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

After you set up the client project in Eclipse, you will need to perform the following steps in order invoke the annotation processor from within your IDE:

  * Within Eclipse, make sure that you have added the Gradle Nature your project by right-clicking on the node corresponding to the project in Project Explorer and selecting ``Configure > Add Gradle Nature`` from the resulting context menu.
  * From the main menu in Eclipse select ``Run > Run Configurations...`` to launch the Run Configurations dialog.
  * Within left tree pane of the Run Configurations dialog, right-click on the ``Gradle Project`` node and select ``New configuration`` from the resulting context menu.
  * Within the right pane of the Run Configurations dialog, select the ``Gradle Tasks`` tab, and set the following properties:
    * **Name:** Give your run configuration any name you want; I always use the name of the project.
    * **Gradle Tasks:** clean build
    * **Working Directory:** Click on the ``Workspace...`` button, and select your client project from the resulting dialog.
    * Make sure that ``Show Execution View`` and ``Show Console`` are both checked.
  *  Within the right pane of the Run Configurations dialog, select the ``Project Settings`` tab, and set the following properties:
    * **Gradle distribution:** Make sure the ``Gradle Wrapper`` radio button is selected.
  * Within the right pane of the Run Configurations dialog, click on the ``Run`` button that appears on the bottom-right corner.
  * Use this newly created Run Configuration any time you want to invoke the annotation processor from within your IDE.
  
  
        
