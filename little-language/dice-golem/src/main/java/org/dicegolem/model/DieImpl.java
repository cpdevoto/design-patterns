package org.dicegolem.model;

import java.util.Map;
import java.util.Random;

import com.google.common.collect.Maps;

class DieImpl implements Die {

  static final Map<Integer, Die> DIE_CACHE = Maps.newConcurrentMap();

  private final int type;
  private final Random rand = new Random();

  DieImpl(int type) {
    this.type = type;
  }

  public int getType() {
    return type;
  }

  @Override
  public int roll() {
    return rand.nextInt(type) + 1;
  }

  @Override
  public double average() {
    return ((double) (1 + type)) / 2;
  }

  @Override
  public double averageDiceOnly() {
    return average();
  }

  public String toString() {
    return "D" + type;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + type;
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
    DieImpl other = (DieImpl) obj;
    if (type != other.type)
      return false;
    return true;
  }


}
