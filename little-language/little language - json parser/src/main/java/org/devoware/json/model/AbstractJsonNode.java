package org.devoware.json.model;

import static java.util.Objects.requireNonNull;

public abstract class AbstractJsonNode implements JsonNode {
  
  private final Type type;

  public AbstractJsonNode(Type type) {
    this.type = requireNonNull(type, "type cannot be null");
  }
  
  public final Type getType() {
    return type;
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((type == null) ? 0 : type.hashCode());
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
    AbstractJsonNode other = (AbstractJsonNode) obj;
    if (type != other.type)
      return false;
    return true;
  }
}
