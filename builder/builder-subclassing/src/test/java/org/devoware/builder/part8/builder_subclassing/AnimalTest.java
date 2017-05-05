package org.devoware.builder.part8.builder_subclassing;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.devoware.builder.part8.builder_subclassing.Dog;
import org.junit.Test;

public class AnimalTest {
  
  @Test
  public void test_build () {
    Dog dog = Dog.builder()
        .withName("Fido")
        .withBark("Woof")
        .build();
    assertThat(dog.getName(), equalTo("Fido"));
    assertThat(dog.getBark(), equalTo("Woof"));
    System.out.println(dog);

    Goat goat = Goat.builder()
        .withName("Fido")
        .withNumHorns(2)
        .build();
    assertThat(goat.getName(), equalTo("Fido"));
    assertThat(goat.getNumHorns(), equalTo(2));
    System.out.println(goat);
  }

}
