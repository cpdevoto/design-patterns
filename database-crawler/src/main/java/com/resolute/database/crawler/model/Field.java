package com.resolute.database.crawler.model;

import java.util.Objects;
import java.util.Optional;

public class Field {
  private final String name;
  private final String dataType;
  private final Optional<String> defaultValue;

  public Field(String name, String dataType, String defaultValue) {
    this(name, dataType, Optional.ofNullable(defaultValue));
  }

  private Field(String name, String dataType, Optional<String> defaultValue) {
    this.name = name;
    this.dataType = dataType;
    this.defaultValue = defaultValue;
  }

  public String getName() {
    return name;
  }

  public String getDataType() {
    return dataType;
  }

  public Optional<String> getDefaultValue() {
    return defaultValue;
  }

  @Override
  public int hashCode() {
    return Objects.hash(dataType, defaultValue, name);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Field other = (Field) obj;
    return Objects.equals(dataType, other.dataType)
        && Objects.equals(defaultValue, other.defaultValue) && Objects.equals(name, other.name);
  }

  @Override
  public String toString() {
    return "Field [name=" + name + ", dataType=" + dataType + ", defaultValue=" + defaultValue
        + "]";
  }
}
