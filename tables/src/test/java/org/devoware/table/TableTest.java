package org.devoware.table;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import org.devoware.testutils.Distribution;
import org.junit.jupiter.api.Test;

public class TableTest {

  @Test
  public void test_roll() {
    Table table = Table.builder()
        .withName("Table 1")
        .withEntry(1, 4, "Result 1")
        .withEntry(5, 6, "Result 2")
        .withEntry(7, 8, "Result 3")
        .withEntry(9, 9, "Result 4")
        .withEntry(10, 10, "Result 5")
        .build();

    Distribution distribution = new Distribution(() -> table.roll());

    assertThat(distribution.getPercentage("Result 1")).isCloseTo(40, within(0.1));
    assertThat(distribution.getPercentage("Result 2")).isCloseTo(20, within(0.1));
    assertThat(distribution.getPercentage("Result 3")).isCloseTo(20, within(0.1));
    assertThat(distribution.getPercentage("Result 4")).isCloseTo(10, within(0.1));
    assertThat(distribution.getPercentage("Result 5")).isCloseTo(10, within(0.1));

  }


}
