package com.resolute.pecs;

public class Canine extends Animal {

  public Canine() {}

  public Canine(int id) {
    super(id);
  }

  @Override
  public String toString() {
    return "Canine [id=" + id + "]";
  }

}
