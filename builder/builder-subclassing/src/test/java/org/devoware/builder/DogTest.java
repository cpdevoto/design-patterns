package org.devoware.builder;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class DogTest {
  
  @Test
  public void test_build () {
    Dog dog = Dog.builder()
        .withName("Fido")
        .withBark("Woof")
        .build();
    assertThat(dog.getName(), equalTo("Fido"));
    assertThat(dog.getBark(), equalTo("Woof"));
    System.out.println(dog);
  }

}
