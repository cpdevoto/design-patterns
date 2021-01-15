package com.ryanharter.example.annotations;

import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;

import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

// Google AutoService is used to automatically generate
// the META-INF/services/javax.annotation.processing.Processor file.
@AutoService(Processor.class)
public class BuilderProcessor extends AbstractProcessor {

  private Messager messager;
  private Filer filer;

  public BuilderProcessor() {
    super();
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
    return ImmutableSet.of(Builder.class.getCanonicalName());
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    for (Element el : roundEnv.getElementsAnnotatedWith(Builder.class)) {

      process(el);
    }

    return true;
  }

  // ------------------
  // Helper Functions
  // ------------------


  private void process(Element el) {
    // 1. get element metadata

    TypeElement type = TypeElement.class.cast(el);
    String packageName = getPackageName(type);
    String targetName = lowerCamelCase(type.getSimpleName().toString());
    Set<VariableElement> vars = getNonPrivateVariables(type);

    String builderName = String.format("%sBuilder", type.getSimpleName());
    ClassName builderType = ClassName.get(packageName, builderName);


    // 2. create private fields and public setters

    List<FieldSpec> fields = Lists.newArrayListWithCapacity(vars.size());
    List<MethodSpec> setters = Lists.newArrayListWithCapacity(vars.size());

    for (VariableElement var : vars) {
      TypeName typeName = TypeName.get(var.asType());
      String name = var.getSimpleName().toString();

      // create the field
      fields.add(FieldSpec.builder(typeName, name, PRIVATE).build());

      // create the setter
      setters.add(MethodSpec.methodBuilder(name)
          .addModifiers(PUBLIC)
          .returns(builderType)
          .addParameter(typeName, name)
          .addStatement("this.$N = $N", name, name)
          .addStatement("return this")
          .build());
    }


    // 3. create the build method

    TypeName targetType = TypeName.get(type.asType());
    MethodSpec.Builder buildMethodBuilder =
        MethodSpec.methodBuilder("build")
            .addModifiers(PUBLIC)
            .returns(targetType)
            .addStatement("$1T $2N = new $1T()", targetType, targetName);

    for (FieldSpec field : fields) {
      buildMethodBuilder
          .addStatement("$1N.$2N = this.$2N", targetName, field);
    }

    buildMethodBuilder.addStatement("return $N", targetName);
    MethodSpec buildMethod = buildMethodBuilder.build();


    // 4. create the builder type

    TypeSpec builder = TypeSpec.classBuilder(builderType)
        .addModifiers(PUBLIC, FINAL)
        .addFields(fields)
        .addMethods(setters)
        .addMethod(buildMethod)
        .build();

    // write the java source file

    JavaFile file = JavaFile
        .builder(builderType.packageName(), builder)
        .build();

    try {
      file.writeTo(filer);
    } catch (IOException e) {
      messager.printMessage(Diagnostic.Kind.ERROR, "Failed to write file for element", el);
    }
  }

  private String getPackageName(TypeElement type) {
    PackageElement packageElement = processingEnv.getElementUtils().getPackageOf(type);
    return packageElement.getQualifiedName().toString();
  }

  private String lowerCamelCase(String s) {
    return s.isEmpty() ? s : s.substring(0, 1).toLowerCase() + s.substring(1);
  }

  private Set<VariableElement> getNonPrivateVariables(TypeElement type) {
    return processingEnv.getElementUtils().getAllMembers(type).stream()
        .filter(member -> VariableElement.class.isInstance(member)
            && !member.getModifiers().contains(Modifier.PRIVATE))
        .map(member -> VariableElement.class.cast(member))
        .collect(Collectors.toCollection(LinkedHashSet::new));
  }

}
