package org.dicegolem.model;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

public class RerollOnceModifier implements DieRollModifier {

  private int threshold;

  public RerollOnceModifier(int threshold) {
    checkArgument(threshold > 0, "threshold must be greater than zero");
    this.threshold = threshold;
  }

  @Override
  public int modify(Die die, int roll) {
    requireNonNull(die, "die cannot be null");
    int result = roll;
    if (result <= threshold) {
      result = die.roll();
    }
    return result;
  }

  public int getThreshold() {
    return threshold;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + threshold;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    RerollOnceModifier other = (RerollOnceModifier) obj;
    if (threshold != other.threshold)
      return false;
    return true;
  }


}
