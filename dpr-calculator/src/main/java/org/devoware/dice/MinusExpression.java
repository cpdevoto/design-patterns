package org.devoware.dice;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.stream.Stream;

class MinusExpression implements DieRollExpression {
  DieRollExpression left;
  DieRollExpression right;

  MinusExpression(DieRollExpression left, DieRollExpression right) {
    this.left = requireNonNull(left, "left cannot be null");
    this.right = requireNonNull(right, "right cannot be null");
  }

  @Override
  public double dpr() {
    return left.dpr() - right.dpr();
  }

  @Override
  public double roll() {
    return left.roll() - right.roll();
  }

  @Override
  public List<Dice> getDice() {
    return Stream.of(left, right)
        .flatMap(expr -> expr.getDice().stream())
        .collect(toList());
  }

  @Override
  public String toString() {
    return left + " - " + right;
  }

}
