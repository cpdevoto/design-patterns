package org.devoware.tarokka;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class CardPositionTest {

  @Test
  public void test_extract_result() {
    String result =
        "A. I see a dead man of noble birth, guarded by his widow. Return life to the dead man’s corpse, and he will be your staunch ally.\n"
            + "B. A man of death named Arrigal will forsake his dark lord to serve your cause. Beware! He has a rotten soul.";

    String optionA = CardPosition.extractResult(result, 0);

    assertThat(optionA)
        .isEqualTo(
            "I see a dead man of noble birth, guarded by his widow. Return life to the dead man’s corpse, and he will be your staunch ally.");

    String optionB = CardPosition.extractResult(result, 1);

    assertThat(optionB)
        .isEqualTo(
            "A man of death named Arrigal will forsake his dark lord to serve your cause. Beware! He has a rotten soul.");

  }


}
