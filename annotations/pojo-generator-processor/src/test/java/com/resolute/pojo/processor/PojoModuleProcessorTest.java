package com.resolute.pojo.processor;

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
import com.resolute.utils.simple.StringUtils;

public class PojoModuleProcessorTest {

  @Test
  public void test_generates_pojo() {

    JavaFileObject userModelSource =
        JavaFileObjects.forSourceString("com.resolute.user.UserModel",
            "package com.resolute.user;\n" +
                "\n" +
                "import java.util.Map;\n" +
                "import java.util.List;\n" +
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
                "    Map<Integer, List<String>> roles;\n" +
                "    int [] ids;\n" +
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
                "}");

    JavaFileObject expectedManagerSource =
        JavaFileObjects.forSourceString("com.resolute.user.Manager",
            "package com.resolute.user;\n" +
                "\n" +
                "import static java.util.Objects.requireNonNull;\n" +
                "\n" +
                "import com.fasterxml.jackson.annotation.JsonCreator;\n" +
                "import com.fasterxml.jackson.annotation.JsonInclude;\n" +
                "import com.fasterxml.jackson.annotation.JsonInclude.Include;\n" +
                "import com.fasterxml.jackson.databind.annotation.JsonDeserialize;\n" +
                "import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;\n" +
                "import java.util.Optional;\n" +
                "import java.util.function.Consumer;\n" +
                "\n" +
                "@JsonInclude(Include.NON_NULL)\n" +
                "@JsonDeserialize(builder = Manager.Builder.class)\n" +
                "public class Manager {\n" +
                "  private final String username;\n" +
                "  private final String firstname;\n" +
                "  private final String lastname;\n" +
                "  private final int numEmployees;\n" +
                "\n" +
                "  @JsonCreator\n" +
                "  public static Builder builder () {\n" +
                "    return new Builder();\n" +
                "  }\n" +
                "\n" +
                "  public static Builder builder (Manager manager) {\n" +
                "    return new Builder(manager);\n" +
                "  }\n" +
                "\n" +
                "  private Manager (Builder builder) {\n" +
                "    this.username = builder.username;\n" +
                "    this.firstname = builder.firstname;\n" +
                "    this.lastname = builder.lastname;\n" +
                "    this.numEmployees = builder.numEmployees;\n" +
                "  }\n" +
                "\n" +
                "  public String getUsername() {\n" +
                "    return username;\n" +
                "  }\n" +
                "\n" +
                "  public Optional<String> getFirstname() {\n" +
                "    return Optional.ofNullable(firstname);\n" +
                "  }\n" +
                "\n" +
                "  public Optional<String> getLastname() {\n" +
                "    return Optional.ofNullable(lastname);\n" +
                "  }\n" +
                "\n" +
                "  public int getNumEmployees() {\n" +
                "    return numEmployees;\n" +
                "  }\n" +
                "\n" +
                "  @JsonPOJOBuilder\n" +
                "  public static class Builder {\n" +
                "    private String username;\n" +
                "    private String firstname;\n" +
                "    private String lastname;\n" +
                "    private Integer numEmployees;\n" +
                "\n" +
                "    private Builder() {}\n" +
                "\n" +
                "    private Builder(Manager manager) {\n" +
                "      requireNonNull(manager, \"manager cannot be null\");\n" +
                "      this.username = manager.username;\n" +
                "      this.firstname = manager.firstname;\n" +
                "      this.lastname = manager.lastname;\n" +
                "      this.numEmployees = manager.numEmployees;\n" +
                "    }\n" +
                "\n" +
                "    public Builder with(Consumer<Builder> consumer) {\n" +
                "      requireNonNull(consumer, \"consumer cannot be null\");\n" +
                "      consumer.accept(this);\n" +
                "      return this;\n" +
                "    }\n" +
                "\n" +
                "    public Builder withUsername(String username) {\n" +
                "      requireNonNull(username, \"username cannot be null\");\n" +
                "      this.username = username;\n" +
                "      return this;\n" +
                "    }\n" +
                "\n" +
                "    public Builder withFirstname(String firstname) {\n" +
                "      this.firstname = firstname;\n" +
                "      return this;\n" +
                "    }\n" +
                "\n" +
                "    public Builder withLastname(String lastname) {\n" +
                "      this.lastname = lastname;\n" +
                "      return this;\n" +
                "    }\n" +
                "\n" +
                "    public Builder withNumEmployees(int numEmployees) {\n" +
                "      this.numEmployees = numEmployees;\n" +
                "      return this;\n" +
                "    }\n" +
                "\n" +
                "    public Manager build() {\n" +
                "      requireNonNull(username, \"username cannot be null\");\n" +
                "      requireNonNull(numEmployees, \"numEmployees cannot be null\");\n" +
                "      return new Manager(this);\n" +
                "    }\n" +
                "  }\n" +
                "}");

    JavaFileObject expectedEmployeeSource =
        JavaFileObjects.forSourceString("com.resolute.user.Manager",
            "package com.resolute.user;\n" +
                "\n" +
                "import static java.util.Objects.requireNonNull;\n" +
                "\n" +
                "import com.google.common.collect.ImmutableMap;\n" +
                "import java.util.List;\n" +
                "import java.util.Map;\n" +
                "import java.util.Optional;\n" +
                "import java.util.function.Consumer;\n" +
                "\n" +
                "public class Employee {\n" +
                "  private final String username;\n" +
                "  private final String firstname;\n" +
                "  private final String lastname;\n" +
                "  private final Map<Integer,List<String>> roles;\n" +
                "  private final int[] ids;\n" +
                "\n" +
                "  public static Builder builder () {\n" +
                "    return new Builder();\n" +
                "  }\n" +
                "\n" +
                "  public static Builder builder (Employee employee) {\n" +
                "    return new Builder(employee);\n" +
                "  }\n" +
                "\n" +
                "  private Employee (Builder builder) {\n" +
                "    this.username = builder.username;\n" +
                "    this.firstname = builder.firstname;\n" +
                "    this.lastname = builder.lastname;\n" +
                "    this.roles = builder.roles;\n" +
                "    this.ids = builder.ids;\n" +
                "  }\n" +
                "\n" +
                "  public String getUsername() {\n" +
                "    return username;\n" +
                "  }\n" +
                "\n" +
                "  public Optional<String> getFirstname() {\n" +
                "    return Optional.ofNullable(firstname);\n" +
                "  }\n" +
                "\n" +
                "  public Optional<String> getLastname() {\n" +
                "    return Optional.ofNullable(lastname);\n" +
                "  }\n" +
                "\n" +
                "  public Optional<Map<Integer,List<String>>> getRoles() {\n" +
                "    return Optional.ofNullable(roles);\n" +
                "  }\n" +
                "\n" +
                "  public Optional<int[]> getIds() {\n" +
                "    return Optional.ofNullable(ids);\n" +
                "  }\n" +
                "\n" +
                "  public static class Builder {\n" +
                "    private String username;\n" +
                "    private String firstname;\n" +
                "    private String lastname;\n" +
                "    private Map<Integer,List<String>> roles;\n" +
                "    private int[] ids;\n" +
                "\n" +
                "    private Builder() {}\n" +
                "\n" +
                "    private Builder(Employee employee) {\n" +
                "      requireNonNull(employee, \"employee cannot be null\");\n" +
                "      this.username = employee.username;\n" +
                "      this.firstname = employee.firstname;\n" +
                "      this.lastname = employee.lastname;\n" +
                "      this.roles = employee.roles;\n" +
                "      this.ids = employee.ids;\n" +
                "    }\n" +
                "\n" +
                "    public Builder with(Consumer<Builder> consumer) {\n" +
                "      requireNonNull(consumer, \"consumer cannot be null\");\n" +
                "      consumer.accept(this);\n" +
                "      return this;\n" +
                "    }\n" +
                "\n" +
                "    public Builder withUsername(String username) {\n" +
                "      requireNonNull(username, \"username cannot be null\");\n" +
                "      this.username = username;\n" +
                "      return this;\n" +
                "    }\n" +
                "\n" +
                "    public Builder withFirstname(String firstname) {\n" +
                "      this.firstname = firstname;\n" +
                "      return this;\n" +
                "    }\n" +
                "\n" +
                "    public Builder withLastname(String lastname) {\n" +
                "      this.lastname = lastname;\n" +
                "      return this;\n" +
                "    }\n" +
                "\n" +
                "    public Builder withRoles(Map<Integer,List<String>> roles) {\n" +
                "      this.roles = (roles == null ? null : ImmutableMap.copyOf(roles));\n" +
                "      return this;\n" +
                "    }\n" +
                "\n" +
                "    public Builder withIds(int[] ids) {\n" +
                "      this.ids = ids;\n" +
                "      return this;\n" +
                "    }\n" +
                "\n" +
                "    public Employee build() {\n" +
                "      requireNonNull(username, \"username cannot be null\");\n" +
                "      return new Employee(this);\n" +
                "    }\n" +
                "  }\n" +
                "}");

    printGeneratedSource(userModelSource);

    assertAbout(javaSources())
        .that(ImmutableSet.of(userModelSource))
        .processedWith(new PojoModuleProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedManagerSource, expectedEmployeeSource);
  }

  @Test
  public void test_module_import_exclusion() {

    JavaFileObject userModelSource =
        JavaFileObjects.forSourceString("com.resolute.user.UserModel",
            "package com.resolute.user;\n" +
                "\n" +
                "import java.util.List;\n" +
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
                "    List<Department> departments;\n" +
                "  }\n" +
                "\n" +
                "  @Pojo\n" +
                "  class Department {\n" +
                "    @Required\n" +
                "    int id;\n" +
                "    @Required\n" +
                "    String name;\n" +
                "  }\n" +
                "\n" +
                "}");

    // The source for the generated Manager class should not contain an import statement for
    // com.resolute.user.UserModel.Department!
    JavaFileObject expectedManagerSource =
        JavaFileObjects.forSourceString("com.resolute.user.Manager",
            "package com.resolute.user;\n" +
                "\n" +
                "import static java.util.Objects.requireNonNull;\n" +
                "\n" +
                "import com.fasterxml.jackson.annotation.JsonCreator;\n" +
                "import com.fasterxml.jackson.annotation.JsonInclude;\n" +
                "import com.fasterxml.jackson.annotation.JsonInclude.Include;\n" +
                "import com.fasterxml.jackson.databind.annotation.JsonDeserialize;\n" +
                "import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;\n" +
                "import com.google.common.collect.ImmutableList;\n" +
                "import java.util.List;\n" +
                "import java.util.Optional;\n" +
                "import java.util.function.Consumer;\n" +
                "\n" +
                "@JsonInclude(Include.NON_NULL)\n" +
                "@JsonDeserialize(builder = Manager.Builder.class)\n" +
                "public class Manager {\n" +
                "  private final String username;\n" +
                "  private final String firstname;\n" +
                "  private final String lastname;\n" +
                "  private final List<Department> departments;\n" +
                "\n" +
                "  @JsonCreator\n" +
                "  public static Builder builder () {\n" +
                "    return new Builder();\n" +
                "  }\n" +
                "\n" +
                "  public static Builder builder (Manager manager) {\n" +
                "    return new Builder(manager);\n" +
                "  }\n" +
                "\n" +
                "  private Manager (Builder builder) {\n" +
                "    this.username = builder.username;\n" +
                "    this.firstname = builder.firstname;\n" +
                "    this.lastname = builder.lastname;\n" +
                "    this.departments = builder.departments;\n" +
                "  }\n" +
                "\n" +
                "  public String getUsername() {\n" +
                "    return username;\n" +
                "  }\n" +
                "\n" +
                "  public Optional<String> getFirstname() {\n" +
                "    return Optional.ofNullable(firstname);\n" +
                "  }\n" +
                "\n" +
                "  public Optional<String> getLastname() {\n" +
                "    return Optional.ofNullable(lastname);\n" +
                "  }\n" +
                "\n" +
                "  public List<Department> getDepartments() {\n" +
                "    return departments;\n" +
                "  }\n" +
                "\n" +
                "  @JsonPOJOBuilder\n" +
                "  public static class Builder {\n" +
                "    private String username;\n" +
                "    private String firstname;\n" +
                "    private String lastname;\n" +
                "    private List<Department> departments;\n" +
                "\n" +
                "    private Builder() {}\n" +
                "\n" +
                "    private Builder(Manager manager) {\n" +
                "      requireNonNull(manager, \"manager cannot be null\");\n" +
                "      this.username = manager.username;\n" +
                "      this.firstname = manager.firstname;\n" +
                "      this.lastname = manager.lastname;\n" +
                "      this.departments = manager.departments;\n" +
                "    }\n" +
                "\n" +
                "    public Builder with(Consumer<Builder> consumer) {\n" +
                "      requireNonNull(consumer, \"consumer cannot be null\");\n" +
                "      consumer.accept(this);\n" +
                "      return this;\n" +
                "    }\n" +
                "\n" +
                "    public Builder withUsername(String username) {\n" +
                "      requireNonNull(username, \"username cannot be null\");\n" +
                "      this.username = username;\n" +
                "      return this;\n" +
                "    }\n" +
                "\n" +
                "    public Builder withFirstname(String firstname) {\n" +
                "      this.firstname = firstname;\n" +
                "      return this;\n" +
                "    }\n" +
                "\n" +
                "    public Builder withLastname(String lastname) {\n" +
                "      this.lastname = lastname;\n" +
                "      return this;\n" +
                "    }\n" +
                "\n" +
                "    public Builder withDepartments(List<Department> departments) {\n" +
                "      requireNonNull(departments, \"departments cannot be null\");\n" +
                "      this.departments = ImmutableList.copyOf(departments);\n" +
                "      return this;\n" +
                "    }\n" +
                "\n" +
                "    public Manager build() {\n" +
                "      requireNonNull(username, \"username cannot be null\");\n" +
                "      requireNonNull(departments, \"departments cannot be null\");\n" +
                "      return new Manager(this);\n" +
                "    }\n" +
                "  }\n" +
                "}");

    printGeneratedSource(userModelSource);

    assertAbout(javaSources())
        .that(ImmutableSet.of(userModelSource))
        .processedWith(new PojoModuleProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedManagerSource);
  }

  @Test
  public void test_package_import_exclusion() {

    JavaFileObject userModelSource =
        JavaFileObjects.forSourceString("com.resolute.user.UserModel",
            "package com.resolute.user;\n" +
                "\n" +
                "import java.util.List;\n" +
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
                "    List<Department2> departments;\n" +
                "  }\n" +
                "\n" +
                "}");

    // The source for the generated Manager class should not contain an import statement for
    // com.resolute.user.UserModel.Department!
    JavaFileObject expectedManagerSource =
        JavaFileObjects.forSourceString("com.resolute.user.Manager",
            "package com.resolute.user;\n" +
                "\n" +
                "import static java.util.Objects.requireNonNull;\n" +
                "\n" +
                "import com.fasterxml.jackson.annotation.JsonCreator;\n" +
                "import com.fasterxml.jackson.annotation.JsonInclude;\n" +
                "import com.fasterxml.jackson.annotation.JsonInclude.Include;\n" +
                "import com.fasterxml.jackson.databind.annotation.JsonDeserialize;\n" +
                "import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;\n" +
                "import com.google.common.collect.ImmutableList;\n" +
                "import java.util.List;\n" +
                "import java.util.Optional;\n" +
                "import java.util.function.Consumer;\n" +
                "\n" +
                "@JsonInclude(Include.NON_NULL)\n" +
                "@JsonDeserialize(builder = Manager.Builder.class)\n" +
                "public class Manager {\n" +
                "  private final String username;\n" +
                "  private final String firstname;\n" +
                "  private final String lastname;\n" +
                "  private final List<Department2> departments;\n" +
                "\n" +
                "  @JsonCreator\n" +
                "  public static Builder builder () {\n" +
                "    return new Builder();\n" +
                "  }\n" +
                "\n" +
                "  public static Builder builder (Manager manager) {\n" +
                "    return new Builder(manager);\n" +
                "  }\n" +
                "\n" +
                "  private Manager (Builder builder) {\n" +
                "    this.username = builder.username;\n" +
                "    this.firstname = builder.firstname;\n" +
                "    this.lastname = builder.lastname;\n" +
                "    this.departments = builder.departments;\n" +
                "  }\n" +
                "\n" +
                "  public String getUsername() {\n" +
                "    return username;\n" +
                "  }\n" +
                "\n" +
                "  public Optional<String> getFirstname() {\n" +
                "    return Optional.ofNullable(firstname);\n" +
                "  }\n" +
                "\n" +
                "  public Optional<String> getLastname() {\n" +
                "    return Optional.ofNullable(lastname);\n" +
                "  }\n" +
                "\n" +
                "  public List<Department2> getDepartments() {\n" +
                "    return departments;\n" +
                "  }\n" +
                "\n" +
                "  @JsonPOJOBuilder\n" +
                "  public static class Builder {\n" +
                "    private String username;\n" +
                "    private String firstname;\n" +
                "    private String lastname;\n" +
                "    private List<Department2> departments;\n" +
                "\n" +
                "    private Builder() {}\n" +
                "\n" +
                "    private Builder(Manager manager) {\n" +
                "      requireNonNull(manager, \"manager cannot be null\");\n" +
                "      this.username = manager.username;\n" +
                "      this.firstname = manager.firstname;\n" +
                "      this.lastname = manager.lastname;\n" +
                "      this.departments = manager.departments;\n" +
                "    }\n" +
                "\n" +
                "    public Builder with(Consumer<Builder> consumer) {\n" +
                "      requireNonNull(consumer, \"consumer cannot be null\");\n" +
                "      consumer.accept(this);\n" +
                "      return this;\n" +
                "    }\n" +
                "\n" +
                "    public Builder withUsername(String username) {\n" +
                "      requireNonNull(username, \"username cannot be null\");\n" +
                "      this.username = username;\n" +
                "      return this;\n" +
                "    }\n" +
                "\n" +
                "    public Builder withFirstname(String firstname) {\n" +
                "      this.firstname = firstname;\n" +
                "      return this;\n" +
                "    }\n" +
                "\n" +
                "    public Builder withLastname(String lastname) {\n" +
                "      this.lastname = lastname;\n" +
                "      return this;\n" +
                "    }\n" +
                "\n" +
                "    public Builder withDepartments(List<Department2> departments) {\n" +
                "      requireNonNull(departments, \"departments cannot be null\");\n" +
                "      this.departments = ImmutableList.copyOf(departments);\n" +
                "      return this;\n" +
                "    }\n" +
                "\n" +
                "    public Manager build() {\n" +
                "      requireNonNull(username, \"username cannot be null\");\n" +
                "      requireNonNull(departments, \"departments cannot be null\");\n" +
                "      return new Manager(this);\n" +
                "    }\n" +
                "  }\n" +
                "}");

    printGeneratedSource(userModelSource);

    assertAbout(javaSources())
        .that(ImmutableSet.of(userModelSource))
        .processedWith(new PojoModuleProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedManagerSource);
  }

  private void printGeneratedSource(JavaFileObject source) {
    Compilation compilation = javac()
        .withProcessors(new PojoModuleProcessor())
        .compile(ImmutableSet.of(source));

    compilation.generatedSourceFiles().stream()
        .forEach(file -> {
          try (InputStream inputStream = file.openInputStream()) {
            String text = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));

            String heading = "SOURCE CODE FILE: " + file.getName();
            String hr = StringUtils.hr(heading.length());
            System.out.println(hr);
            System.out.println(heading);
            System.out.println(hr);
            System.out.println();
            System.out.println(text);
            System.out.println();
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        });
  }

}
