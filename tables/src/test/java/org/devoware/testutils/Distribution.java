package org.devoware.testutils;

import static java.util.Objects.requireNonNull;
import static org.testcontainers.shaded.com.google.common.base.Preconditions.checkArgument;

import java.util.Map;
import java.util.Set;

import org.testcontainers.shaded.com.google.common.collect.Maps;

import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.function.Supplier;
import com.google.common.collect.ImmutableMap;

public class Distribution {

  private final Map<String, Integer> distribution;
  private final int numRolls = 1_000_000;

  public Distribution(Supplier<String> resultGenerator) {
    requireNonNull(resultGenerator, "resultGenerator cannot be null");
    Map<String, Integer> temp = Maps.newTreeMap();
    for (int i = 0; i < numRolls; i++) {
      String result = resultGenerator.get();
      int frequency = temp.computeIfAbsent(result, r -> 0) + 1;
      temp.put(result, frequency);
    }
    this.distribution = ImmutableMap.copyOf(temp);

  }

  public Set<String> getResultSet() {
    return distribution.keySet();
  }

  public boolean contains(String result) {
    requireNonNull(result, "result cannot be null");
    return distribution.containsKey(result);
  }

  public int getFrequency(String result) {
    requireNonNull(result, "result cannot be null");
    checkArgument(distribution.containsKey(result),
        "the specified result '" + result + "' is not present in this distribution");
    return distribution.get(result);
  }

  public double getPercentage(String result) {
    requireNonNull(result, "result cannot be null");
    checkArgument(distribution.containsKey(result),
        "the specified result '" + result + "' is not present in this distribution");
    return ((double) distribution.get(result)) / numRolls * 100;
  }

}
