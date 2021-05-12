package com.resolute.database.crawler.model;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toCollection;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class Graph {

  private final Map<String, Node> nodes = Maps.newTreeMap();

  public Graph() {}

  public void addNode(Node node) {
    requireNonNull(node, "node cannot be null");
    nodes.computeIfAbsent(node.getName(),
        n -> new Node(n, node.getPrimaryKeyFields(), node.getUniqueIndecesWithNames(),
            node.getFields()));
  }

  public void addNode(String name, List<Field> primaryKey, List<UniqueIndex> uniqueIndeces) {
    requireNonNull(name, "name cannot be null");
    requireNonNull(primaryKey, "primaryKey cannot be null");
    requireNonNull(uniqueIndeces, "uniqueIndeces cannot be null");
    nodes.computeIfAbsent(name, n -> new Node(n, primaryKey, uniqueIndeces));
  }

  public void addEdge(String from, String to, ForeignKey foreignKey) {
    requireNonNull(from, "from cannot be null");
    requireNonNull(to, "to cannot be null");
    requireNonNull(foreignKey, "foreignKey cannot be null");
    Node fromNode = Optional.ofNullable(nodes.get(from))
        .orElseThrow(() -> new IllegalArgumentException("Invalid from node: " + from));
    Node toNode = Optional.ofNullable(nodes.get(to))
        .orElseThrow(() -> new IllegalArgumentException("Invalid to node: " + to));
    new Edge(fromNode, toNode, foreignKey);

  }



  public Optional<Node> getNode(String name) {
    requireNonNull(name, "name cannot be null");
    return Optional.ofNullable(nodes.get(name));
  }

  public Set<Node> getNodes() {
    return nodes.values().stream()
        .collect(toCollection(LinkedHashSet::new));
  }

  public Set<Node> getSuperclassRoots() {
    return nodes.values().stream()
        .filter(node -> node.isSuperclass() && !node.isSubclass())
        .collect(toCollection(LinkedHashSet::new));
  }

  public Graph union(Graph graph) {
    Graph result = new Graph();
    List<Node> sorted = topologicalSort();
    for (Node node : sorted) {
      result.addNode(node);
      for (Edge edge : node.getToEdges()) {
        result.addEdge(edge.getFrom().getName(), edge.getTo().getName(), edge.getForeignKey());
      }
    }
    sorted = graph.topologicalSort();
    for (Node node : sorted) {
      result.addNode(node);
      for (Edge edge : node.getToEdges()) {
        result.addEdge(edge.getFrom().getName(), edge.getTo().getName(), edge.getForeignKey());
      }
    }
    return result;
  }


  public Graph difference(Graph graph) {
    return difference(graph, true);
  }

  // The difference between two connected graphs G1 and G2 rooted at vertex R1 and vertex R2
  // respectively is computed as the set of all vertices in G1 which do not appear in G2, removing
  // any edges in G1 incident to the vertices that appear in G2.
  public Graph difference(Graph graph, boolean displayWarnings) {
    Graph result = new Graph();
    List<Node> sorted = topologicalSort();
    for (Node node : sorted) {
      if (graph.nodes.containsKey(node.getName())) {
        List<Edge> nonDupEdges = getNonDuplicateToEdges(node, graph.getNode(node.getName()).get());
        if (!nonDupEdges.isEmpty() && displayWarnings) {
          System.err.println("\nWARNING: Table " + node.getName()
              + " is reachable from the source graph using edges that are not present in the graph to be subtracted.\n"
              + "If you believe that this might lead to orphaned records, you might wish to add some\n"
              + "appendedFunctionCode to delete from this table and its subtree using the edges from the source graph.\n"
              + "Here is a list of the edges that can be used to reach the table from the source graph but not the subtracted graph:\n");
          nonDupEdges.stream()
              .map(e -> "  * " + e)
              .forEach(System.err::println);
        }
        continue;
      }
      result.addNode(node);
      for (Edge edge : node.getToEdges()) {
        result.addEdge(edge.getFrom().getName(), edge.getTo().getName(), edge.getForeignKey());
      }
    }
    return result;
  }

  // The difference between two connected graphs G1 and G2 rooted at vertex R1 and vertex R2
  // respectively is computed as the set of all vertices in G1 which are reachable from R1 through a
  // sequence of edges that do not pass through R2.
  public Graph difference2(Graph graph) {
    Objects.requireNonNull(graph, "graph cannot be null");
    Graph result = new Graph();
    List<Node> nodes1 = topologicalSort();
    List<Node> nodes2 = graph.topologicalSort();
    if (nodes1.isEmpty() || nodes2.isEmpty()) {
      return this;
    }
    Node root1 = nodes1.get(0);
    Node root2 = nodes2.get(0);

    Map<Node, List<List<Edge>>> nodePaths = Maps.newLinkedHashMap();
    for (Node node : nodes1) {
      Deque<Edge> stack = new ArrayDeque<Edge>();
      Deque<Node> visited = new ArrayDeque<Node>();
      List<List<Edge>> paths = Lists.newArrayList();
      getPaths(stack, visited, paths, root1, root2, node);
      if (!paths.isEmpty() || node.getName().equals(root1.getName())) {
        nodePaths.put(node, paths);
      }
    }

    nodePaths.keySet().stream()
        .forEach(node -> result.addNode(node));

    nodePaths.values().stream()
        .flatMap(paths -> paths.stream())
        .flatMap(path -> path.stream())
        .collect(Collectors.toSet())
        .forEach(edge -> result.addEdge(edge.getFrom().getName(), edge.getTo().getName(),
            edge.getForeignKey()));

    nodePaths.keySet().stream()
        .flatMap(node -> node.getFromEdges().stream())
        .filter(Edge::isUnaryAssociation)
        .forEach(edge -> result.addEdge(edge.getFrom().getName(), edge.getTo().getName(),
            edge.getForeignKey()));

    return result;
  }

  private void getPaths(Deque<Edge> stack, Deque<Node> visited, List<List<Edge>> paths,
      Node root1, Node root2,
      Node node) {
    if (visited.contains(node) || node.getName().equals(root2.getName())) {
      return;
    }
    visited.push(node);
    for (Edge edge : node.getToEdges()) {
      stack.push(edge);
      String from = edge.getFrom().getName();
      if (from.equals(root2.getName())) {
        // The path goes through root2, so we skip it!
      } else if (from.equals(root1.getName())) {
        List<Edge> path = Lists.newArrayList(stack);
        // Collections.reverse(path);
        paths.add(path);
      } else {
        getPaths(stack, visited, paths, root1, root2, edge.getFrom());
      }
      stack.pop();
    }
    visited.pop();
  }

  private List<Edge> getNonDuplicateToEdges(Node node1, Node node2) {
    List<Edge> nonDupEdges = Lists.newArrayList();
    if (node2.getToEdges().isEmpty()) {
      return nonDupEdges;
    }
    for (Edge edge1 : node1.getToEdges()) {
      boolean duplicate = false;
      for (Edge edge2 : node2.getToEdges()) {
        if (edge1.getFrom().equals(edge2.getFrom())
            && edge1.getForeignKey().equals(edge2.getForeignKey())) {
          duplicate = true;
          break;
        }
      }
      if (!duplicate) {
        nonDupEdges.add(edge1);
      }
    }
    return nonDupEdges;
  }

  public Set<Node> getNodesReachableFrom(String name) {
    requireNonNull(name, "name cannot be null");
    Set<Node> result = Sets.newTreeSet();
    Set<Node> visited = Sets.newHashSet();
    Node root = nodes.get(name);
    if (root == null) {
      throw new IllegalArgumentException("Invalid node: " + name);
    }
    getNodesReachableFrom(root, result, visited);
    return result;
  }

  public Graph getSubgraphReachableFrom(String name) {
    return getSubgraphReachableFrom(name, ImmutableSet.of());
  }

  public Graph getSubgraphReachableFrom(String name, Set<IgnoredEdge> ignoredEdges) {
    requireNonNull(name, "name cannot be null");
    requireNonNull(ignoredEdges, "ignoredEdges cannot be null");
    Graph subgraph = new Graph();
    Set<Node> visited = Sets.newHashSet();
    Node root = nodes.get(name);
    if (root == null) {
      throw new IllegalArgumentException("WARNING: Node " + name
          + " does not appear in the current graph, so the subgraph reachable from it would be be empty");
    }
    getSubgraphReachableFrom(root, subgraph, visited, ImmutableSet.copyOf(ignoredEdges));
    return subgraph;
  }

  public List<Node> topologicalSort() {
    Set<Node> visited = Sets.newLinkedHashSet();
    List<Node> stack = Lists.newArrayList();
    for (Node node : nodes.values()) {
      topologicalSort(visited, stack, node);
    }
    Collections.reverse(stack);
    return stack;
  }

  private void topologicalSort(Set<Node> visited, List<Node> stack, Node node) {
    if (visited.contains(node)) {
      return;
    }
    visited.add(node);
    node.getFromEdges().stream()
        .forEach(e -> topologicalSort(visited, stack, e.getTo()));
    stack.add(node);
  }

  @Override
  public String toString() {
    return getNodes().toString();
  }

  private void getNodesReachableFrom(Node node, Set<Node> result, Set<Node> visited) {
    if (visited.contains(node)) {
      return;
    }
    result.add(node);
    visited.add(node);
    for (Edge edge : node.getFromEdges()) {
      getNodesReachableFrom(edge.getTo(), result, visited);
    }
  }

  private void getSubgraphReachableFrom(Node node, Graph subgraph, Set<Node> visited,
      Set<IgnoredEdge> ignoredEdges) {
    if (visited.contains(node)) {
      return;
    }
    subgraph.addNode(node);
    visited.add(node);
    for (Edge edge : node.getFromEdges()) {
      if (ignoredEdges.contains(IgnoredEdge.create(edge))) {
        continue;
      }
      subgraph.nodes.computeIfAbsent(edge.getTo().getName(),
          name -> new Node(edge.getTo()));
      subgraph.addEdge(edge.getFrom().getName(), edge.getTo().getName(), edge.getForeignKey());
      getSubgraphReachableFrom(edge.getTo(), subgraph, visited, ignoredEdges);
    }
  }
}
