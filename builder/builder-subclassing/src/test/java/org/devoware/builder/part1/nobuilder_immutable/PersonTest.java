package org.devoware.builder.part1.nobuilder_immutable;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class PersonTest {

  @Test
  public void test_construction () {
    Person person = new Person("Fred", "Flintstone", "fflintstone@hannahbarbera.com", null, null, 4);
    
    // Problem: constructor becomes unwieldy as more attributes are added; 
    // Problem: impossible to tell what arguments correspond to which attributes without looking at Javadoc
    // Problem: Optional attributes are represented with null when the value is absent
    
    assertThat(person.getFirstName(), equalTo("Fred"));
    assertThat(person.getLastName(), equalTo("Flintstone"));
    assertThat(person.getEmail(), equalTo("fflintstone@hannahbarbera.com"));
    assertNull(person.getGender());
    assertNull(person.getBirthDate());
    assertThat(person.getNumPets(), equalTo(4));
  }
}
