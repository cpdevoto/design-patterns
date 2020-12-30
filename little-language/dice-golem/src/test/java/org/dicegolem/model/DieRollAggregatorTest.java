package org.dicegolem.model;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.dicegolem.model.DieRollAggregator;
import org.dicegolem.model.DropHighestAggregator;
import org.dicegolem.model.DropLowestAggregator;
import org.dicegolem.model.KeepHighestAggregator;
import org.dicegolem.model.KeepLowestAggregator;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

public class DieRollAggregatorTest {

  @Test
  public void test_keep_highest_aggregator() {
    DieRollAggregator aggregator = new KeepHighestAggregator(1);
    int result = aggregator.aggregate(ImmutableList.of(1, 2, 3));
    assertThat(result, equalTo(3));
    result = aggregator.aggregate(ImmutableList.of(1, 2));
    assertThat(result, equalTo(2));
    result = aggregator.aggregate(ImmutableList.of(1));
    assertThat(result, equalTo(1));

    aggregator = new KeepHighestAggregator(2);
    result = aggregator.aggregate(ImmutableList.of(1, 2, 3));
    assertThat(result, equalTo(5));
    result = aggregator.aggregate(ImmutableList.of(1, 2));
    assertThat(result, equalTo(3));
    result = aggregator.aggregate(ImmutableList.of(1));
    assertThat(result, equalTo(1));

    aggregator = new KeepHighestAggregator(3);
    result = aggregator.aggregate(ImmutableList.of(1, 2, 3));
    assertThat(result, equalTo(6));
    result = aggregator.aggregate(ImmutableList.of(1, 2));
    assertThat(result, equalTo(3));
    result = aggregator.aggregate(ImmutableList.of(1));
    assertThat(result, equalTo(1));

    aggregator = new KeepHighestAggregator(4);
    result = aggregator.aggregate(ImmutableList.of(1, 2, 3));
    assertThat(result, equalTo(6));
    result = aggregator.aggregate(ImmutableList.of(1, 2));
    assertThat(result, equalTo(3));
    result = aggregator.aggregate(ImmutableList.of(1));
    assertThat(result, equalTo(1));
  }

  @Test
  public void test_keep_lowest_aggregator() {
    DieRollAggregator aggregator = new KeepLowestAggregator(1);
    int result = aggregator.aggregate(ImmutableList.of(1, 2, 3));
    assertThat(result, equalTo(1));
    result = aggregator.aggregate(ImmutableList.of(1, 2));
    assertThat(result, equalTo(1));
    result = aggregator.aggregate(ImmutableList.of(1));
    assertThat(result, equalTo(1));

    aggregator = new KeepLowestAggregator(2);
    result = aggregator.aggregate(ImmutableList.of(1, 2, 3));
    assertThat(result, equalTo(3));
    result = aggregator.aggregate(ImmutableList.of(1, 2));
    assertThat(result, equalTo(3));
    result = aggregator.aggregate(ImmutableList.of(1));
    assertThat(result, equalTo(1));

    aggregator = new KeepLowestAggregator(3);
    result = aggregator.aggregate(ImmutableList.of(1, 2, 3));
    assertThat(result, equalTo(6));
    result = aggregator.aggregate(ImmutableList.of(1, 2));
    assertThat(result, equalTo(3));
    result = aggregator.aggregate(ImmutableList.of(1));
    assertThat(result, equalTo(1));

    aggregator = new KeepLowestAggregator(4);
    result = aggregator.aggregate(ImmutableList.of(1, 2, 3));
    assertThat(result, equalTo(6));
    result = aggregator.aggregate(ImmutableList.of(1, 2));
    assertThat(result, equalTo(3));
    result = aggregator.aggregate(ImmutableList.of(1));
    assertThat(result, equalTo(1));
  }

  @Test
  public void test_drop_highest_aggregator() {
    DieRollAggregator aggregator = new DropHighestAggregator(1);
    int result = aggregator.aggregate(ImmutableList.of(1, 2, 3));
    assertThat(result, equalTo(3));
    result = aggregator.aggregate(ImmutableList.of(1, 2));
    assertThat(result, equalTo(1));
    result = aggregator.aggregate(ImmutableList.of(1));
    assertThat(result, equalTo(0));

    aggregator = new DropHighestAggregator(2);
    result = aggregator.aggregate(ImmutableList.of(1, 2, 3));
    assertThat(result, equalTo(1));
    result = aggregator.aggregate(ImmutableList.of(1, 2));
    assertThat(result, equalTo(0));
    result = aggregator.aggregate(ImmutableList.of(1));
    assertThat(result, equalTo(0));

    aggregator = new DropHighestAggregator(3);
    result = aggregator.aggregate(ImmutableList.of(1, 2, 3));
    assertThat(result, equalTo(0));
    result = aggregator.aggregate(ImmutableList.of(1, 2));
    assertThat(result, equalTo(0));
    result = aggregator.aggregate(ImmutableList.of(1));
    assertThat(result, equalTo(0));

    aggregator = new DropHighestAggregator(4);
    result = aggregator.aggregate(ImmutableList.of(1, 2, 3));
    assertThat(result, equalTo(0));
    result = aggregator.aggregate(ImmutableList.of(1, 2));
    assertThat(result, equalTo(0));
    result = aggregator.aggregate(ImmutableList.of(1));
    assertThat(result, equalTo(0));
  }

  @Test
  public void test_drop_lowest_aggregator() {
    DieRollAggregator aggregator = new DropLowestAggregator(1);
    int result = aggregator.aggregate(ImmutableList.of(1, 2, 3));
    assertThat(result, equalTo(5));
    result = aggregator.aggregate(ImmutableList.of(1, 2));
    assertThat(result, equalTo(2));
    result = aggregator.aggregate(ImmutableList.of(1));
    assertThat(result, equalTo(0));

    aggregator = new DropLowestAggregator(2);
    result = aggregator.aggregate(ImmutableList.of(1, 2, 3));
    assertThat(result, equalTo(3));
    result = aggregator.aggregate(ImmutableList.of(1, 2));
    assertThat(result, equalTo(0));
    result = aggregator.aggregate(ImmutableList.of(1));
    assertThat(result, equalTo(0));

    aggregator = new DropLowestAggregator(3);
    result = aggregator.aggregate(ImmutableList.of(1, 2, 3));
    assertThat(result, equalTo(0));
    result = aggregator.aggregate(ImmutableList.of(1, 2));
    assertThat(result, equalTo(0));
    result = aggregator.aggregate(ImmutableList.of(1));
    assertThat(result, equalTo(0));

    aggregator = new DropLowestAggregator(4);
    result = aggregator.aggregate(ImmutableList.of(1, 2, 3));
    assertThat(result, equalTo(0));
    result = aggregator.aggregate(ImmutableList.of(1, 2));
    assertThat(result, equalTo(0));
    result = aggregator.aggregate(ImmutableList.of(1));
    assertThat(result, equalTo(0));
  }

}
