package org.devoware.samples;

import java.io.IOException;

import org.devoware.table.Tables;
import org.junit.jupiter.api.Test;

public class WildernessEncountersTest {

  private static final Tables tables = Tables.loadFromClasspath("org.devoware.samples");;

  @Test
  public void test_beach() throws IOException {
    String tableName = "Beach Encounters";
    int numDays = 10;
    System.out.println("BEACH:\n");
    for (int i = 0; i < numDays; i++) {
      String result = tables.roll(tableName);
      System.out.printf("Day %d:%n------------%n%s%n%n", i + 1, result);
    }
    System.out.println();
  }


}
