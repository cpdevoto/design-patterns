package org.devoware.homonculus.validators.validation;

import static java.util.Objects.requireNonNull;

@SuppressWarnings({"rawtypes", "unchecked"})
public enum ComparisonOperator {
  EQUALS("equal to", (first, second) -> first.compareTo(second) == 0),
  NOT_EQUALS("not equal to", (first, second) -> first.compareTo(second) != 0),
  GREATER_THAN("greater than", (first, second) -> first.compareTo(second) > 0),
  LESS_THAN("less than", (first, second) -> first.compareTo(second) < 0),
  GREATER_THAN_OR_EQUALS("greater than or equal to", (first, second) -> first.compareTo(second) >= 0),
  LESS_THAN_OR_EQUALS("less than or equal to", (first, second) -> first.compareTo(second) <= 0);

  private String description;
  private Operation operation;
  
  private <T extends Comparable<T>> ComparisonOperator(String description, Operation<T> operation) {
    this.description = description;
    this.operation = operation;
  }

  public <T extends Comparable<T>> boolean compare(T first, T second) {
    return operation.compare(requireNonNull(first), requireNonNull(second));
  }
  
  @Override
  public String toString() {
    return description;
  }
  
  private static interface Operation <T extends Comparable<T>> {
    
    public boolean compare (T first, T second);
    
  }
  
  public static void main(String[] args) {
    System.out.println(EQUALS.compare(1, 2));
  }
}
