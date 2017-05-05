package org.devoware.builder.part5.nobuilder_mutable_optional_copy_constr_method_chain;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.devoware.builder.model.Gender;
import org.junit.Test;

public class PersonTest {

  @Test
  public void test_construction () {
    Person person = new Person("Fred", "Flintstone", "fflintstone@hannahbarbera.com")
        .setGender(Gender.MALE)
        .setNumPets(4);
    
    // Problem: Object is mutable!
    
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
