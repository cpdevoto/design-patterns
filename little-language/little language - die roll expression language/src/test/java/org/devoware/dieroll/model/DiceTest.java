package org.devoware.dieroll.model;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

public class DiceTest {
  @Mock
  private RandomValueGenerator die;
  
  @Before
  public void setup () {
    initMocks(this);
    when(die.value()).thenReturn(2, 6, 5, 1);
  }

  @Test
  public void test_dice_value () {
    Dice dice = new Dice(4, die);
    
    assertThat(dice.value(), equalTo(14));
  }
  
  @Test
  public void test_dice_raw_values () {
    Dice dice = new Dice(4, die);
    
    int [] rawValues = dice.rawValues();
    assertNotNull(rawValues);
    assertThat(rawValues.length, equalTo(4));
    assertThat(rawValues[0], equalTo(2));
    assertThat(rawValues[1], equalTo(6));
    assertThat(rawValues[2], equalTo(5));
    assertThat(rawValues[3], equalTo(1));
  }
}
