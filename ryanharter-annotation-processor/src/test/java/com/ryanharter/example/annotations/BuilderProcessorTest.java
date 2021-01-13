package com.ryanharter.example.annotations;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.Compiler.javac;
import static com.google.testing.compile.JavaSourcesSubjectFactory.javaSources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import javax.tools.JavaFileObject;

import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableSet;
import com.google.testing.compile.Compilation;
import com.google.testing.compile.JavaFileObjects;

public class BuilderProcessorTest {

  @Test
  public void test_generates_builder() {
    JavaFileObject userSource = JavaFileObjects.forSourceString("com.resolute.user.User", ""
        + "package com.resolute.user;\n"
        + "\n"
        + "import com.ryanharter.example.annotations.Builder;\n"
        + "\n"
        + "@Builder\n"
        + "public class User {\n"
        + "  String username;\n"
        + "  String firstname;\n"
        + "  String lastname;\n"
        + "  int age;\n"
        + "\n"
        + "  public String getUsername() {\n"
        + "    return username;\n"
        + "  }\n"
        + "\n"
        + "  public String getFirstname() {\n"
        + "    return firstname;\n"
        + "  }\n"
        + "\n"
        + "  public String getLastname() {\n"
        + "    return lastname;\n"
        + "  }\n"
        + "\n"
        + "  public int getAge() {\n"
        + "    return age;\n"
        + "  }\n"
        + "}");

    JavaFileObject expected = JavaFileObjects.forSourceString("com.resolute.user.UserBuilder",
        "package com.resolute.user;\n" +
            "\n" +
            "import java.lang.String;\n" +
            "\n" +
            "public final class UserBuilder {\n" +
            "  private String username;\n" +
            "\n" +
            "  private String firstname;\n" +
            "\n" +
            "  private String lastname;\n" +
            "\n" +
            "  private int age;\n" +
            "\n" +
            "  public UserBuilder username(String username) {\n" +
            "    this.username = username;\n" +
            "    return this;\n" +
            "  }\n" +
            "\n" +
            "  public UserBuilder firstname(String firstname) {\n" +
            "    this.firstname = firstname;\n" +
            "    return this;\n" +
            "  }\n" +
            "\n" +
            "  public UserBuilder lastname(String lastname) {\n" +
            "    this.lastname = lastname;\n" +
            "    return this;\n" +
            "  }\n" +
            "\n" +
            "  public UserBuilder age(int age) {\n" +
            "    this.age = age;\n" +
            "    return this;\n" +
            "  }\n" +
            "\n" +
            "  public User build() {\n" +
            "    User user = new User();\n" +
            "    user.username = this.username;\n" +
            "    user.firstname = this.firstname;\n" +
            "    user.lastname = this.lastname;\n" +
            "    user.age = this.age;\n" +
            "    return user;\n" +
            "  }\n" +
            "}\n" +
            "");

    printGeneratedSource(userSource);

    assertAbout(javaSources())
        .that(ImmutableSet.of(userSource))
        .processedWith(new BuilderProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expected);
  }

  private void printGeneratedSource(JavaFileObject source) {
    Compilation compilation = javac()
        .withProcessors(new BuilderProcessor())
        .compile(ImmutableSet.of(source));

    compilation.generatedSourceFiles().stream()
        .forEach(file -> {
          try (InputStream inputStream = file.openInputStream()) {
            String text = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));
            System.out.println(text);
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        });
  }

}
