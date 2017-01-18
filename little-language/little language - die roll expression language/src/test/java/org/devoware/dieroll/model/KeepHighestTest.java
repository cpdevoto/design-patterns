package org.devoware.dieroll.model;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

public class KeepHighestTest {
  @Mock
  private Dice dice;
  
  @Before
  public void setup () {
    initMocks(this);    
    when(dice.rawValues()).thenReturn(new int [] {12, 3, 5});
    when(dice.value()).thenReturn(20);
    when(dice.getNumDice()).thenReturn(3);
  }
  

  @Test
  public void test_value () {
    KeepHighest selector = new KeepHighest(dice, 1);
    assertThat(selector.value(), equalTo(12));
    
    selector = new KeepHighest(dice, 2);
    assertThat(selector.value(), equalTo(17));
  }

  @Test(expected=IllegalArgumentException.class)
  public void test_num_dice_less_than_total_dice() {
    new KeepHighest(dice, 3);
  }

  @Test(expected=IllegalArgumentException.class)
  public void test_num_dice_greater_than_zero() {
    new KeepHighest(dice, 0);
  }
  
  @Test(expected=NullPointerException.class)
  public void test_dice_not_null() {
    new KeepHighest(null, 1);
  }
  
}
