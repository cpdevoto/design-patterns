package org.devoware.builder.part4.nobuilder_mutable_optional_copy_constructor;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.devoware.builder.model.Gender;
import org.junit.Test;

public class PersonTest {

  @Test
  public void test_construction () {
    Person person = new Person("Fred", "Flintstone", "fflintstone@hannahbarbera.com");
    person.setGender(Gender.MALE);
    person.setNumPets(4);
    
    // Problem: Object is mutable!
    // Problem: It requires multiple statements to initialize the object.
    
    assertThat(person.getFirstName(), equalTo("Fred"));
    assertThat(person.getLastName(), equalTo("Flintstone"));
    assertThat(person.getEmail(), equalTo("fflintstone@hannahbarbera.com"));
    assertTrue(person.getGender().isPresent());
    assertThat(person.getGender().get(), equalTo(Gender.MALE));
    assertFalse(person.getBirthDate().isPresent());
    assertTrue(person.getNumPets().isPresent());
    assertThat(person.getNumPets().get(), equalTo(4));
    
    Person person2 = new Person(person);
    
    assertThat(person2.getFirstName(), equalTo("Fred"));
    assertThat(person2.getLastName(), equalTo("Flintstone"));
    assertThat(person2.getEmail(), equalTo("fflintstone@hannahbarbera.com"));
    assertTrue(person2.getGender().isPresent());
    assertThat(person2.getGender().get(), equalTo(Gender.MALE));
    assertFalse(person2.getBirthDate().isPresent());
    assertTrue(person2.getNumPets().isPresent());
    assertThat(person2.getNumPets().get(), equalTo(4));
    
  }
}
