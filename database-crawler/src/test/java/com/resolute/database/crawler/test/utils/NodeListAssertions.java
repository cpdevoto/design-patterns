package com.resolute.database.crawler.test.utils;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toMap;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.IntStream;

import com.resolute.database.crawler.model.Node;

public class NodeListAssertions {

  private final Map<String, Integer> nodeIndeces;

  NodeListAssertions(List<Node> nodes) {
    requireNonNull(nodes, "nodes cannot be null");
    this.nodeIndeces = IntStream.range(0, nodes.size())
        .mapToObj(i -> new MyEntry<String, Integer>(nodes.get(i).getName(), i))
        .collect(toMap(Entry::getKey, Entry::getValue));
  }

  public NodeListOrderAssertion assertThat(String nodeName) {
    requireNonNull(nodeName, "nodeName cannot be null");
    return new NodeListOrderAssertion(this, nodeIndeces, nodeName);
  }

  private static class MyEntry<K, V> implements Map.Entry<K, V> {
    private final K key;
    private V value;

    public MyEntry(K key, V value) {
      this.key = key;
      this.value = value;
    }

    @Override
    public K getKey() {
      return key;
    }

    @Override
    public V getValue() {
      return value;
    }

    @Override
    public V setValue(V value) {
      V old = this.value;
      this.value = value;
      return old;
    }
  }
}
