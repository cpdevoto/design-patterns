package com.resolute.database.crawler.model;

import static com.resolute.database.crawler.model.Comparators.fromEdgeComparator;
import static com.resolute.database.crawler.model.Comparators.toEdgeComparator;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableList;
import com.resolute.database.crawler.model.Edge;
import com.resolute.database.crawler.model.ForeignKey;
import com.resolute.database.crawler.model.ForeignKeyField;
import com.resolute.database.crawler.model.Node;

public class ComparatorsTest {

  private Node node = new Node("node_tbl", ImmutableList.of("id"));
  private Node nodeClosure =
      new Node("node_closure_tbl", ImmutableList.of("parent_id", "child_id"));
  private Node customer = new Node("customer_tbl", ImmutableList.of("id"));
  private Node rawPoint = new Node("raw_point_tbl", ImmutableList.of("id"));
  private Edge nodeNodeClosureParentId =
      new Edge(node, nodeClosure,
          new ForeignKey(ImmutableList.of(new ForeignKeyField("id", "parent_id"))));
  private Edge nodeNodeClosureChildId =
      new Edge(node, nodeClosure,
          new ForeignKey(ImmutableList.of(new ForeignKeyField("id", "child_id"))));
  private Edge nodeRawPoint =
      new Edge(node, rawPoint,
          new ForeignKey(ImmutableList.of(new ForeignKeyField("id", "node_id"))));
  private Edge customerRawPoint =
      new Edge(customer, rawPoint,
          new ForeignKey(ImmutableList.of(new ForeignKeyField("id", "customer_id"))));

  @Test
  public void test_from_edge_comparator() {
    int result = fromEdgeComparator().compare(nodeNodeClosureParentId, nodeNodeClosureChildId);
    assertThat(result).isGreaterThan(0);
    result = fromEdgeComparator().compare(nodeNodeClosureChildId, nodeNodeClosureParentId);
    assertThat(result).isLessThan(0);
    result = fromEdgeComparator().compare(nodeNodeClosureParentId, nodeNodeClosureParentId);
    assertThat(result).isEqualTo(0);

    result = fromEdgeComparator().compare(nodeNodeClosureParentId, nodeRawPoint);
    assertThat(result).isLessThan(0);
    result = fromEdgeComparator().compare(nodeRawPoint, nodeNodeClosureParentId);
    assertThat(result).isGreaterThan(0);

  }

  @Test
  public void test_to_edge_comparator() {
    int result = toEdgeComparator().compare(nodeNodeClosureParentId, nodeNodeClosureChildId);
    assertThat(result).isGreaterThan(0);
    result = toEdgeComparator().compare(nodeNodeClosureChildId, nodeNodeClosureParentId);
    assertThat(result).isLessThan(0);
    result = toEdgeComparator().compare(nodeNodeClosureParentId, nodeNodeClosureParentId);
    assertThat(result).isEqualTo(0);

    result = toEdgeComparator().compare(nodeRawPoint, customerRawPoint);
    assertThat(result).isGreaterThan(0);
    result = toEdgeComparator().compare(customerRawPoint, nodeRawPoint);
    assertThat(result).isLessThan(0);
  }
}
