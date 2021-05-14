package org.devoware.names.application;

import org.junit.jupiter.api.Test;

public class NameGeneratorTest {

  @Test
  public void test_generate() {
    for (int i = 0; i < 10; i++) {
      System.out.println(NameGenerator.DRAGONBORN_MALE.generateName());
    }
    System.out.println();
    for (int i = 0; i < 10; i++) {
      System.out.println(NameGenerator.DRAGONBORN_FEMALE.generateName());
    }
    System.out.println();
  }

}
