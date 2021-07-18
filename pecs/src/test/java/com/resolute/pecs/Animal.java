package com.resolute.pecs;

import java.util.concurrent.ThreadLocalRandom;

public class Animal implements Comparable<Animal> {
  protected int id = ThreadLocalRandom.current().nextInt();

  public Animal() {}

  public Animal(int id) {
    this.id = id;
  }

  public int getId() {
    return id;
  }

  @Override
  public int compareTo(Animal o) {
    return id - o.id;
  }

  @Override
  public String toString() {
    return "Animal [id=" + id + "]";
  }


}
