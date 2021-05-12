package com.resolute.database.crawler.test.utils;

import static java.util.Objects.requireNonNull;
import static org.testcontainers.shaded.com.google.common.base.Preconditions.checkArgument;

import java.util.Map;

public class NodeListOrderAssertion {

  private final NodeListAssertions parent;
  private final Map<String, Integer> nodeIndeces;
  private final String node1Name;

  NodeListOrderAssertion(NodeListAssertions parent, Map<String, Integer> nodeIndeces, String node1Name) {
    this.parent = requireNonNull(parent, "parent cannot be null");
    this.nodeIndeces = requireNonNull(nodeIndeces, "nodeIndeces cannot be null");
    this.node1Name = requireNonNull(node1Name, "node1Name cannot be null");
    checkArgument(nodeIndeces.containsKey(node1Name), String
        .format("A node named '%s' does not appear in the specified list of node", node1Name));
  }

  public NodeListAssertions isBefore(String node2Name) {
    requireNonNull(node2Name, "node2Name cannot be null");
    checkArgument(nodeIndeces.containsKey(node2Name), String
        .format("A node named '%s' does not appear in the specified list of node", node2Name));
    int node1Idx = nodeIndeces.get(node1Name);
    int node2Idx = nodeIndeces.get(node2Name);
    if ((node1Idx < node2Idx) == false) {
      throw new AssertionError(
          String.format("Expected '%s' to appear before '%s' in the specified list of nodes",
              node1Name, node2Name));
    }
    return parent;
  }

}
