package com.resolute.jackson.fixtures;

import static java.util.Objects.requireNonNull;

import java.util.Objects;
import java.util.function.Consumer;

public class DesigoEnumeratedText {
  private final String descriptor;
  private final long value;

  public static Builder builder() {
    return new Builder();
  }

  public static Builder builder(DesigoEnumeratedText desigoEnumeratedText) {
    return new Builder(desigoEnumeratedText);
  }

  private DesigoEnumeratedText(Builder builder) {
    this.descriptor = builder.descriptor;
    this.value = builder.value;
  }

  public String getDescriptor() {
    return descriptor;
  }

  public long getValue() {
    return value;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + Objects.hash(descriptor, value);
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    DesigoEnumeratedText other = (DesigoEnumeratedText) obj;
    return Objects.equals(descriptor, other.descriptor) && Objects.equals(value, other.value);
  }

  @Override
  public String toString() {
    return "DesigoEnumeratedText [descriptor=" + descriptor + ", value=" + value + "]";
  }

  public static class Builder {
    private String descriptor;
    private Long value;

    private Builder() {}

    private Builder(DesigoEnumeratedText desigoEnumeratedText) {
      requireNonNull(desigoEnumeratedText, "desigoEnumeratedText cannot be null");
      this.descriptor = desigoEnumeratedText.descriptor;
      this.value = desigoEnumeratedText.value;
    }

    public Builder with(Consumer<Builder> consumer) {
      requireNonNull(consumer, "consumer cannot be null");
      consumer.accept(this);
      return this;
    }

    public Builder withDescriptor(String descriptor) {
      requireNonNull(descriptor, "descriptor cannot be null");
      this.descriptor = descriptor;
      return this;
    }

    public Builder withValue(long value) {
      this.value = value;
      return this;
    }

    public DesigoEnumeratedText build() {
      requireNonNull(descriptor, "descriptor cannot be null");
      requireNonNull(value, "value cannot be null");
      return new DesigoEnumeratedText(this);
    }

    public boolean isValid() {
      return descriptor != null && value != null;
    }

    public void clear() {
      this.descriptor = null;
      this.value = null;
    }

  }
}
