package org.devoware.builder.part9.builder_subclassing_w_interfaces;

public interface Animal {

  public String getName();


  public static interface Builder<T extends Animal, B extends Builder<T, B>> {

    public B withName(String name);

    public T build();
  }

}
