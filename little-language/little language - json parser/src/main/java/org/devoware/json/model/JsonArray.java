package org.devoware.json.model;

import static java.util.Objects.requireNonNull;

import java.util.Iterator;
import java.util.List;

import com.google.common.collect.ImmutableList;

public class JsonArray extends AbstractJsonNode implements Iterable<JsonNode> {
  private final List<JsonNode> elements;

  public JsonArray(List<JsonNode> elements) {
    super(Type.ARRAY);
    requireNonNull(elements, "elements cannot be null");
    this.elements = ImmutableList.copyOf(elements);
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder("[");
    boolean firstLoop = true;
    for (JsonNode element : elements) {
      if (firstLoop) {
        firstLoop = false;
      } else {
        buf.append(",");
      }
      buf.append(element.toString());
    }
    buf.append("]");
    return buf.toString();
  }

  @Override
  public Iterator<JsonNode> iterator() {
    return elements.iterator();
  }
  
  public int size () {
    return elements.size();
  }
 
  public boolean isEmpty() {
    return elements.isEmpty();
  }
  
  @SuppressWarnings({"unchecked", "rawtypes"})
  public <T> T get (int idx) {
    Object node = elements.get(idx);
    if (node instanceof Value) {
      return (T) ((Value) node).value();
    }
    return (T) node;
  }

}
