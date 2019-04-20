package com.resolute.utils.simple.pojo_generator;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.function.Consumer;

import com.google.common.collect.ImmutableList;

public class Pojo {
  private final String className;
  private final String packageName;
  private final boolean jacksonAnnotations;
  private final List<PojoDataMember> dataMembers;

  public static Builder builder() {
    return new Builder();
  }

  public static Builder builder(Pojo pojo) {
    return new Builder(pojo);
  }

  private Pojo(Builder builder) {
    this.className = builder.className;
    this.packageName = builder.packageName;
    this.jacksonAnnotations = builder.jacksonAnnotations;
    this.dataMembers = builder.dataMembers;
  }

  public String getClassName() {
    return className;
  }

  public String getPackageName() {
    return packageName;
  }

  public boolean getJacksonAnnotations() {
    return jacksonAnnotations;
  }

  public List<PojoDataMember> getDataMembers() {
    return dataMembers;
  }

  @Override
  public String toString() {
    return "Pojo [className=" + className + ", packageName=" + packageName + ", jacksonAnnotations="
        + jacksonAnnotations + ", dataMembers=" + dataMembers + "]";
  }

  public static class Builder {
    private String className;
    private String packageName;
    private Boolean jacksonAnnotations;
    private List<PojoDataMember> dataMembers;

    private Builder() {}

    private Builder(Pojo pojo) {
      requireNonNull(pojo, "pojo cannot be null");
      this.className = pojo.className;
      this.packageName = pojo.packageName;
      this.jacksonAnnotations = pojo.jacksonAnnotations;
      this.dataMembers = pojo.dataMembers;
    }

    public Builder with(Consumer<Builder> consumer) {
      requireNonNull(consumer, "consumer cannot be null");
      consumer.accept(this);
      return this;
    }

    public Builder withClassName(String className) {
      requireNonNull(className, "className cannot be null");
      this.className = className;
      return this;
    }

    public Builder withPackageName(String packageName) {
      requireNonNull(packageName, "packageName cannot be null");
      this.packageName = packageName;
      return this;
    }

    public Builder withJacksonAnnotations(boolean jacksonAnnotations) {
      this.jacksonAnnotations = jacksonAnnotations;
      return this;
    }

    public Builder withDataMembers(List<PojoDataMember> dataMembers) {
      requireNonNull(dataMembers, "dataMembers cannot be null");
      this.dataMembers = ImmutableList.copyOf(dataMembers);
      return this;
    }

    public Pojo build() {
      requireNonNull(className, "className cannot be null");
      requireNonNull(packageName, "packageName cannot be null");
      requireNonNull(jacksonAnnotations, "jacksonAnnotations cannot be null");
      requireNonNull(dataMembers, "dataMembers cannot be null");
      return new Pojo(this);
    }
  }
}
