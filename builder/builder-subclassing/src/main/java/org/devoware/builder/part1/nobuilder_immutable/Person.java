package org.devoware.builder.part1.nobuilder_immutable;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.time.LocalDate;

import org.devoware.builder.model.Gender;

public class Person {
  private final String firstName;    // REQUIRED
  private final String lastName;     // REQUIRED
  private final String email;        // REQUIRED
  private final Gender gender;       // OPTIONAL
  private final LocalDate birthDate; // OPTIONAL
  private final int numPets;         // OPTIONAL; non-negative integer
  
  public Person (String firstName, String lastName, String email, Gender gender, LocalDate birthDate, int numPets) {
    this.firstName = requireNonNull(firstName, "firstName cannot be null");
    this.lastName = requireNonNull(lastName, "lastName cannot be null");
    this.email = requireNonNull(email, "email cannot be null");
    this.gender = gender;
    this.birthDate = birthDate;
    checkArgument(numPets >= 0, "numPets must be greater than or equal to zero");
    this.numPets = numPets;
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

  public Gender getGender() {
    return gender;
  }

  public LocalDate getBirthDate() {
    return birthDate;
  }

  public int getNumPets() {
    return numPets;
  }

}


