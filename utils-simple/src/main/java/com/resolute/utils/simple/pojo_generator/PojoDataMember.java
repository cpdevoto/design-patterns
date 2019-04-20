package com.resolute.utils.simple.pojo_generator;

import static java.util.Objects.requireNonNull;

import java.util.function.Consumer;

public class PojoDataMember {
  private final String name;
  private final String dataType;
  private final boolean required;

  public static Builder builder() {
    return new Builder();
  }

  public static Builder builder(PojoDataMember pojoDataMember) {
    return new Builder(pojoDataMember);
  }

  private PojoDataMember(Builder builder) {
    this.name = builder.name;
    this.dataType = builder.dataType;
    this.required = builder.required;
  }

  public String getName() {
    return name;
  }

  public String getDataType() {
    return dataType;
  }

  public boolean getRequired() {
    return required;
  }

  @Override
  public String toString() {
    return "PojoDataMember [name=" + name + ", dataType=" + dataType + ", required=" + required
        + "]";
  }

  public static class Builder {
    private String name;
    private String dataType;
    private Boolean required;

    private Builder() {}

    private Builder(PojoDataMember pojoDataMember) {
      requireNonNull(pojoDataMember, "pojoDataMember cannot be null");
      this.name = pojoDataMember.name;
      this.dataType = pojoDataMember.dataType;
      this.required = pojoDataMember.required;
    }

    public Builder with(Consumer<Builder> consumer) {
      requireNonNull(consumer, "consumer cannot be null");
      consumer.accept(this);
      return this;
    }

    public Builder withName(String name) {
      requireNonNull(name, "name cannot be null");
      this.name = name;
      return this;
    }

    public Builder withDataType(String dataType) {
      requireNonNull(dataType, "dataType cannot be null");
      this.dataType = dataType;
      return this;
    }

    public Builder withRequired(boolean required) {
      this.required = required;
      return this;
    }

    public PojoDataMember build() {
      requireNonNull(name, "name cannot be null");
      requireNonNull(dataType, "dataType cannot be null");
      requireNonNull(required, "required cannot be null");
      return new PojoDataMember(this);
    }
  }
}
