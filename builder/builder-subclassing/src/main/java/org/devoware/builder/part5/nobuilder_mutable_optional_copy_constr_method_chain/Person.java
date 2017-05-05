package org.devoware.builder.part5.nobuilder_mutable_optional_copy_constr_method_chain;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.time.LocalDate;
import java.util.Optional;

import org.devoware.builder.model.Gender;

public class Person {
  private String firstName;               // REQUIRED
  private String lastName;                // REQUIRED
  private String email;                   // REQUIRED
  private Optional<Gender> gender;        // OPTIONAL
  private Optional<LocalDate> birthDate;  // OPTIONAL
  private Optional<Integer> numPets;      // OPTIONAL; non-negative integer
  
  public Person (String firstName, String lastName, String email) {
    setFirstName(firstName);
    setLastName(lastName);
    setEmail(email);
    this.gender = Optional.empty();
    this.birthDate = Optional.empty();
    this.numPets = Optional.empty();
  }
  
  public Person (Person person) {
    this.firstName = person.firstName;
    this.lastName = person.lastName;
    this.email = person.email;
    this.gender = person.gender;
    this.birthDate = person.birthDate;
    this.numPets = person.numPets;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public String getEmail() {
    return email;
  }

  public Optional<Gender> getGender() {
    return gender;
  }

  public Optional<LocalDate> getBirthDate() {
    return birthDate;
  }

  public Optional<Integer> getNumPets() {
    return numPets;
  }

  public Person setFirstName(String firstName) {
    this.firstName = requireNonNull(firstName, "firstName cannot be null");
    return this;
  }

  public Person setLastName(String lastName) {
    this.lastName = requireNonNull(lastName, "lastName cannot be null");
    return this;
  }

  public Person setEmail(String email) {
    this.email = requireNonNull(email, "email cannot be null");
    return this;
  }

  public Person setGender(Gender gender) {
    this.gender = gender != null ? Optional.of(gender) : Optional.empty();
    return this;
  }

  public Person setBirthDate(LocalDate birthDate) {
    this.birthDate = birthDate != null ? Optional.of(birthDate) : Optional.empty();
    return this;
  }

  public Person setNumPets(int numPets) {
    checkArgument(numPets >= 0, "numPets must be greater than or equal to zero");
    this.numPets = Optional.of(numPets);
    return this;
  }

}


