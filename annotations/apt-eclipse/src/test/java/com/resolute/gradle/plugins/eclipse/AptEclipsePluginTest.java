package com.resolute.gradle.plugins.eclipse;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;

import org.gradle.api.Project;
import org.gradle.api.plugins.ExtensionAware;
import org.gradle.api.tasks.compile.CompileOptions;
import org.gradle.plugins.ide.eclipse.model.EclipseClasspath;
import org.gradle.plugins.ide.eclipse.model.EclipseModel;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import net.ltgt.gradle.apt.EclipseJdtApt;

public class AptEclipsePluginTest {
  private static Project project;

  @BeforeAll
  static void before_all() {
    project = ProjectBuilder.builder().build();
    project.getPluginManager().apply("com.resolute.apt-eclipse");
  }

  @Test
  void apt_eclipse_plugin_adds_multiple_plugins_to_project() {
    assertThat(project.getPlugins().getPlugin("java-library")).isNotNull();
    assertThat(project.getPlugins().getPlugin("eclipse")).isNotNull();
    assertThat(project.getPlugins().getPlugin("net.ltgt.apt-eclipse")).isNotNull();
  }

  @Test
  void apt_eclipse_plugin_creates_generated_source_directory() {
    File generatedSourceDir = new File(project.getProjectDir(), "src/main/generated");
    assertThat(generatedSourceDir)
        .exists()
        .isDirectory();
    File generatedTestSourceDir = new File(project.getProjectDir(), "src/test/generated");
    assertThat(generatedTestSourceDir)
        .exists()
        .isDirectory();
  }

  @Test
  void apt_eclipse_plugin_updates_compile_java_options() {
    File expectedGeneratedSourceDir = new File(project.getProjectDir(), "src/main/generated");
    CompileOptions options =
        (CompileOptions) project.getTasks().getByName("compileJava").property("options");
    File actualGeneratedSourceDir = options.getAnnotationProcessorGeneratedSourcesDirectory();
    assertThat(actualGeneratedSourceDir)
        .isEqualTo(expectedGeneratedSourceDir);
  }

  @Test
  void apt_eclipse_plugin_updates_compile_test_java_options() {
    File expectedGeneratedTestSourceDir = new File(project.getProjectDir(), "src/test/generated");
    CompileOptions options =
        (CompileOptions) project.getTasks().getByName("compileTestJava").property("options");
    File actualGeneratedTestSourceDir = options.getAnnotationProcessorGeneratedSourcesDirectory();
    assertThat(actualGeneratedTestSourceDir)
        .isEqualTo(expectedGeneratedTestSourceDir);
  }

  @Test
  void apt_eclipse_plugin_updates_eclipse_jdt_apt() {
    File expectedGeneratedSourceDir = new File(project.getProjectDir(), "src/main/generated");
    File expectedGeneratedTestSourceDir = new File(project.getProjectDir(), "src/test/generated");
    EclipseModel eclipse =
        (EclipseModel) project.getExtensions().getByName("eclipse");
    ExtensionAware jdt = (ExtensionAware) eclipse.getJdt();
    EclipseJdtApt apt =
        (EclipseJdtApt) jdt.getExtensions().getByName("apt");
    File actualGeneratedSourceDir = apt.getGenSrcDir();
    assertThat(actualGeneratedSourceDir)
        .isEqualTo(expectedGeneratedSourceDir);
    File actualGeneratedTestSourceDir = apt.getGenTestSrcDir();
    assertThat(actualGeneratedTestSourceDir)
        .isEqualTo(expectedGeneratedTestSourceDir);
  }

  @Test
  void apt_eclipse_plugin_updates_eclipse_classpath_to_include_new_source_folder() {
    EclipseModel eclipse =
        (EclipseModel) project.getExtensions().getByName("eclipse");
    EclipseClasspath cp = (EclipseClasspath) eclipse.getClasspath();
    assertThat(cp.getFile().getWhenMerged().isEmpty())
        .isEqualTo(false);
  }

}
