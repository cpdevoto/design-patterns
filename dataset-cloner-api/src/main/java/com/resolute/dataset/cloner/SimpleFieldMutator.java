package com.resolute.dataset.cloner;

import static java.util.Objects.requireNonNull;

import java.util.function.UnaryOperator;

public class SimpleFieldMutator implements FieldMutator {
  private final String name;
  private final UnaryOperator<Object> mutator;

  public SimpleFieldMutator(String name, UnaryOperator<Object> mutator) {
    this.name = requireNonNull(name, "name cannot be null");
    this.mutator = requireNonNull(mutator, "mutator cannot be null");
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public Object mutate(Object value) {
    return mutator.apply(value);
  }

}
