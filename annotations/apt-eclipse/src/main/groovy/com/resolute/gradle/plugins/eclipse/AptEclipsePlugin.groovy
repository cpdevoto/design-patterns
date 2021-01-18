package com.resolute.gradle.plugins.eclipse

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class AptEclipsePlugin implements Plugin<Project> {
  Logger logger = LoggerFactory.getLogger(AptEclipsePlugin.class.name)

  @Override
  public void apply(Project project) {
    logger.quiet('Applying '+ this.class.name + ' to project ' + project.name);

    int gradleMajorVersion = getMajorVersion(project.getGradle().getGradleVersion());
    int gradleMinorVersion = getMinorVersion(project.getGradle().getGradleVersion());
    logger.quiet('Gradle Major Version: '+ gradleMajorVersion);
    logger.quiet('Gradle Minor Version: '+ gradleMinorVersion);

    if (gradleMajorVersion < 4 || gradleMajorVersion == 4 && gradleMinorVersion < 3) {
      logger.quiet('The '+ this.class.name + ' requires a minimum Gradle version of 4.3 to run!');
      return;
    }
    project.pluginManager.apply("java-library")
    project.pluginManager.apply("eclipse")
    project.pluginManager.apply("net.ltgt.apt-eclipse")

    project.mkdir("${project.projectDir}/src/main/generated")

    project.compileJava {
      options.annotationProcessorGeneratedSourcesDirectory = project.file("${project.projectDir}/src/main/generated")
    }

    project.eclipse {
      classpath {
        file.whenMerged { cp ->
          cp.entries.add( new org.gradle.plugins.ide.eclipse.model.SourceFolder('src/main/generated', null) )
        }
      }
      jdt {
        apt {
          genSrcDir = project.file("${project.projectDir}/src/main/generated")
        }
      }
    }
  }

  private int getMajorVersion(String version) {
    int firstDot = version.indexOf('.');
    return Integer.parseInt(version.substring(0, firstDot))
  }
  private int getMinorVersion(String version) {
    int firstDot = version.indexOf('.');
    int secondDot = version.indexOf('.', firstDot + 1)
    if (secondDot == -1) {
      return Integer.parseInt(version.substring(firstDot + 1))
    }
    return Integer.parseInt(version.substring(firstDot + 1, secondDot))
  }
}
