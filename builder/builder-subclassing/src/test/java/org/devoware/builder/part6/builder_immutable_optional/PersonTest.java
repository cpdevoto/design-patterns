package org.devoware.builder.part6.builder_immutable_optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.devoware.builder.model.Gender;
import org.junit.Test;

public class PersonTest {

  @Test
  public void test_construction () {
    Person person = Person.builder()
        .withFirstName("Fred")
        .withLastName("Flintstone")
        .withEmail("fflintstone@hannahbarbera.com")
        .withGender(Gender.MALE)
        .withNumPets(4)
        .build();
    
    assertThat(person.getFirstName(), equalTo("Fred"));
    assertThat(person.getLastName(), equalTo("Flintstone"));
    assertThat(person.getEmail(), equalTo("fflintstone@hannahbarbera.com"));
    assertTrue(person.getGender().isPresent());
    assertThat(person.getGender().get(), equalTo(Gender.MALE));
    assertFalse(person.getBirthDate().isPresent());
    assertTrue(person.getNumPets().isPresent());
    assertThat(person.getNumPets().get(), equalTo(4));
  }
}
