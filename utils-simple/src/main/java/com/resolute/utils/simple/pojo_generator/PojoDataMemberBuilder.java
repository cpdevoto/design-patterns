package com.resolute.utils.simple.pojo_generator;

public class PojoDataMemberBuilder {
  private final PojoDataMember.Builder dataMemberBuilder = PojoDataMember.builder();

  PojoDataMemberBuilder() {
    dataMemberBuilder.withRequired(false);
  }

  public PojoDataMemberBuilder name(String name) {
    dataMemberBuilder.withName(name);
    return this;
  }

  public PojoDataMemberBuilder dataType(String dataType) {
    dataMemberBuilder.withDataType(dataType);
    return this;
  }

  public PojoDataMemberBuilder required() {
    dataMemberBuilder.withRequired(true);
    return this;
  }

  PojoDataMember build() {
    return dataMemberBuilder.build();
  }
}
