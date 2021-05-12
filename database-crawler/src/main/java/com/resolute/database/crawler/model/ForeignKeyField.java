package com.resolute.database.crawler.model;

import static java.util.Objects.requireNonNull;

public class ForeignKeyField {

  private final String fromField;
  private final String toField;

  public ForeignKeyField(String fromField, String toField) {
    this.fromField = requireNonNull(fromField, "fromField cannot be null");
    this.toField = requireNonNull(toField, "toField cannot be null");
  }

  public String getFromField() {
    return fromField;
  }

  public String getToField() {
    return toField;
  }

  @Override
  public String toString() {
    return fromField + " => " + toField;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((fromField == null) ? 0 : fromField.hashCode());
    result = prime * result + ((toField == null) ? 0 : toField.hashCode());
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
    ForeignKeyField other = (ForeignKeyField) obj;
    if (fromField == null) {
      if (other.fromField != null)
        return false;
    } else if (!fromField.equals(other.fromField))
      return false;
    if (toField == null) {
      if (other.toField != null)
        return false;
    } else if (!toField.equals(other.toField))
      return false;
    return true;
  }

}
