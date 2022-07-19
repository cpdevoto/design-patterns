package org.devoware.dicegolem.dice;

import java.util.Random;

/**
 * An abstraction layer on top of the Random class to allow for the use of test doubles that return
 * specific numbers instead of randomly generated ones.
 * 
 * @author cdevoto
 *
 */
@FunctionalInterface
interface RandomNumberGenerator {

  public static RandomNumberGenerator DEFAULT = new RandomNumberGenerator() {
    private final Random rng = new Random();

    public int nextInt(int bound) {
      return rng.nextInt(bound) + 1;
    }
  };

  public int nextInt(int bound);

}
