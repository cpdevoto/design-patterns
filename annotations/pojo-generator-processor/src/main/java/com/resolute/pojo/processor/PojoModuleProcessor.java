package com.resolute.pojo.processor;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableSet;
import com.resolute.pojo.annotations.Pojo;
import com.resolute.pojo.annotations.PojoModule;
import com.resolute.pojo.annotations.Required;
import com.resolute.pojo.processor.types.DataTypeParser;
import com.resolute.utils.simple.pojo_generator.DataType;
import com.resolute.utils.simple.pojo_generator.PojoDataMemberBuilder;
import com.resolute.utils.simple.pojo_generator.PojoGenerator;
import com.squareup.javapoet.TypeName;

@AutoService(Processor.class)
public class PojoModuleProcessor extends AbstractProcessor {

  private Messager messager;
  private Filer filer;

  public PojoModuleProcessor() {
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
    return ImmutableSet.of(
        PojoModule.class.getCanonicalName(),
        Pojo.class.getCanonicalName(),
        Required.class.getCanonicalName());
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    Set<TypeElement> pojos = roundEnv.getElementsAnnotatedWith(PojoModule.class).stream()
        .map(module -> TypeElement.class.cast(module))
        .flatMap(this::getPojoTypes)
        .collect(toSet());

    if (pojos.isEmpty()) {
      return true;
    }

    pojos.stream().forEach(this::processPojo);
    return true;
  }

  private void processPojo(TypeElement pojo) {
    String packageName = getPackageName(pojo);
    String className = pojo.getSimpleName().toString();
    boolean jacksonAnnotations = pojo.getAnnotation(Pojo.class).json();
    Set<VariableElement> memberElements = getNonPrivateVariables(pojo);
    if (memberElements.isEmpty()) {
      // The POJO class has no non-private, non-static data members, so we won't bother to generate
      // the source code!
      messager.printMessage(Diagnostic.Kind.WARNING,
          "The following POJO spec has no data members "
              + "so we will not bother to generate any source code.",
          pojo);
      return;
    }


    PojoGenerator generator = PojoGenerator.forClass(className)
        .inPackage(packageName);
    if (jacksonAnnotations) {
      generator.jacksonAnnotations();
    }

    List<PojoDataMemberBuilder> dataMemberBuilders = memberElements.stream()
        .map(this::toDataMemberBuilder)
        .collect(toList());

    PojoDataMemberBuilder[] dataMemberBuilderArray =
        dataMemberBuilders.toArray(new PojoDataMemberBuilder[dataMemberBuilders.size()]);

    generator.dataMembers(dataMemberBuilderArray);

    String source = generator.generate();

    try {
      JavaFileObject pojoFile =
          filer.createSourceFile(String.format("%s.%s", packageName, className));
      try (PrintWriter out = new PrintWriter(pojoFile.openWriter())) {
        out.print(source);
      }
    } catch (IOException e) {

    }
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

  private Set<VariableElement> getNonPrivateVariables(TypeElement type) {
    return processingEnv.getElementUtils().getAllMembers(type).stream()
        .filter(member -> VariableElement.class.isInstance(member)
            && !member.getModifiers().contains(Modifier.PRIVATE)
            && !member.getModifiers().contains(Modifier.STATIC))
        .map(member -> VariableElement.class.cast(member))
        .collect(Collectors.toCollection(LinkedHashSet::new));
  }

  private String getPackageName(TypeElement type) {
    PackageElement packageElement = processingEnv.getElementUtils().getPackageOf(type);
    return packageElement.getQualifiedName().toString();
  }

  private PojoDataMemberBuilder toDataMemberBuilder(VariableElement member) {
    return PojoGenerator.dataMember(dm -> {
      String name = member.getSimpleName().toString();
      // String type = Util.typeToString(member.asType());

      DataType type = toDataType(name, member.asType());

      dm.name(name)
          .dataType(type);

      if (member.getAnnotation(Required.class) != null) {
        dm.required();
      }
    });
  }

  private DataType toDataType(String name, TypeMirror type) {
    String fullyQualifiedType = TypeName.get(type).toString();
    return DataTypeParser.parse(fullyQualifiedType);


  }
}
