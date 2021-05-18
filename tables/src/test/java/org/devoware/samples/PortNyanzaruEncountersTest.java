package org.devoware.samples;

import java.io.IOException;

import org.devoware.table.Tables;
import org.junit.jupiter.api.Test;

public class PortNyanzaruEncountersTest {

  @Test
  public void test() throws IOException {
    Tables tables = Tables.loadFromClasspath("org.devoware.samples");

    String tableName = "Port Nyanzaru Encounters";
    int numDays = 5;
    for (int i = 0; i < numDays; i++) {
      String result = tables.roll(tableName);
      System.out.printf("Day %d:%n------------%n%s%n%n", i + 1, result);
    }


  }

}
