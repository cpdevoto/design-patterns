package com.resolute.utils.simple.pojo_generator;

import static java.util.Objects.requireNonNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

class SourceGenerator {
  private static final List<String> primitives = ImmutableList.copyOf(Arrays.asList(
      "boolean", "byte", "char", "short", "int", "long", "float", "double"));

  // @formatter: off
  private static final Map<String, String> primitiveWrappers =
      ImmutableMap.<String, String>builder()
          .put("boolean", "Boolean")
          .put("byte", "Byte")
          .put("char", "Character")
          .put("short", "Short")
          .put("int", "Integer")
          .put("long", "Long")
          .put("float", "Float")
          .put("double", "Double")
          .build();
  // @formatter: on


  private final Pojo pojo;
  private final SourceBuffer buf = new SourceBuffer();
  private final PojoFlags flags;

  SourceGenerator(Pojo pojo) {
    this.pojo = requireNonNull(pojo, "pojo cannot be null");
    this.flags = new PojoFlags(pojo);
  }

  public String generate() {
    generatePackageStatement();
    generateStaticImports();
    generateImports();
    generateClassDeclaration();
    generateClassDataMembers();
    generateStaticFactoryMethods();
    generateClassConstructor();
    generateClassGetterMethods();
    generateBuilderDeclaration();
    generateBuilderDataMembers();
    generateBuilderConstructors();
    generateBuilderBulkSetterMethod();
    generateBuilderSetterMethods();
    generateBuilderBuildMethod();
    generateEndOfBuilder();
    generateEndOfClass();
    return buf.toString();

  }

  // Code generation methods

  private void generatePackageStatement() {
    buf.print("package ").print(pojo.getPackageName()).println(";");
    buf.println();
  }

  private void generateStaticImports() {
    buf.println("import static java.util.Objects.requireNonNull;");
    buf.println();
  }

  private void generateImports() {
    buf.println("import java.util.function.Consumer;");
    if (pojo.getJacksonAnnotations()) {
      buf.println("import com.fasterxml.jackson.annotation.JsonCreator;");
      buf.println("import com.fasterxml.jackson.annotation.JsonInclude;");
      buf.println("import com.fasterxml.jackson.annotation.JsonInclude.Include;");
      buf.println("import com.fasterxml.jackson.databind.annotation.JsonDeserialize;");
      buf.println("import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;");
    }
    if (flags.get(PojoFlag.HAS_LIST)) {
      buf.println("import java.util.List;");
      buf.println("import com.google.common.collect.ImmutableList;");
    }
    if (flags.get(PojoFlag.HAS_MAP)) {
      buf.println("import java.util.Map;");
      buf.println("import com.google.common.collect.ImmutableMap;");
    }
    if (flags.get(PojoFlag.HAS_SET)) {
      buf.println("import java.util.Set;");
      buf.println("import com.google.common.collect.ImmutableSet;");
    }
    if (flags.get(PojoFlag.HAS_LOCAL_DATE)) {
      buf.println("import java.time.LocalDate;");
    }
    if (flags.get(PojoFlag.HAS_LOCAL_DATE_TIME)) {
      buf.println("import java.time.LocalDateTime;");
    }
    if (flags.get(PojoFlag.HAS_OPTIONAL)) {
      buf.println("import java.util.Optional;");
    }
    buf.println();
  }

  private void generateClassDeclaration() {
    if (pojo.getJacksonAnnotations()) {
      buf.println("@JsonInclude(Include.NON_NULL)");
      buf.print("@JsonDeserialize(builder = ").print(pojo.getClassName())
          .println(".Builder.class)");
    }
    buf.print("public class ").print(pojo.getClassName()).println(" {");
  }

  private void generateClassDataMembers() {
    buf.increaseIndent();
    pojo.getDataMembers().stream()
        .forEach(dm -> buf.indentAndPrint("private final ").print(getDataType(dm)).print(" ")
            .print(dm.getName()).println(";"));
    buf.println();
  }

  private void generateStaticFactoryMethods() {
    generateNoArgStaticFactoryMethod();
    generateCopyStaticFactoryMethod();
  }

  private void generateNoArgStaticFactoryMethod() {
    if (pojo.getJacksonAnnotations()) {
      buf.indentAndPrintln("@JsonCreator");
    }
    buf.indentAndPrintln("public static Builder builder () {");
    buf.increaseIndent().indentAndPrintln("return new Builder();");
    buf.decreaseIndent().indentAndPrintln("}");
    buf.println();
  }

  private void generateCopyStaticFactoryMethod() {
    buf.indentAndPrint("public static Builder builder (").print(pojo.getClassName()).print(" ")
        .print(toCamel(pojo.getClassName())).println(") {");
    buf.increaseIndent().indentAndPrint("return new Builder(").print(toCamel(pojo.getClassName()))
        .println(");");
    buf.decreaseIndent().indentAndPrintln("}");
    buf.println();
  }

  private void generateClassConstructor() {
    buf.indentAndPrint("private ").print(pojo.getClassName()).println(" (Builder builder) {");
    buf.increaseIndent();
    pojo.getDataMembers().stream()
        .forEach(dm -> buf.indentAndPrint("this.").print(dm.getName()).print(" = builder.")
            .print(dm.getName()).println(";"));
    buf.decreaseIndent().indentAndPrintln("}");
    buf.println();
  }

  private void generateClassGetterMethods() {
    pojo.getDataMembers().stream()
        .forEach(dm -> {
          buf.indentAndPrint("public ").print(getOptionalDataType(dm)).print(" get")
              .print(toHungarian(dm.getName())).println("() {");
          buf.increaseIndent().indentAndPrint("return ").print(toOptional(dm)).println(";");
          buf.decreaseIndent().indentAndPrintln("}");
          buf.println();
        });
  }

  private void generateBuilderDeclaration() {
    if (pojo.getJacksonAnnotations()) {
      buf.indentAndPrintln("@JsonPOJOBuilder");
    }
    buf.indentAndPrintln("public static class Builder {");
  }

  private void generateBuilderDataMembers() {
    buf.increaseIndent();
    pojo.getDataMembers().stream()
        .forEach(dm -> buf.indentAndPrint("private ").print(getDataType(dm, true)).print(" ")
            .print(dm.getName()).println(";"));
    buf.println();
  }

  private void generateBuilderConstructors() {
    generateBuilderNoArgConstructor();
    generateBuilderCopyConstructor();
  }

  private void generateBuilderNoArgConstructor() {
    buf.indentAndPrintln("private Builder() {}");
    buf.println();
  }

  private void generateBuilderCopyConstructor() {
    buf.indentAndPrint("private Builder(").print(pojo.getClassName()).print(" ")
        .print(toCamel(pojo.getClassName())).println(") {");
    buf.increaseIndent();
    buf.indentAndPrint("requireNonNull(").print(toCamel(pojo.getClassName())).print(", \"")
        .print(toCamel(pojo.getClassName())).println(" cannot be null\");");
    pojo.getDataMembers().stream()
        .forEach(dm -> buf.indentAndPrint("this.").print(dm.getName()).print(" = ")
            .print(toCamel(pojo.getClassName())).print(".")
            .print(dm.getName()).println(";"));
    buf.decreaseIndent().indentAndPrintln("}");
    buf.println();
  }

  private void generateBuilderBulkSetterMethod() {
    buf.indentAndPrintln("public Builder with(Consumer<Builder> consumer) {");
    buf.increaseIndent().indentAndPrintln("requireNonNull(consumer, \"consumer cannot be null\");");
    buf.indentAndPrintln("consumer.accept(this);");
    buf.indentAndPrintln("return this;");
    buf.decreaseIndent().indentAndPrintln("}");
    buf.println();
  }

  private void generateBuilderSetterMethods() {
    pojo.getDataMembers().stream()
        .forEach(dm -> {
          buf.indentAndPrint("public Builder with").print(toHungarian(dm.getName())).print("(")
              .print(!dm.getRequired() ? getDataType(dm) : dm.getDataType()).print(" ")
              .print(dm.getName()).println(") {");
          buf.increaseIndent();
          if (dm.getRequired() && !primitives.contains(dm.getDataType())) {
            buf.indentAndPrint("requireNonNull(").print(dm.getName()).print(", \"")
                .print(dm.getName())
                .println(" cannot be null\");");
          }
          buf.indentAndPrint("this.").print(dm.getName()).print(" = ");
          if (PojoFlag.HAS_LIST.predicate.test(dm)) {
            nullCollectionCheck(dm,
                () -> buf.print("ImmutableList.copyOf(").print(dm.getName()).print(")"));
          } else if (PojoFlag.HAS_MAP.predicate.test(dm)) {
            nullCollectionCheck(dm,
                () -> buf.print("ImmutableMap.copyOf(").print(dm.getName()).print(")"));
          } else if (PojoFlag.HAS_SET.predicate.test(dm)) {
            nullCollectionCheck(dm,
                () -> buf.print("ImmutableSet.copyOf(").print(dm.getName()).print(")"));
          } else {
            buf.print(dm.getName());
          }
          buf.println(";");
          buf.indentAndPrintln("return this;");
          buf.decreaseIndent().indentAndPrintln("}");
          buf.println();
        });
  }

  private void generateBuilderBuildMethod() {
    buf.indentAndPrint("public ").print(pojo.getClassName()).println(" build() {");
    buf.increaseIndent();
    pojo.getDataMembers().stream()
        .forEach(dm -> {
          if (dm.getRequired()) {
            buf.indentAndPrint("requireNonNull(").print(dm.getName()).print(", \"")
                .print(dm.getName())
                .println(" cannot be null\");");
          }
        });
    buf.indentAndPrint("return new ").print(pojo.getClassName()).println("(this);");
    buf.decreaseIndent().indentAndPrintln("}");
  }

  private void generateEndOfBuilder() {
    buf.decreaseIndent().indentAndPrintln("}");
  }

  private void generateEndOfClass() {
    buf.decreaseIndent().indentAndPrintln("}");
  }

  // Helper methods

  private void nullCollectionCheck(PojoDataMember dm, Runnable assigner) {
    if (!dm.getRequired()) {
      buf.print("(").print(dm.getName()).print(" == null ? null : ");
    }
    assigner.run();
    if (!dm.getRequired()) {
      buf.print(")");
    }
  }

  private String toCamel(String s) {
    return Character.toLowerCase(s.charAt(0)) + (s.length() > 1 ? s.substring(1) : "");
  }

  private String toHungarian(String s) {
    return Character.toUpperCase(s.charAt(0)) + (s.length() > 1 ? s.substring(1) : "");
  }

  private String getOptionalDataType(PojoDataMember dm) {
    String dataType = getDataType(dm);
    if (!dm.getRequired()) {
      dataType = "Optional<" + dataType + ">";
    }
    return dataType;
  }

  private String toOptional(PojoDataMember dm) {
    String value = dm.getName();
    if (!dm.getRequired()) {
      value = "Optional.ofNullable(" + value + ")";
    }
    return value;
  }

  private String getDataType(PojoDataMember dm) {
    return getDataType(dm, false);
  }

  private String getDataType(PojoDataMember dm, boolean forceWrapper) {
    String dataType = dm.getDataType();
    if (!dm.getRequired()) {
      if (primitives.contains(dataType)) {
        dataType = primitiveWrappers.get(dataType);
      }
    }
    if (forceWrapper && primitives.contains(dataType)) {
      dataType = primitiveWrappers.get(dataType);
    }
    return dataType;
  }

  private static enum PojoFlag {
    // @formatter:off
    HAS_LOCAL_DATE(dm -> "LocalDate".equals(dm.getDataType())), 
    HAS_LOCAL_DATE_TIME(dm -> "LocalDateTime".equals(dm.getDataType())), 
    HAS_DATE(dm -> "Date".equals(dm.getDataType())), 
    HAS_MAP(dm -> dm.getDataType().equals("Map") ||
                  dm.getDataType().startsWith("Map<") ||
                  dm.getDataType().startsWith("Map ")), 
    HAS_SET(dm -> dm.getDataType().equals("Set") ||
                  dm.getDataType().startsWith("Set<") ||
                  dm.getDataType().startsWith("Set ")), 
    HAS_LIST(dm -> dm.getDataType().equals("List") ||
                   dm.getDataType().startsWith("List<") ||
                   dm.getDataType().startsWith("List ")),
    HAS_OPTIONAL(dm -> !dm.getRequired());
    // @formatter:on

    private final Predicate<PojoDataMember> predicate;

    private PojoFlag(Predicate<PojoDataMember> predicate) {
      this.predicate = predicate;
    }

  }

  private static class PojoFlags {
    private final Map<PojoFlag, Boolean> flags;

    private PojoFlags(Pojo pojo) {
      Map<PojoFlag, Boolean> flagMap = Maps.newHashMap();
      pojo.getDataMembers().stream()
          .forEach(dm -> {
            Arrays.stream(PojoFlag.values())
                .forEach(flag -> {
                  boolean newValue = flag.predicate.test(dm);
                  flagMap.compute(flag, (f, v) -> (v == null) ? newValue : v || newValue);
                });
          });
      flags = ImmutableMap.copyOf(flagMap);
    }

    public boolean get(PojoFlag flag) {
      return flags.get(flag);
    }

  }

}
