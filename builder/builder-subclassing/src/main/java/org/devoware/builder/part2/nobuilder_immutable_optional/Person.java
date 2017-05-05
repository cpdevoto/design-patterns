package org.devoware.builder.part2.nobuilder_immutable_optional;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.time.LocalDate;
import java.util.Optional;

import org.devoware.builder.model.Gender;

public class Person {
  private final String firstName;    // REQUIRED
  private final String lastName;     // REQUIRED
  private final String email;        // REQUIRED
  private final Optional<Gender> gender;       // OPTIONAL
  private final Optional<LocalDate> birthDate; // OPTIONAL
  private final Optional<Integer> numPets;         // OPTIONAL; non-negative integer
  
  public Person (String firstName, String lastName, String email, Gender gender, LocalDate birthDate, int numPets) {
    this.firstName = requireNonNull(firstName, "firstName cannot be null");
    this.lastName = requireNonNull(lastName, "lastName cannot be null");
    this.email = requireNonNull(email, "email cannot be null");
    
    this.gender = gender != null ? Optional.of(gender) : Optional.empty();
    this.birthDate = birthDate != null ? Optional.of(birthDate) : Optional.empty();
    checkArgument(numPets >= 0, "numPets must be greater than or equal to zero");
    this.numPets = Optional.of(numPets);
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

}


