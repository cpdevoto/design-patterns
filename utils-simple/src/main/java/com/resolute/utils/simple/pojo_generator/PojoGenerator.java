package com.resolute.utils.simple.pojo_generator;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class PojoGenerator {
  private final Pojo.Builder pojoBuilder = Pojo.builder();

  public static PojoGenerator forClass(String className) {
    return new PojoGenerator(className);
  }

  public static PojoDataMemberBuilder dataMember(Consumer<PojoDataMemberBuilder> consumer) {
    requireNonNull(consumer, "consumer cannot be null");
    PojoDataMemberBuilder dataMemberBuilder = new PojoDataMemberBuilder();
    consumer.accept(dataMemberBuilder);
    return dataMemberBuilder;
  }

  private PojoGenerator(String className) {
    pojoBuilder.withClassName(className);
    pojoBuilder.withJacksonAnnotations(false);
  }

  public PojoGenerator inPackage(String packageName) {
    pojoBuilder.withPackageName(packageName);
    return this;
  }

  public PojoGenerator jacksonAnnotations() {
    pojoBuilder.withJacksonAnnotations(true);
    return this;
  }

  public PojoGenerator dataMembers(PojoDataMemberBuilder... dataMemberBuilders) {
    List<PojoDataMember> dataMembers = Stream.of(dataMemberBuilders)
        .map(PojoDataMemberBuilder::build)
        .collect(toList());
    pojoBuilder.withDataMembers(dataMembers);
    return this;
  }

  public String generate() {
    SourceGenerator generator = new SourceGenerator(pojoBuilder.build());
    return generator.generate();
  }
}
