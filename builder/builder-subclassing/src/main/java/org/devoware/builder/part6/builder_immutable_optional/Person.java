package org.devoware.builder.part6.builder_immutable_optional;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.time.LocalDate;
import java.util.Optional;

import org.devoware.builder.model.Gender;

public class Person {
  private final String firstName;               // REQUIRED
  private final String lastName;                // REQUIRED
  private final String email;                   // REQUIRED
  private final Optional<Gender> gender;        // OPTIONAL
  private final Optional<LocalDate> birthDate;  // OPTIONAL
  private final Optional<Integer> numPets;      // OPTIONAL; non-negative integer
  
  public static Builder builder () {
    return new Builder();
  }
  
  private Person (Builder builder) {
    this.firstName = builder.firstName;
    this.lastName = builder.lastName;
    this.email = builder.email;
    this.gender = builder.gender;
    this.birthDate = builder.birthDate;
    this.numPets = builder.numPets;
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

  public static class Builder {
    private String firstName;               // REQUIRED
    private String lastName;                // REQUIRED
    private String email;                   // REQUIRED
    private Optional<Gender> gender;        // OPTIONAL
    private Optional<LocalDate> birthDate;  // OPTIONAL
    private Optional<Integer> numPets;      // OPTIONAL; non-negative integer
    
    private Builder () {
      this.gender = Optional.empty();
      this.birthDate = Optional.empty();
      this.numPets = Optional.empty();
    }
    
    public Builder withFirstName(String firstName) {
      this.firstName = requireNonNull(firstName, "firstName cannot be null");
      return this;
    }

    public Builder withLastName(String lastName) {
      this.lastName = requireNonNull(lastName, "lastName cannot be null");
      return this;
    }

    public Builder withEmail(String email) {
      this.email = requireNonNull(email, "email cannot be null");
      return this;
    }

    public Builder withGender(Gender gender) {
      this.gender = gender != null ? Optional.of(gender) : Optional.empty();
      return this;
    }

    public Builder withBirthDate(LocalDate birthDate) {
      this.birthDate = birthDate != null ? Optional.of(birthDate) : Optional.empty();
      return this;
    }

    public Builder withNumPets(int numPets) {
      checkArgument(numPets >= 0, "numPets must be greater than or equal to zero");
      this.numPets = Optional.of(numPets);
      return this;
    }

    public Person build () {
      requireNonNull(firstName, "firstName cannot be null");
      requireNonNull(lastName, "lastName cannot be null");
      requireNonNull(email, "email cannot be null");
      return new Person(this);
    }
  }
}


