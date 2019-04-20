package com.resolute.utils.simple;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Collector.Characteristics;
import java.util.stream.Stream;

public class MoreCollectors {

  /**
   * Adapts a {@code Collector} to one accepting elements of the same type {@code T} by applying the
   * predicate to each input element and only accumulating if the predicate returns {@code true}.
   *
   * @apiNote The {@code filtering()} collectors are most useful when used in a multi-level
   *          reduction, such as downstream of a {@code groupingBy} or {@code partitioningBy}. For
   *          example, given a stream of {@code Employee}, to accumulate the employees in each
   *          department that have a salary above a certain threshold:
   * 
   *          <pre>
   * {@code
   *     Map<Department, Set<Employee>> wellPaidEmployeesByDepartment
   *         = employees.stream().collect(groupingBy(Employee::getDepartment,
   *                                              filtering(e -> e.getSalary() > 2000, toSet())));
   * }
   *          </pre>
   * 
   *          A filtering collector differs from a stream's {@code filter()} operation. In this
   *          example, suppose there are no employees whose salary is above the threshold in some
   *          department. Using a filtering collector as shown above would result in a mapping from
   *          that department to an empty {@code Set}. If a stream {@code filter()} operation were
   *          done instead, there would be no mapping for that department at all.
   *
   * @param <T> the type of the input elements
   * @param <A> intermediate accumulation type of the downstream collector
   * @param <R> result type of collector
   * @param predicate a predicate to be applied to the input elements
   * @param downstream a collector which will accept values that match the predicate
   * @return a collector which applies the predicate to the input elements and provides matching
   *         elements to the downstream collector
   */
  // Adapted from Java 9!
  public static <T, A, R> Collector<T, ?, R> filtering(Predicate<? super T> predicate,
      Collector<? super T, A, R> downstream) {
    BiConsumer<A, ? super T> downstreamAccumulator = downstream.accumulator();

    Characteristics[] characteristics = downstreamCharacteristics(downstream);

    return Collector.of(downstream.supplier(),
        (r, t) -> {
          if (predicate.test(t)) {
            downstreamAccumulator.accept(r, t);
          }
        },
        downstream.combiner(), downstream.finisher(),
        characteristics);
  }


  // Adapted from Java 9!
  /**
   * Adapts a {@code Collector} accepting elements of type {@code U} to one accepting elements of
   * type {@code T} by applying a flat mapping function to each input element before accumulation.
   * The flat mapping function maps an input element to a {@link Stream stream} covering zero or
   * more output elements that are then accumulated downstream. Each mapped stream is
   * {@link java.util.stream.BaseStream#close() closed} after its contents have been placed
   * downstream. (If a mapped stream is {@code null} an empty stream is used, instead.)
   *
   * @apiNote The {@code flatMapping()} collectors are most useful when used in a multi-level
   *          reduction, such as downstream of a {@code groupingBy} or {@code partitioningBy}. For
   *          example, given a stream of {@code Order}, to accumulate the set of line items for each
   *          customer:
   * 
   *          <pre>
   * {@code
   *     Map<String, Set<LineItem>> itemsByCustomerName
   *         = orders.stream().collect(groupingBy(Order::getCustomerName,
   *                                              flatMapping(order -> order.getLineItems().stream(), toSet())));
   * }
   *          </pre>
   *
   * @param <T> the type of the input elements
   * @param <U> type of elements accepted by downstream collector
   * @param <A> intermediate accumulation type of the downstream collector
   * @param <R> result type of collector
   * @param mapper a function to be applied to the input elements, which returns a stream of results
   * @param downstream a collector which will receive the elements of the stream returned by mapper
   * @return a collector which applies the mapping function to the input elements and provides the
   *         flat mapped results to the downstream collector
   */
  public static <T, U, A, R> Collector<T, ?, R> flatMapping(
      Function<? super T, ? extends Stream<? extends U>> mapper,
      Collector<? super U, A, R> downstream) {
    BiConsumer<A, ? super U> downstreamAccumulator = downstream.accumulator();
    return Collector.of(downstream.supplier(),
        (r, t) -> {
          try (Stream<? extends U> result = mapper.apply(t)) {
            if (result != null)
              result.sequential().forEach(u -> downstreamAccumulator.accept(r, u));
          }
        },
        downstream.combiner(), downstream.finisher(),
        downstreamCharacteristics(downstream));
  }
  
  /**
   * @apiNote The {@code sortedList()} collectors are most useful when used in a multi-level
   *          reduction, such as downstream of a {@code groupingBy} or {@code partitioningBy}. For
   *          example, given a stream of {@code Order}, to accumulate the set of line items for each
   *          customer:
   * 
   *          <pre>
   * {@code
   *     Map<String, List<LineItem>> itemsByCustomerName
   *         = orders.stream().collect(groupingBy(Order::getCustomerName,
   *                                              toSortedList(naturalOrder())));
   * }
   *          </pre>
   *
   */  
  public static <T> Collector<T, ?, List<T>> toSortedList(Comparator<? super T> c) {
    return Collectors.collectingAndThen(
        Collectors.toCollection(ArrayList::new), l -> {
          l.sort(c);
          return l;
        });
  }


  private static <T, A, R> Characteristics[] downstreamCharacteristics(
      Collector<? super T, A, R> downstream) {
    Characteristics[] characteristics = new Characteristics[downstream.characteristics().size()];
    characteristics = downstream.characteristics().toArray(characteristics);
    return characteristics;
  }

  private MoreCollectors() {}
}
