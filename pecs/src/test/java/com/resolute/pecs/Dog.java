package com.resolute.pecs;

public class Dog extends Canine {
  public Dog() {}

  public Dog(int id) {
    super(id);
  }

  @Override
  public String toString() {
    return "Dog [id=" + id + "]";
  }

}
