package org.devoware.json.model;



public interface Value<T> extends JsonNode {

  T value();

}
