package com.resolute.pecs;

public class Cat extends Feline {
  public Cat() {}

  public Cat(int id) {
    super(id);
  }

  @Override
  public String toString() {
    return "Cat [id=" + id + "]";
  }

}
