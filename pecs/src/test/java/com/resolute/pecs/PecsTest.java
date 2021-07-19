package com.resolute.pecs;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

@SuppressWarnings("unused")
public class PecsTest {

  /*
   * The signature for Comparator.comparing looks like this:
   * 
   * public static <T, U extends Comparable<? super U>> Comparator<T> comparing( Function<? super T,
   * ? extends U> keyExtractor)
   * 
   * Why is it so complex? To understand this signature, you first need to understand the PECS
   * principle: Producer Extends, Consumer Super.
   */

  @Test
  public void intro() {
    List<Canine> canines =
        new ArrayList<>(Arrays.asList(new Dog(4), new Wolf(3), new Dog(8), new Wolf(1)));

    Comparator<Canine> comparator = Comparator.comparing((Animal a) -> a.getId());

    Collections.sort(canines, comparator);

    assertThat(canines.get(0)).isInstanceOf(Wolf.class);
    assertThat(canines.get(0).getId()).isEqualTo(1);
    assertThat(canines.get(1)).isInstanceOf(Wolf.class);
    assertThat(canines.get(1).getId()).isEqualTo(3);
    assertThat(canines.get(2)).isInstanceOf(Dog.class);
    assertThat(canines.get(2).getId()).isEqualTo(4);
    assertThat(canines.get(3)).isInstanceOf(Dog.class);
    assertThat(canines.get(3).getId()).isEqualTo(8);

  }

  @Test
  public void test_producer_without_pecs() {
    Supplier<Animal> producer1 = () -> new Animal();
    Supplier<Canine> producer2 = () -> new Canine();
    Supplier<Dog> producer3 = () -> new Dog();

    // A producer of Animal objects cannot produce objects assignable to Canine, so the following
    // should never be allowed.
    // processCanineProducerWithoutPecs(producer1);

    processCanineProducerWithoutPecs(producer2);

    // Since Dog is a subclass of Canine it should be possible to use a producer of Dog objects in
    // order produce objects assignable to Canine, but the compiler does not allow it.
    // processCanineProducerWithoutPecs(producer3);
  }

  @Test
  public void test_producer_with_pecs() {
    Supplier<Animal> producer1 = () -> new Animal();
    Supplier<Canine> producer2 = () -> new Canine();
    Supplier<Dog> producer3 = () -> new Dog();

    // A producer of Animal objects cannot produce objects assignable to Canine, so the following
    // should never be allowed.
    // processCanineProducerWithPecs(producer1);

    processCanineProducerWithPecs(producer2);

    processCanineProducerWithPecs(producer3);
  }

  private void processCanineProducerWithoutPecs(Supplier<Canine> supplier) {
    Canine b = supplier.get();
    System.out.println("Produced: " + b);
  }

  private void processCanineProducerWithPecs(Supplier<? extends Canine> supplier) {
    Canine b = supplier.get();
    System.out.println("Produced: " + b);
  }

  @Test
  public void test_consumer_without_pecs() {
    Consumer<Animal> consumer1 = (Animal a) -> System.out.println("Consuming " + a);
    Consumer<Canine> consumer2 = (Canine b) -> System.out.println("Consuming " + b);
    Consumer<Dog> consumer3 = (Dog c) -> System.out.println("Consuming " + c);

    // Since Canine is a subclass of Animal it should be possible to use a consumer of Animal
    // objects in order consume Canine objects, but the compiler does not allow it.
    // processCanineConsumerWithoutPecs(consumer1);

    processCanineConsumerWithoutPecs(consumer2);

    // A consumer of Dog objects cannot consume Canine objects, so the following should never be
    // allowed.
    // processCanineConsumerWithoutPecs(consumer3);
  }

  @Test
  public void test_consumer_with_pecs() {
    Consumer<Animal> consumer1 = (Animal a) -> System.out.println("Consuming " + a);
    Consumer<Canine> consumer2 = (Canine b) -> System.out.println("Consuming " + b);
    Consumer<Dog> consumer3 = (Dog c) -> System.out.println("Consuming " + c);

    processCanineConsumerWithPecs(consumer1);

    processCanineConsumerWithPecs(consumer2);

    // A consumer of Dog objects cannot consume Canine objects, so the following should never be
    // allowed.
    // processCanineConsumerWithPecs(consumer3);
  }

  private void processCanineConsumerWithoutPecs(Consumer<Canine> consumer) {
    Canine b = new Canine();
    consumer.accept(b);
  }

  private void processCanineConsumerWithPecs(Consumer<? super Canine> consumer) {
    Canine b = new Canine();
    consumer.accept(b);
  }

  @Test
  public void test_function_without_pecs() {
    Function<Animal, Animal> functionAA = (Animal a) -> new Animal();
    Function<Animal, Canine> functionAB = (Animal a) -> new Canine();
    Function<Animal, Dog> functionAC = (Animal a) -> new Dog();
    Function<Canine, Animal> functionBA = (Canine b) -> new Animal();
    Function<Canine, Canine> functionBB = (Canine b) -> new Canine();
    Function<Canine, Dog> functionBC = (Canine b) -> new Dog();
    Function<Dog, Animal> functionCA = (Dog c) -> new Animal();
    Function<Dog, Canine> functionCB = (Dog c) -> new Canine();
    Function<Dog, Dog> functionCC = (Dog c) -> new Dog();

    // Canine resultAA = processCanineFunctionWithoutPecs(functionAA);
    // Canine resultAB = processCanineFunctionWithoutPecs(functionAB);
    // Canine resultAC = processCanineFunctionWithoutPecs(functionAC);
    // Canine resultBA = processCanineFunctionWithoutPecs(functionBA);
    Canine resultBB = processCanineFunctionWithoutPecs(functionBB);
    // Canine resultBC = processCanineFunctionWithoutPecs(functionBC);
    // Canine resultCA = processCanineFunctionWithoutPecs(functionCA);
    // Canine resultCB = processCanineFunctionWithoutPecs(functionCB);
    // Canine resultCC = processCanineFunctionWithoutPecs(functionCC);
  }

  @Test
  public void test_function_with_pecs() {
    Function<Animal, Animal> functionAA = (Animal a) -> new Animal();
    Function<Animal, Canine> functionAB = (Animal a) -> new Canine();
    Function<Animal, Dog> functionAC = (Animal a) -> new Dog();
    Function<Canine, Animal> functionBA = (Canine b) -> new Animal();
    Function<Canine, Canine> functionBB = (Canine b) -> new Canine();
    Function<Canine, Dog> functionBC = (Canine b) -> new Dog();
    Function<Dog, Animal> functionCA = (Dog c) -> new Animal();
    Function<Dog, Canine> functionCB = (Dog c) -> new Canine();
    Function<Dog, Dog> functionCC = (Dog c) -> new Dog();

    // Canine resultAA = processCanineFunctionWithPecs(functionAA);
    Canine resultAB = processCanineFunctionWithPecs(functionAB);
    Canine resultAC = processCanineFunctionWithPecs(functionAC);
    // Canine resultBA = processCanineFunctionWithPecs(functionBA);
    Canine resultBB = processCanineFunctionWithPecs(functionBB);
    Canine resultBC = processCanineFunctionWithPecs(functionBC);
    // Canine resultCA = processCanineFunctionWithPecs(functionCA);
    // Canine resultCB = processCanineFunctionWithPecs(functionCB);
    // Canine resultCC = processCanineFunctionWithPecs(functionCC);
  }

  private Canine processCanineFunctionWithoutPecs(Function<Canine, Canine> function) {
    Canine c = function.apply(new Canine());
    return c;
  }

  private Canine processCanineFunctionWithPecs(
      Function<? super Canine, ? extends Canine> function) {
    Canine c = function.apply(new Canine());
    return c;
  }

  @Test
  public void test_list_acting_as_consumer_without_pecs() {
    List<Animal> listA = new ArrayList<>();
    List<Canine> listB = new ArrayList<>();
    List<Dog> listC = new ArrayList<>();

    // processCanineListActingAsConsumerWithoutPecs(listA);
    processCanineListActingAsConsumerWithoutPecs(listB);
    // processCanineListActingAsConsumerWithoutPecs(listC);
  }

  @Test
  public void test_list_acting_as_consumer_with_pecs() {
    List<Animal> listA = new ArrayList<>();
    List<Canine> listB = new ArrayList<>();
    List<Dog> listC = new ArrayList<>();

    processCanineListActingAsConsumerWithPecs(listA);
    processCanineListActingAsConsumerWithPecs(listB);
    // processCanineListActingAsConsumerWithPecs(listC);
  }

  private void processCanineListActingAsConsumerWithoutPecs(List<Canine> list) {
    list.add(new Canine());
  }

  private void processCanineListActingAsConsumerWithPecs(List<? super Canine> list) {
    list.add(new Canine());
  }

  @Test
  public void test_list_acting_as_producer_without_pecs() {
    List<Animal> listA = Arrays.asList(new Animal(), new Canine(), new Dog());
    List<Canine> listB = Arrays.asList(new Canine(), new Dog());
    List<Dog> listC = Arrays.asList(new Dog());

    // processCanineListActingAsProducerWithoutPecs(listA);
    processCanineListActingAsProducerWithoutPecs(listB);
    // processCanineListActingAsProducerWithoutPecs(listC);
  }

  @Test
  public void test_list_acting_as_producer_with_pecs() {
    List<Animal> listA = Arrays.asList(new Animal(), new Canine(), new Dog());
    List<Canine> listB = Arrays.asList(new Canine(), new Dog());
    List<Dog> listC = Arrays.asList(new Dog());

    // processCanineListActingAsProducerWithPecs(listA);
    processCanineListActingAsProducerWithPecs(listB);
    processCanineListActingAsProducerWithPecs(listC);
  }


  private void processCanineListActingAsProducerWithoutPecs(List<Canine> list) {
    for (int i = 0; i < list.size(); i++) {
      Canine b = list.get(0);
      System.out.println("Consuming: " + b);
    }
  }

  private void processCanineListActingAsProducerWithPecs(List<? extends Canine> list) {
    for (int i = 0; i < list.size(); i++) {
      Canine b = list.get(0);
      System.out.println("Consuming: " + b);
    }
  }

  @Test
  public void test_list_acting_as_producer_and_consumer() {
    List<Animal> listA = Stream.of(new Animal(), new Canine(), new Dog()).collect(toList());
    List<Canine> listB = Stream.of(new Canine(), new Dog()).collect(toList());
    List<Dog> listC = Stream.of(new Dog()).collect(toList());

    // processCanineListActingAsProducerAndConsumer(listA);
    processCanineListActingAsProducerAndConsumer(listB);
    // processCanineListActingAsProducerAndConsumer(listC);
  }

  private void processCanineListActingAsProducerAndConsumer(List<Canine> list) {
    for (int i = 0; i < list.size(); i++) {
      Canine b = list.get(i);
      System.out.println("Consuming: " + b);
    }
    list.add(new Canine());
  }

  // THE FOLLOWING METHOD IS INCORRECTLY DEFINED!
  private void processCanineListActingAsProducerAndConsumerWithExtends(
      List<? extends Canine> list) {
    for (int i = 0; i < list.size(); i++) {
      Canine b = list.get(0);
      System.out.println("Consuming: " + b);
    }
    // The following line does not compile because we are using extends on a consumer!
    // list.add(new Canine());
  }

  // THE FOLLOWING METHOD IS INCORRECTLY DEFINED!
  private void processCanineListActingAsProducerAndConsumerWithSuper(List<? super Canine> list) {
    for (int i = 0; i < list.size(); i++) {
      // The following line does not compile because we are using super on a producer!
      // Canine b = list.get(0);
      // System.out.println("Consuming: " + b);
    }

    list.add(new Canine());
  }

  @Test
  public void test_generic_type_def_without_pecs() {
    List<Canine> list = Arrays.asList(new Canine(2), new Dog(1), new Wolf(3));
    // The following line does not compile because Canine implements Comparable<Animal> and the
    // method returns an object that extends Comparable<Canine>
    // Canine min = processGenericTypeDefWithoutPecs(list);
  }

  @Test
  public void test_generic_type_def_with_pecs() {
    List<Canine> list = Arrays.asList(new Canine(2), new Dog(1), new Wolf(3));
    Canine min = processGenericTypeDefWithPecs(list);

    assertThat(min)
        .isNotNull()
        .isInstanceOf(Dog.class);
    assertThat(min.getId()).isEqualTo(1);
  }

  private <T extends Comparable<T>> T processGenericTypeDefWithoutPecs(List<? extends T> list) {
    requireNonNull(list);
    // Find the minimum element in a list of Comparable objects
    return list.stream()
        .sorted()
        .findFirst()
        .orElse(null);
  }

  private <T extends Comparable<? super T>> T processGenericTypeDefWithPecs(
      List<? extends T> list) {
    // Find the minimum element in a list of Comparable objects
    requireNonNull(list);
    return list.stream()
        .sorted()
        .findFirst()
        .orElse(null);
  }

  public void putting_it_all_together() {
    List<Canine> canines =
        new ArrayList<>(Arrays.asList(new Dog(4), new Wolf(3), new Dog(8), new Wolf(1)));

    // Comparator<Canine> comparator1 = comparing_without_pecs((Animal a) -> a.getId());
    Comparator<Canine> comparator2 = comparing((Animal a) -> a.getId());

    Collections.sort(canines, comparator2);

    assertThat(canines.get(0)).isInstanceOf(Wolf.class);
    assertThat(canines.get(0).getId()).isEqualTo(1);
    assertThat(canines.get(1)).isInstanceOf(Wolf.class);
    assertThat(canines.get(1).getId()).isEqualTo(3);
    assertThat(canines.get(2)).isInstanceOf(Dog.class);
    assertThat(canines.get(2).getId()).isEqualTo(4);
    assertThat(canines.get(3)).isInstanceOf(Dog.class);
    assertThat(canines.get(3).getId()).isEqualTo(8);

  }

  private <T, U extends Comparable<U>> Comparator<T> comparing_without_pecs(Function<T, U> func) {
    return (t1, t2) -> func.apply(t1).compareTo(func.apply(t2));
  }

  private <T, U extends Comparable<? super U>> Comparator<T> comparing(
      Function<? super T, ? extends U> func) {
    return (t1, t2) -> func.apply(t1).compareTo(func.apply(t2));
  }

}
