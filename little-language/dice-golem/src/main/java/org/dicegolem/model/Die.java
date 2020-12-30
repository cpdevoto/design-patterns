package org.dicegolem.model;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.stream.Collectors.toCollection;

import java.util.LinkedHashSet;
import java.util.Set;

public interface Die extends DiceRollExpression {

  public static Die D3 = get(3);
  public static Die D4 = get(4);
  public static Die D6 = get(6);
  public static Die D8 = get(8);
  public static Die D10 = get(10);
  public static Die D12 = get(12);
  public static Die D20 = get(20);
  public static Die D100 = get(100);


  public static Set<Die> values() {
    return DieImpl.DIE_CACHE.values().stream()
        .sorted((d1, d2) -> {
          return d1.getType() - d2.getType();
        })
        .collect(toCollection(LinkedHashSet::new));
  }

  public static Die get(int type) {
    checkArgument(type > 0, "type must be greater than zero");
    return DieImpl.DIE_CACHE.computeIfAbsent(type, DieImpl::new);
  }

  public int getType();


}
