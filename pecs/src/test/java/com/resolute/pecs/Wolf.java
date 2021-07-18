package com.resolute.pecs;

public class Wolf extends Canine {
  public Wolf() {}

  public Wolf(int id) {
    super(id);
  }

  @Override
  public String toString() {
    return "Wolf [id=" + id + "]";
  }

}
