package com.resolute.pojo.annotations;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourcesSubjectFactory.javaSources;
import static java.util.stream.Collectors.toMap;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.JavaFileObject;

import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableSet;
import com.google.testing.compile.JavaFileObjects;

public class AnnotationTest {

  @Test
  public void test_annotations() {
    String source =
        "package com.resolute.user;\n" +
            "\n" +
            "import com.resolute.pojo.annotations.PojoModule;\n" +
            "import com.resolute.pojo.annotations.Pojo;\n" +
            "import com.resolute.pojo.annotations.Required;\n" +
            "\n" +
            "@PojoModule\n" +
            "public class UserModel {\n" +
            "\n" +
            "  @Pojo(json = true)\n" +
            "  class Manager {\n" +
            "    @Required\n" +
            "    String username;\n" +
            "    String firstname;\n" +
            "    String lastname;\n" +
            "    @Required\n" +
            "    int numEmployees;\n" +
            "  }\n" +
            "\n" +
            "  @Pojo\n" +
            "  class Employee {\n" +
            "    @Required\n" +
            "    String username;\n" +
            "    String firstname;\n" +
            "    String lastname;\n" +
            "  }\n" +
            "\n" +
            "  @Pojo\n" +
            "  static class InvalidPojo1 {\n" +
            "    @Required\n" +
            "    String username;\n" +
            "    String firstname;\n" +
            "    String lastname;\n" +
            "  }\n" +
            "\n" +
            "  class InvalidPojo2 {\n" +
            "    @Required\n" +
            "    String username;\n" +
            "    String firstname;\n" +
            "    String lastname;\n" +
            "  }\n" +
            "\n" +
            "}";

    System.out.println(source);

    JavaFileObject userSource =
        JavaFileObjects.forSourceString("com.resolute.user.UserModel", source);

    TestProcessor processor = new TestProcessor();

    assertAbout(javaSources())
        .that(ImmutableSet.of(userSource))
        .processedWith(processor)
        .compilesWithoutError();

    assertThat(processor.getPojosFound())
        .isTrue();
  }

  // -----------------------
  // Helper Classes
  // -----------------------

  @PojoModule
  public class ExampleModule {

  }

  public class TestProcessor extends AbstractProcessor {

    private Messager messager;
    private Filer filer;
    private boolean pojosFound;

    public TestProcessor() {
      super();
    }

    public boolean getPojosFound() {
      return pojosFound;
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
      super.init(processingEnv);
      this.messager = processingEnv.getMessager();
      this.filer = processingEnv.getFiler();
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
      return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
      return ImmutableSet.of(
          PojoModule.class.getCanonicalName(),
          Pojo.class.getCanonicalName(),
          Required.class.getCanonicalName());
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
      Map<String, TypeElement> pojos = roundEnv.getElementsAnnotatedWith(PojoModule.class).stream()
          .map(module -> TypeElement.class.cast(module))
          .flatMap(this::getPojoTypes)
          .collect(toMap(type -> type.getSimpleName().toString(), Function.identity()));

      if (pojos.isEmpty()) {
        return true;
      }

      pojosFound = true;

      assertThat(pojos.keySet())
          .contains("Manager", "Employee");

      assertManager(pojos.get("Manager"));
      assertEmployee(pojos.get("Employee"));

      return true;
    }

    private void assertManager(TypeElement typeElement) {
      Pojo pojoAnnotation = typeElement.getAnnotation(Pojo.class);
      assertThat(pojoAnnotation.json()).isTrue();

      Map<String, VariableElement> members = getNonPrivateVariables(typeElement);

      assertThat(members.keySet())
          .containsOnly("username", "firstname", "lastname", "numEmployees");

      assertThat(members.get("username").getAnnotation(Required.class))
          .as("Expected Manager.username to be required").isNotNull();
      assertThat(members.get("firstname").getAnnotation(Required.class))
          .as("Expected Manager.firstname to be optional").isNull();
      assertThat(members.get("lastname").getAnnotation(Required.class))
          .as("Expected Manager.lastname to be optional").isNull();
      assertThat(members.get("numEmployees").getAnnotation(Required.class))
          .as("Expected Manager.numEmployees to be required").isNotNull();
    }

    private void assertEmployee(TypeElement typeElement) {
      Pojo pojoAnnotation = typeElement.getAnnotation(Pojo.class);
      assertThat(pojoAnnotation.json()).isFalse();

      Map<String, VariableElement> members = getNonPrivateVariables(typeElement);

      assertThat(members.keySet())
          .containsOnly("username", "firstname", "lastname");

      assertThat(members.get("username").getAnnotation(Required.class))
          .as("Expected Manager.username to be required").isNotNull();
      assertThat(members.get("firstname").getAnnotation(Required.class))
          .as("Expected Manager.firstname to be optional").isNull();
      assertThat(members.get("lastname").getAnnotation(Required.class))
          .as("Expected Manager.lastname to be optional").isNull();

    }

    private Stream<? extends TypeElement> getPojoTypes(TypeElement module) {
      return processingEnv.getElementUtils().getAllMembers(module).stream()
          .filter(this::isPojoType)
          .map(member -> TypeElement.class.cast(member));

    }

    private boolean isPojoType(Element member) {
      return TypeElement.class.isInstance(member)
          && !member.getModifiers().contains(Modifier.STATIC)
          && member.getAnnotation(Pojo.class) != null;
    }

    private Map<String, VariableElement> getNonPrivateVariables(TypeElement type) {
      return processingEnv.getElementUtils().getAllMembers(type).stream()
          .filter(member -> VariableElement.class.isInstance(member)
              && !member.getModifiers().contains(Modifier.PRIVATE))
          .map(member -> VariableElement.class.cast(member))
          .collect(toMap(member -> member.getSimpleName().toString(), Function.identity()));
    }

  }

}
