package com.resolute.utils.simple;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

public class StateUtilsTest {

  @Test
  public void getStateAbbreviation() {

    // STEP 1: ARRANGE
    String stateName = " Michigan ";
    String expectedStateAbbreviation = "MI";


    // STEP 2: ACT
    String stateAbbreviation = StateUtils.getStateAbbreviation(stateName);


    // STEP 3: ASSERT
    assertThat(stateAbbreviation).isEqualTo(expectedStateAbbreviation);
  }

  @Test
  public void getStateAbbreviation_nullArgument() {

    // STEP 1: ARRANGE
    String stateName = null;


    // STEP 2: ACT AND ASSERT
    assertThatThrownBy(() -> {
      StateUtils.getStateAbbreviation(stateName);
    }).isInstanceOf(IllegalArgumentException.class);

  }
}
