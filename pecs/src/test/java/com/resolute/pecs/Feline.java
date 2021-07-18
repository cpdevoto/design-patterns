package com.resolute.pecs;

public class Feline extends Animal {

  public Feline() {}

  public Feline(int id) {
    super(id);
  }

  @Override
  public String toString() {
    return "Feline [id=" + id + "]";
  }

}
