package com.resolute.jackson.fixtures;

import static java.util.Objects.requireNonNull;

import java.util.Objects;
import java.util.function.Consumer;

public class DesigoNode {
  private final String designation;
  private final long managedType;
  private final String managedTypeName;
  private final String subtypeDescriptor;

  public static Builder builder() {
    return new Builder();
  }

  public static Builder builder(DesigoNode systemBrowserEndpointResponse) {
    return new Builder(systemBrowserEndpointResponse);
  }

  private DesigoNode(Builder builder) {
    this.designation = builder.designation;
    this.managedType = builder.managedType;
    this.managedTypeName = builder.managedTypeName;
    this.subtypeDescriptor = builder.subtypeDescriptor;
  }

  public String getDesignation() {
    return designation;
  }

  public long getManagedType() {
    return managedType;
  }

  public String getManagedTypeName() {
    return managedTypeName;
  }

  public String getSubtypeDescriptor() {
    return subtypeDescriptor;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result =
        prime * result + Objects.hash(designation, managedType, managedTypeName, subtypeDescriptor);
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
    DesigoNode other = (DesigoNode) obj;
    return Objects.equals(designation, other.designation)
        && Objects.equals(managedType, other.managedType)
        && Objects.equals(managedTypeName, other.managedTypeName)
        && Objects.equals(subtypeDescriptor, other.subtypeDescriptor);
  }

  @Override
  public String toString() {
    return "DesigoNode [designation=" + designation + ", managedType="
        + managedType + ", managedTypeName=" + managedTypeName + ", subtypeDescriptor="
        + subtypeDescriptor + "]";
  }

  public static class Builder {
    private String designation;
    private Long managedType;
    private String managedTypeName;
    private String subtypeDescriptor;

    private Builder() {}

    private Builder(DesigoNode node) {
      requireNonNull(node, "node cannot be null");
      this.designation = node.designation;
      this.managedType = node.managedType;
      this.managedTypeName = node.managedTypeName;
      this.subtypeDescriptor = node.subtypeDescriptor;
    }

    public Builder with(Consumer<Builder> consumer) {
      requireNonNull(consumer, "consumer cannot be null");
      consumer.accept(this);
      return this;
    }

    public Builder withDesignation(String designation) {
      requireNonNull(designation, "designation cannot be null");
      this.designation = designation;
      return this;
    }

    public Builder withManagedType(long managedType) {
      this.managedType = managedType;
      return this;
    }

    public Builder withManagedTypeName(String managedTypeName) {
      requireNonNull(managedTypeName, "managedTypeName cannot be null");
      this.managedTypeName = managedTypeName;
      return this;
    }

    public Builder withSubtypeDescriptor(String subtypeDescriptor) {
      requireNonNull(subtypeDescriptor, "subtypeDescriptor cannot be null");
      this.subtypeDescriptor = subtypeDescriptor;
      return this;
    }

    public boolean isValid() {
      return designation != null && managedType != null && managedTypeName != null
          && subtypeDescriptor != null;
    }

    public DesigoNode build() {
      requireNonNull(designation, "designation cannot be null");
      requireNonNull(managedType, "managedType cannot be null");
      requireNonNull(managedTypeName, "managedTypeName cannot be null");
      return new DesigoNode(this);
    }

    public void clear() {
      this.designation = null;
      this.managedType = null;
      this.managedTypeName = null;
      this.subtypeDescriptor = null;
    }

    @Override
    public String toString() {
      return "DesigoNode.Builder [designation=" + designation + ", managedType="
          + managedType + ", managedTypeName=" + managedTypeName + ", subtypeDescriptor="
          + subtypeDescriptor + "]";
    }

  }
}
