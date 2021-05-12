package com.resolute.database.crawler;

import static com.resolute.database.crawler.test.utils.CrawlerAssertions.with;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.resolute.database.crawler.integration.AbstractPostgresDaoTest;
import com.resolute.database.crawler.integration.IntegrationTestSuite;
import com.resolute.database.crawler.model.Edge;
import com.resolute.database.crawler.model.Field;
import com.resolute.database.crawler.model.Graph;
import com.resolute.database.crawler.model.Node;
import com.resolute.jdbc.simple.DaoUtils;
import com.resolute.utils.simple.ElapsedTimeUtils;

public abstract class AbstractDatabaseCrawlerTest extends AbstractPostgresDaoTest {

  protected static Graph schemaGraph;

  @BeforeAll
  public static void beforeAll() throws Exception {
    DatabaseCrawler dao = DatabaseCrawler.create(dataSource);
    long start = System.currentTimeMillis();
    System.out.println("Starting database graph generation...");
    schemaGraph = dao.getSchemaGraph();
    long elapsed = System.currentTimeMillis() - start;
    System.out
        .println(
            "Database graph generation completed in " + ElapsedTimeUtils.format(elapsed)
                + "\n");
  }

  @BeforeEach
  public void setUpBefore() throws Exception {
    DaoUtils.executeSqlScript(dataSource, IntegrationTestSuite.class,
        "base-test-data.sql");
    DaoUtils.executeSqlScript(dataSource, IntegrationTestSuite.class,
        "base-resolute-data.sql");
  }

  @AfterEach
  public void tearDownAfter() throws Exception {
    DaoUtils.executeSqlScript(dataSource, IntegrationTestSuite.class,
        "base-resolute-teardown.sql");
    DaoUtils.executeSqlScript(dataSource, IntegrationTestSuite.class,
        "base-test-teardown.sql");
  }

  @Test
  public void test_get_subgraph_reachable_from() {
    Graph subgraph = schemaGraph.getSubgraphReachableFrom("test1_tbl");

    Map<String, Node> nodes = subgraph.getNodes().stream()
        .collect(Collectors.toMap(Node::getName, identity()));
    assertThat(nodes.size()).isEqualTo(4);

    Node node;
    Map<String, Edge> edges;
    Edge edge;

    // Validate the node corresponding to test1_tbl

    node = nodes.get("test1_tbl");
    assertThat(node).isNotNull();
    assertThat(node.getName()).isEqualTo("test1_tbl");
    assertThat(node.getPrimaryKey().size()).isEqualTo(1);
    assertThat(node.getPrimaryKey().get(0)).isEqualTo("id");

    edges = node.getFromEdges().stream()
        .collect(Collectors.toMap(e -> e.getTo().getName(), identity()));
    assertThat(edges.size()).isEqualTo(2);

    edge = edges.get("test3_tbl");
    assertThat(edge).isNotNull();
    assertThat(edge.getFrom().getName()).isEqualTo("test1_tbl");
    assertThat(edge.getTo().getName()).isEqualTo("test3_tbl");
    assertThat(edge.getForeignKey().getFields().size()).isEqualTo(1);
    assertThat(edge.getForeignKey().getFields().get(0).getFromField()).isEqualTo("id");
    assertThat(edge.getForeignKey().getFields().get(0).getToField()).isEqualTo("test1_id");

    edge = edges.get("test4_tbl");
    assertThat(edge).isNotNull();
    assertThat(edge.getFrom().getName()).isEqualTo("test1_tbl");
    assertThat(edge.getTo().getName()).isEqualTo("test4_tbl");
    assertThat(edge.getForeignKey().getFields().size()).isEqualTo(1);
    assertThat(edge.getForeignKey().getFields().get(0).getFromField()).isEqualTo("id");
    assertThat(edge.getForeignKey().getFields().get(0).getToField()).isEqualTo("test1_id");

    edges = node.getToEdges().stream()
        .collect(Collectors.toMap(e -> e.getFrom().getName(), identity()));
    assertThat(edges.size()).isEqualTo(0);

    // Validate the node corresponding to test2_tbl

    node = nodes.get("test2_tbl");
    assertThat(node).isNull();

    // Validate the node corresponding to test3_tbl

    node = nodes.get("test3_tbl");
    assertThat(node).isNotNull();
    assertThat(node.getName()).isEqualTo("test3_tbl");
    assertThat(node.getPrimaryKey().size()).isEqualTo(2);
    assertThat(node.getPrimaryKey().get(0)).isEqualTo("test1_id");
    assertThat(node.getPrimaryKey().get(1)).isEqualTo("test2_id");

    edges = node.getFromEdges().stream()
        .collect(Collectors.toMap(e -> e.getTo().getName(), identity()));
    assertThat(edges.size()).isEqualTo(0);

    edges = node.getToEdges().stream()
        .collect(Collectors.toMap(e -> e.getFrom().getName(), identity()));
    assertThat(edges.size()).isEqualTo(1);

    edge = edges.get("test1_tbl");
    assertThat(edge).isNotNull();
    assertThat(edge.getFrom().getName()).isEqualTo("test1_tbl");
    assertThat(edge.getTo().getName()).isEqualTo("test3_tbl");
    assertThat(edge.getForeignKey().getFields().size()).isEqualTo(1);
    assertThat(edge.getForeignKey().getFields().get(0).getFromField()).isEqualTo("id");
    assertThat(edge.getForeignKey().getFields().get(0).getToField()).isEqualTo("test1_id");

    // Validate the node corresponding to test4_tbl

    node = nodes.get("test4_tbl");
    assertThat(node).isNotNull();
    assertThat(node.getName()).isEqualTo("test4_tbl");
    assertThat(node.getPrimaryKey().size()).isEqualTo(1);
    assertThat(node.getPrimaryKey().get(0)).isEqualTo("id");

    edges = node.getFromEdges().stream()
        .collect(Collectors.toMap(e -> e.getTo().getName(), identity()));
    assertThat(edges.size()).isEqualTo(2);

    edge = edges.get("test4_tbl");
    assertThat(edge).isNotNull();
    assertThat(edge.getFrom().getName()).isEqualTo("test4_tbl");
    assertThat(edge.getTo().getName()).isEqualTo("test4_tbl");
    assertThat(edge.getForeignKey().getFields().size()).isEqualTo(1);
    assertThat(edge.getForeignKey().getFields().get(0).getFromField()).isEqualTo("id");
    assertThat(edge.getForeignKey().getFields().get(0).getToField()).isEqualTo("parent_id");

    edge = edges.get("test10_tbl");
    assertThat(edge).isNotNull();
    assertThat(edge.getFrom().getName()).isEqualTo("test4_tbl");
    assertThat(edge.getTo().getName()).isEqualTo("test10_tbl");
    assertThat(edge.getForeignKey().getFields().size()).isEqualTo(1);
    assertThat(edge.getForeignKey().getFields().get(0).getFromField()).isEqualTo("id");
    assertThat(edge.getForeignKey().getFields().get(0).getToField()).isEqualTo("test4_id");

    edges = node.getToEdges().stream()
        .collect(Collectors.toMap(e -> e.getFrom().getName(), identity()));
    assertThat(edges.size()).isEqualTo(2);

    edge = edges.get("test1_tbl");
    assertThat(edge).isNotNull();
    assertThat(edge.getFrom().getName()).isEqualTo("test1_tbl");
    assertThat(edge.getTo().getName()).isEqualTo("test4_tbl");
    assertThat(edge.getForeignKey().getFields().size()).isEqualTo(1);
    assertThat(edge.getForeignKey().getFields().get(0).getFromField()).isEqualTo("id");
    assertThat(edge.getForeignKey().getFields().get(0).getToField()).isEqualTo("test1_id");

    edge = edges.get("test4_tbl");
    assertThat(edge).isNotNull();
    assertThat(edge.getFrom().getName()).isEqualTo("test4_tbl");
    assertThat(edge.getTo().getName()).isEqualTo("test4_tbl");
    assertThat(edge.getForeignKey().getFields().size()).isEqualTo(1);
    assertThat(edge.getForeignKey().getFields().get(0).getFromField()).isEqualTo("id");
    assertThat(edge.getForeignKey().getFields().get(0).getToField()).isEqualTo("parent_id");

    // Validate the node corresponding to test10_tbl

    node = nodes.get("test10_tbl");
    assertThat(node).isNotNull();
    assertThat(node.getName()).isEqualTo("test10_tbl");
    assertThat(node.getPrimaryKey().size()).isEqualTo(1);
    assertThat(node.getPrimaryKey().get(0)).isEqualTo("id");

    edges = node.getFromEdges().stream()
        .collect(Collectors.toMap(e -> e.getTo().getName(), identity()));
    assertThat(edges.size()).isEqualTo(0);

    edges = node.getToEdges().stream()
        .collect(Collectors.toMap(e -> e.getFrom().getName(), identity()));
    assertThat(edges.size()).isEqualTo(1);

    edge = edges.get("test4_tbl");
    assertThat(edge).isNotNull();
    assertThat(edge.getFrom().getName()).isEqualTo("test4_tbl");
    assertThat(edge.getTo().getName()).isEqualTo("test10_tbl");
    assertThat(edge.getForeignKey().getFields().size()).isEqualTo(1);
    assertThat(edge.getForeignKey().getFields().get(0).getFromField()).isEqualTo("id");
    assertThat(edge.getForeignKey().getFields().get(0).getToField()).isEqualTo("test4_id");
  }

  @Test
  public void test_union_with_test_tables() {
    Graph subgraph1 = schemaGraph.getSubgraphReachableFrom("test1_tbl");
    Graph subgraph2 = schemaGraph.getSubgraphReachableFrom("test2_tbl");
    Graph subgraph3 = subgraph1.union(subgraph2);

    Map<String, Node> nodes = subgraph3.getNodes().stream()
        .collect(toMap(Node::getName, identity()));
    assertThat(nodes.size()).isGreaterThanOrEqualTo(5);

    Node node;
    Map<String, Edge> edges;
    Edge edge;

    // Validate the node corresponding to test1_tbl

    node = nodes.get("test1_tbl");
    assertThat(node).isNotNull();
    assertThat(node.getName()).isEqualTo("test1_tbl");
    assertThat(node.getPrimaryKey().size()).isEqualTo(1);
    assertThat(node.getPrimaryKey().get(0)).isEqualTo("id");

    edges = node.getToEdges().stream()
        .collect(Collectors.toMap(e -> e.getFrom().getName(), identity()));
    assertThat(edges.size()).isEqualTo(0);

    edges = node.getFromEdges().stream()
        .collect(toMap(e -> e.getTo().getName(), identity()));
    assertThat(edges.size()).isEqualTo(2);

    edge = edges.get("test3_tbl");
    assertThat(edge).isNotNull();
    assertThat(edge.getFrom().getName()).isEqualTo("test1_tbl");
    assertThat(edge.getTo().getName()).isEqualTo("test3_tbl");
    assertThat(edge.getForeignKey().getFields().size()).isEqualTo(1);
    assertThat(edge.getForeignKey().getFields().get(0).getFromField()).isEqualTo("id");
    assertThat(edge.getForeignKey().getFields().get(0).getToField()).isEqualTo("test1_id");

    edge = edges.get("test4_tbl");
    assertThat(edge).isNotNull();
    assertThat(edge.getFrom().getName()).isEqualTo("test1_tbl");
    assertThat(edge.getTo().getName()).isEqualTo("test4_tbl");
    assertThat(edge.getForeignKey().getFields().size()).isEqualTo(1);
    assertThat(edge.getForeignKey().getFields().get(0).getFromField()).isEqualTo("id");
    assertThat(edge.getForeignKey().getFields().get(0).getToField()).isEqualTo("test1_id");

    // Validate the node corresponding to test2_tbl

    node = nodes.get("test2_tbl");
    assertThat(node).isNotNull();
    assertThat(node.getName()).isEqualTo("test2_tbl");
    assertThat(node.getPrimaryKey().size()).isEqualTo(1);
    assertThat(node.getPrimaryKey().get(0)).isEqualTo("id");

    edges = node.getToEdges().stream()
        .collect(Collectors.toMap(e -> e.getFrom().getName(), identity()));
    assertThat(edges.size()).isEqualTo(0);


    edges = node.getFromEdges().stream()
        .collect(toMap(e -> e.getTo().getName(), identity()));
    assertThat(edges.size()).isEqualTo(1);

    edge = edges.get("test3_tbl");
    assertThat(edge).isNotNull();
    assertThat(edge.getFrom().getName()).isEqualTo("test2_tbl");
    assertThat(edge.getTo().getName()).isEqualTo("test3_tbl");
    assertThat(edge.getForeignKey().getFields().size()).isEqualTo(1);
    assertThat(edge.getForeignKey().getFields().get(0).getFromField()).isEqualTo("id");
    assertThat(edge.getForeignKey().getFields().get(0).getToField()).isEqualTo("test2_id");

    // Validate the node corresponding to test3_tbl

    node = nodes.get("test3_tbl");
    assertThat(node).isNotNull();
    assertThat(node.getName()).isEqualTo("test3_tbl");
    assertThat(node.getPrimaryKey().size()).isEqualTo(2);
    assertThat(node.getPrimaryKey().get(0)).isEqualTo("test1_id");
    assertThat(node.getPrimaryKey().get(1)).isEqualTo("test2_id");

    edges = node.getToEdges().stream()
        .collect(Collectors.toMap(e -> e.getFrom().getName(), identity()));
    assertThat(edges.size()).isEqualTo(2);

    edge = edges.get("test1_tbl");
    assertThat(edge).isNotNull();
    assertThat(edge.getFrom().getName()).isEqualTo("test1_tbl");
    assertThat(edge.getTo().getName()).isEqualTo("test3_tbl");
    assertThat(edge.getForeignKey().getFields().size()).isEqualTo(1);
    assertThat(edge.getForeignKey().getFields().get(0).getFromField()).isEqualTo("id");
    assertThat(edge.getForeignKey().getFields().get(0).getToField()).isEqualTo("test1_id");

    edge = edges.get("test2_tbl");
    assertThat(edge).isNotNull();
    assertThat(edge.getFrom().getName()).isEqualTo("test2_tbl");
    assertThat(edge.getTo().getName()).isEqualTo("test3_tbl");
    assertThat(edge.getForeignKey().getFields().size()).isEqualTo(1);
    assertThat(edge.getForeignKey().getFields().get(0).getFromField()).isEqualTo("id");
    assertThat(edge.getForeignKey().getFields().get(0).getToField()).isEqualTo("test2_id");

    edges = node.getFromEdges().stream()
        .collect(toMap(e -> e.getTo().getName(), identity()));
    assertThat(edges.size()).isEqualTo(0);

    // Validate the node corresponding to test4_tbl

    node = nodes.get("test4_tbl");
    assertThat(node).isNotNull();
    assertThat(node.getName()).isEqualTo("test4_tbl");
    assertThat(node.getPrimaryKey().size()).isEqualTo(1);
    assertThat(node.getPrimaryKey().get(0)).isEqualTo("id");

    edges = node.getToEdges().stream()
        .collect(Collectors.toMap(e -> e.getFrom().getName(), identity()));
    assertThat(edges.size()).isEqualTo(2);

    edge = edges.get("test1_tbl");
    assertThat(edge).isNotNull();
    assertThat(edge.getFrom().getName()).isEqualTo("test1_tbl");
    assertThat(edge.getTo().getName()).isEqualTo("test4_tbl");
    assertThat(edge.getForeignKey().getFields().size()).isEqualTo(1);
    assertThat(edge.getForeignKey().getFields().get(0).getFromField()).isEqualTo("id");
    assertThat(edge.getForeignKey().getFields().get(0).getToField()).isEqualTo("test1_id");

    edge = edges.get("test4_tbl");
    assertThat(edge).isNotNull();
    assertThat(edge.getFrom().getName()).isEqualTo("test4_tbl");
    assertThat(edge.getTo().getName()).isEqualTo("test4_tbl");
    assertThat(edge.getForeignKey().getFields().size()).isEqualTo(1);
    assertThat(edge.getForeignKey().getFields().get(0).getFromField()).isEqualTo("id");
    assertThat(edge.getForeignKey().getFields().get(0).getToField()).isEqualTo("parent_id");

    edges = node.getFromEdges().stream()
        .collect(toMap(e -> e.getTo().getName(), identity()));
    assertThat(edges.size()).isEqualTo(2);

    edge = edges.get("test10_tbl");
    assertThat(edge).isNotNull();
    assertThat(edge.getFrom().getName()).isEqualTo("test4_tbl");
    assertThat(edge.getTo().getName()).isEqualTo("test10_tbl");
    assertThat(edge.getForeignKey().getFields().size()).isEqualTo(1);
    assertThat(edge.getForeignKey().getFields().get(0).getFromField()).isEqualTo("id");
    assertThat(edge.getForeignKey().getFields().get(0).getToField()).isEqualTo("test4_id");

    edge = edges.get("test4_tbl");
    assertThat(edge).isNotNull();
    assertThat(edge.getFrom().getName()).isEqualTo("test4_tbl");
    assertThat(edge.getTo().getName()).isEqualTo("test4_tbl");
    assertThat(edge.getForeignKey().getFields().size()).isEqualTo(1);
    assertThat(edge.getForeignKey().getFields().get(0).getFromField()).isEqualTo("id");
    assertThat(edge.getForeignKey().getFields().get(0).getToField()).isEqualTo("parent_id");

    // Validate the node corresponding to test5_tbl

    node = nodes.get("test5_tbl");
    assertThat(node).isNull();

    // Validate the node corresponding to test6_tbl

    node = nodes.get("test6_tbl");
    assertThat(node).isNull();

    // Validate the node corresponding to test7_tbl

    node = nodes.get("test7_tbl");
    assertThat(node).isNull();

    // Validate the node corresponding to test8_tbl

    node = nodes.get("test8_tbl");
    assertThat(node).isNull();

    // Validate the node corresponding to test9_tbl

    node = nodes.get("test9_tbl");
    assertThat(node).isNull();

    // Validate the node corresponding to test10_tbl

    node = nodes.get("test10_tbl");
    assertThat(node).isNotNull();
    assertThat(node.getName()).isEqualTo("test10_tbl");
    assertThat(node.getPrimaryKey().size()).isEqualTo(1);
    assertThat(node.getPrimaryKey().get(0)).isEqualTo("id");

    edges = node.getToEdges().stream()
        .collect(Collectors.toMap(e -> e.getFrom().getName(), identity()));
    assertThat(edges.size()).isEqualTo(1);

    edge = edges.get("test4_tbl");
    assertThat(edge).isNotNull();
    assertThat(edge.getFrom().getName()).isEqualTo("test4_tbl");
    assertThat(edge.getTo().getName()).isEqualTo("test10_tbl");
    assertThat(edge.getForeignKey().getFields().size()).isEqualTo(1);
    assertThat(edge.getForeignKey().getFields().get(0).getFromField()).isEqualTo("id");
    assertThat(edge.getForeignKey().getFields().get(0).getToField()).isEqualTo("test4_id");

    edges = node.getFromEdges().stream()
        .collect(toMap(e -> e.getTo().getName(), identity()));
    assertThat(edges.size()).isEqualTo(0);

  }


  @Test
  public void test_difference_with_resolute_tables() {
    Graph subgraph1 = schemaGraph.getSubgraphReachableFrom("customer_tbl");
    Graph subgraph2 = schemaGraph.getSubgraphReachableFrom("node_tbl");
    Graph subgraph3 = subgraph1.difference2(subgraph2);

    Map<String, Node> nodes = subgraph3.getNodes().stream()
        .collect(Collectors.toMap(Node::getName, identity()));
    assertThat(nodes.size()).isGreaterThan(0);
    assertThat(nodes.get("customer_tbl")).isNotNull();
    assertThat(nodes.get("component_tbl")).isNotNull();
    assertThat(nodes.get("node_tbl")).isNull();
    assertThat(nodes.get("building_tbl")).isNull();
  }

  @Test
  public void test_difference_with_test_tables() {
    Graph subgraph1 = schemaGraph.getSubgraphReachableFrom("test5_tbl");
    Graph subgraph2 = schemaGraph.getSubgraphReachableFrom("test6_tbl");
    Graph subgraph3 = subgraph1.difference2(subgraph2);

    Map<String, Node> nodes = subgraph3.getNodes().stream()
        .collect(Collectors.toMap(Node::getName, identity()));
    assertThat(nodes.size()).isGreaterThanOrEqualTo(2);

    Node node;
    Map<String, Edge> edges;
    Edge edge;

    // Validate the node corresponding to test5_tbl

    node = nodes.get("test5_tbl");
    assertThat(node).isNotNull();
    assertThat(node.getName()).isEqualTo("test5_tbl");
    assertThat(node.getPrimaryKey().size()).isEqualTo(1);
    assertThat(node.getPrimaryKey().get(0)).isEqualTo("id");

    edges = node.getFromEdges().stream()
        .collect(Collectors.toMap(e -> e.getTo().getName(), identity()));
    assertThat(edges.size()).isEqualTo(1);

    edge = edges.get("test6_tbl");
    assertThat(edge).isNull();

    edge = edges.get("test8_tbl");
    assertThat(edge).isNotNull();
    assertThat(edge.getFrom().getName()).isEqualTo("test5_tbl");
    assertThat(edge.getTo().getName()).isEqualTo("test8_tbl");
    assertThat(edge.getForeignKey().getFields().size()).isEqualTo(1);
    assertThat(edge.getForeignKey().getFields().get(0).getFromField()).isEqualTo("id");
    assertThat(edge.getForeignKey().getFields().get(0).getToField()).isEqualTo("test5_id");

    edges = node.getToEdges().stream()
        .collect(Collectors.toMap(e -> e.getFrom().getName(), identity()));
    assertThat(edges.size()).isEqualTo(0);

    // Validate the node corresponding to test6_tbl

    node = nodes.get("test6_tbl");
    assertThat(node).isNull();

    // Validate the node corresponding to test7_tbl

    node = nodes.get("test7_tbl");
    assertThat(node).isNull();

    // Validate the node corresponding to test8_tbl

    node = nodes.get("test8_tbl");
    assertThat(node).isNotNull();
    assertThat(node.getName()).isEqualTo("test8_tbl");
    assertThat(node.getPrimaryKey().size()).isEqualTo(1);
    assertThat(node.getPrimaryKey().get(0)).isEqualTo("id");

    edges = node.getFromEdges().stream()
        .collect(Collectors.toMap(e -> e.getTo().getName(), identity()));
    assertThat(edges.size()).isEqualTo(1);

    edge = edges.get("test9_tbl");
    assertThat(edge).isNotNull();
    assertThat(edge.getFrom().getName()).isEqualTo("test8_tbl");
    assertThat(edge.getTo().getName()).isEqualTo("test9_tbl");
    assertThat(edge.getForeignKey().getFields().size()).isEqualTo(1);
    assertThat(edge.getForeignKey().getFields().get(0).getFromField()).isEqualTo("id");
    assertThat(edge.getForeignKey().getFields().get(0).getToField()).isEqualTo("test8_id");

    edges = node.getToEdges().stream()
        .collect(Collectors.toMap(e -> e.getFrom().getName(), identity()));
    assertThat(edges.size()).isEqualTo(1);

    edge = edges.get("test5_tbl");
    assertThat(edge).isNotNull();
    assertThat(edge.getFrom().getName()).isEqualTo("test5_tbl");
    assertThat(edge.getTo().getName()).isEqualTo("test8_tbl");
    assertThat(edge.getForeignKey().getFields().size()).isEqualTo(1);
    assertThat(edge.getForeignKey().getFields().get(0).getFromField()).isEqualTo("id");
    assertThat(edge.getForeignKey().getFields().get(0).getToField()).isEqualTo("test5_id");

    // Validate the node corresponding to test9_tbl

    node = nodes.get("test9_tbl");
    assertThat(node).isNotNull();
    assertThat(node.getName()).isEqualTo("test9_tbl");
    assertThat(node.getPrimaryKey().size()).isEqualTo(1);
    assertThat(node.getPrimaryKey().get(0)).isEqualTo("id");

    edges = node.getFromEdges().stream()
        .collect(Collectors.toMap(e -> e.getTo().getName(), identity()));
    assertThat(edges.size()).isEqualTo(0);

    edges = node.getToEdges().stream()
        .collect(Collectors.toMap(e -> e.getFrom().getName(), identity()));
    assertThat(edges.size()).isEqualTo(1);

    edge = edges.get("test8_tbl");
    assertThat(edge).isNotNull();
    assertThat(edge.getFrom().getName()).isEqualTo("test8_tbl");
    assertThat(edge.getTo().getName()).isEqualTo("test9_tbl");
    assertThat(edge.getForeignKey().getFields().size()).isEqualTo(1);
    assertThat(edge.getForeignKey().getFields().get(0).getFromField()).isEqualTo("id");
    assertThat(edge.getForeignKey().getFields().get(0).getToField()).isEqualTo("test8_id");

  }

  @Test
  public void test_get_super_class_roots() {
    Set<String> superclassRoots = schemaGraph.getSuperclassRoots().stream()
        .map(Node::getName)
        .collect(toSet());
    assertThat(superclassRoots.contains("node_tbl")).isEqualTo(true);
    assertThat(superclassRoots.contains("distributor_tbl")).isEqualTo(true);
  }

  @Test
  public void test_topological_sort() {
    Graph subgraph = schemaGraph.getSubgraphReachableFrom("test1_tbl");
    List<Node> sorted = subgraph.topologicalSort();
    with(sorted)
        .assertThat("test1_tbl").isBefore("test3_tbl")
        .assertThat("test1_tbl").isBefore("test4_tbl")
        .assertThat("test4_tbl").isBefore("test10_tbl");
  }

  @Test
  public void test_topological_sort_of_full_schema_graph() {
    List<Node> sorted = schemaGraph.topologicalSort();
    with(sorted)
        .assertThat("node_tbl").isBefore("building_tbl")
        .assertThat("customer_tbl").isBefore("node_tbl")
        .assertThat("distributor_tbl").isBefore("online_distributor_tbl")
        .assertThat("distributor_tbl").isBefore("customer_tbl");
  }

  @Test
  public void test_topological_sort_of_union_with_test_tables() {
    Graph subgraph1 = schemaGraph.getSubgraphReachableFrom("test1_tbl");
    Graph subgraph2 = schemaGraph.getSubgraphReachableFrom("test2_tbl");
    Graph subgraph3 = subgraph1.union(subgraph2);

    List<Node> sorted = subgraph3.topologicalSort();
    Set<String> nodeNames = sorted.stream()
        .map(Node::getName)
        .collect(toSet());
    assertThat(nodeNames)
        .contains("test1_tbl", "test2_tbl", "test3_tbl", "test4_tbl", "test10_tbl")
        .doesNotContain("test5_tbl", "test6_tbl", "test7_tbl", "test8_tbl", "test9_tbl");
    with(sorted)
        .assertThat("test1_tbl").isBefore("test3_tbl")
        .assertThat("test1_tbl").isBefore("test4_tbl")
        .assertThat("test4_tbl").isBefore("test10_tbl")
        .assertThat("test2_tbl").isBefore("test3_tbl");

  }


  @Test
  public void test_is_primary_key_field() {
    Optional<Node> optNode = schemaGraph.getNode("node_tbl2");
    assertThat(optNode).isPresent();

    Node node = optNode.get();
    assertThat(node.isPrimaryKeyField("parent_id")).isFalse();
    assertThat(node.isPrimaryKeyField("name")).isFalse();
    assertThat(node.isPrimaryKeyField("node_type_id")).isFalse();
    assertThat(node.isPrimaryKeyField("id")).isTrue();
    assertThat(node.isPrimaryKeyField("uuid")).isFalse();
    assertThat(node.isPrimaryKeyField("customer_id")).isFalse();
  }

  @Test
  public void test_is_unique_index_field() {
    Optional<Node> optNode = schemaGraph.getNode("node_tbl2");
    assertThat(optNode).isPresent();

    Node node = optNode.get();
    assertThat(node.isUniqueIndexField("parent_id")).isTrue();
    assertThat(node.isUniqueIndexField("name")).isTrue();
    assertThat(node.isUniqueIndexField("node_type_id")).isFalse();
    assertThat(node.isUniqueIndexField("id")).isFalse();
    assertThat(node.isUniqueIndexField("uuid")).isTrue();
    assertThat(node.isUniqueIndexField("customer_id")).isFalse();
  }

  @Test
  public void test_get_primary_key() {
    Optional<Node> optNode = schemaGraph.getNode("node_tbl2");
    assertThat(optNode).isPresent();

    Node node = optNode.get();
    List<String> fields = node.getPrimaryKey();
    assertThat(fields).hasSize(1);
    assertThat(fields.get(0)).isEqualTo("id");
  }

  @Test
  public void test_get_primary_key_fields() {
    Optional<Node> optNode = schemaGraph.getNode("node_tbl2");
    assertThat(optNode).isPresent();

    Node node = optNode.get();
    List<Field> fields = node.getPrimaryKeyFields();
    assertThat(fields).hasSize(1);
    assertThat(fields.get(0))
        .isEqualTo(new Field("id", "integer", "nextval('node_tbl2_id_seq'::regclass)"));
  }

  @Test
  public void test_get_unique_index_fields() {
    Optional<Node> optNode = schemaGraph.getNode("node_tbl2");
    assertThat(optNode).isPresent();

    Node node = optNode.get();
    List<List<Field>> indeces = node.getUniqueIndeces();
    assertThat(indeces).hasSize(2);

    List<Field> uuidIndex = indeces.stream()
        .filter(index -> {
          return index.stream()
              .map(Field::getName)
              .filter(name -> "uuid".equals(name))
              .findAny()
              .isPresent();
        })
        .findAny()
        .orElse(null);

    assertThat(uuidIndex).isNotNull();
    assertThat(uuidIndex).hasSize(1);
    assertThat(uuidIndex.get(0)).isEqualTo(new Field("uuid", "uuid", "gen_random_uuid()"));

    List<Field> parentIdNameTypeIndex = indeces.stream()
        .filter(index -> {
          return index.stream()
              .map(Field::getName)
              .filter(name -> "name".equals(name))
              .findAny()
              .isPresent();
        })
        .findAny()
        .orElse(null);

    assertThat(parentIdNameTypeIndex).isNotNull();
    assertThat(parentIdNameTypeIndex).hasSize(2);
    assertThat(parentIdNameTypeIndex.get(0)).isEqualTo(new Field("parent_id", "integer", null));
    assertThat(parentIdNameTypeIndex.get(1))
        .isEqualTo(new Field("name", "character varying", null));

  }

  @Test
  public void test_get_unique_index_fields_with_user_tbl() {
    Optional<Node> optNode = schemaGraph.getNode("user_tbl");
    assertThat(optNode).isPresent();

    Node node = optNode.get();
    List<List<Field>> indeces = node.getUniqueIndeces();

    List<Field> uuidIndex = indeces.stream()
        .filter(index -> {
          return index.stream()
              .map(Field::getName)
              .filter(name -> "uuid".equals(name))
              .findAny()
              .isPresent();
        })
        .findAny()
        .orElse(null);

    assertThat(uuidIndex).isNotNull();
    assertThat(uuidIndex).hasSize(1);
    assertThat(uuidIndex.get(0)).isEqualTo(new Field("uuid", "uuid", "gen_random_uuid()"));

    List<Field> unlockTokenIndex = indeces.stream()
        .filter(index -> {
          return index.stream()
              .map(Field::getName)
              .filter(name -> "unlock_token".equals(name))
              .findAny()
              .isPresent();
        })
        .findAny()
        .orElse(null);

    assertThat(unlockTokenIndex).isNotNull();
    assertThat(unlockTokenIndex).hasSize(1);
    assertThat(unlockTokenIndex.get(0))
        .isEqualTo(new Field("unlock_token", "character varying", null));

  }

  @Test
  public void test_get_data_type() {
    Optional<Node> optNode = schemaGraph.getNode("node_tbl2");
    assertThat(optNode).isPresent();

    Node node = optNode.get();
    assertThat(node.getDataType("parent_id")).hasValueSatisfying(val -> "integer".equals(val));
    assertThat(node.getDataType("name")).hasValueSatisfying(val -> "character varying".equals(val));
    assertThat(node.getDataType("node_type_id")).hasValueSatisfying(val -> "integer".equals(val));
    assertThat(node.getDataType("id")).hasValueSatisfying(val -> "integer".equals(val));
    assertThat(node.getDataType("uuid")).hasValueSatisfying(val -> "uuid".equals(val));
    assertThat(node.getDataType("customer_id")).hasValueSatisfying(val -> "integer".equals(val));
    assertThat(node.getDataType("display_name"))
        .hasValueSatisfying(val -> "character varying".equals(val));
    assertThat(node.getDataType("display_name2")).isEmpty();

  }

  @Test
  public void test_get_field() {
    Optional<Node> optNode = schemaGraph.getNode("node_tbl2");
    assertThat(optNode).isPresent();

    Node node = optNode.get();
    Optional<Field> optField = node.getField("created_at");
    assertThat(optField).isPresent();
    Field field = optField.get();
    assertThat(field)
        .isEqualTo(new Field("created_at", "timestamp without time zone", "now()"));

    optField = node.getField("created_at_xxx");
    assertThat(optField).isEmpty();
  }

  @Test
  public void test_get_fields() {
    Optional<Node> optNode = schemaGraph.getNode("node_tbl2");
    assertThat(optNode).isPresent();

    Node node = optNode.get();
    List<Field> fields = node.getFields();
    assertThat(fields.size()).isEqualTo(9);

    assertThat(fields.get(0))
        .isEqualTo(new Field("id", "integer", "nextval('node_tbl2_id_seq'::regclass)"));
    assertThat(fields.get(1))
        .isEqualTo(new Field("uuid", "uuid", "gen_random_uuid()"));
    assertThat(fields.get(2))
        .isEqualTo(new Field("customer_id", "integer", null));
    assertThat(fields.get(3))
        .isEqualTo(new Field("node_type_id", "integer", null));
    assertThat(fields.get(4))
        .isEqualTo(new Field("parent_id", "integer", null));
    assertThat(fields.get(5))
        .isEqualTo(new Field("name", "character varying", null));
    assertThat(fields.get(6))
        .isEqualTo(new Field("display_name", "character varying", "''::character varying"));
    assertThat(fields.get(7))
        .isEqualTo(new Field("created_at", "timestamp without time zone", "now()"));
    assertThat(fields.get(8))
        .isEqualTo(new Field("updated_at", "timestamp without time zone", "now()"));

  }
}
