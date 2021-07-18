package com.resolute.pecs;

public class Lion extends Feline {
  public Lion() {}

  public Lion(int id) {
    super(id);
  }

  @Override
  public String toString() {
    return "Lion [id=" + id + "]";
  }

}
