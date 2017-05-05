package org.devoware.builder.part2.nobuilder_immutable_optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class PersonTest {

  @Test
  public void test_construction () {
    Person person = new Person("Fred", "Flintstone", "fflintstone@hannahbarbera.com", null, null, 4);
    
    // Problem: constructor becomes unwieldy as more attributes are added; 
    // Problem: impossible to tell what arguments correspond to which attributes without looking at Javadoc
    
    assertThat(person.getFirstName(), equalTo("Fred"));
    assertThat(person.getLastName(), equalTo("Flintstone"));
    assertThat(person.getEmail(), equalTo("fflintstone@hannahbarbera.com"));
    assertFalse(person.getGender().isPresent());
    assertFalse(person.getBirthDate().isPresent());
    assertTrue(person.getNumPets().isPresent());
    assertThat(person.getNumPets().get(), equalTo(4));
  }
}
