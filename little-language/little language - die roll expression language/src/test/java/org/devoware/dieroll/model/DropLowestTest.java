package org.devoware.dieroll.model;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

public class DropLowestTest {
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
    DropLowest selector = new DropLowest(dice, 1);
    assertThat(selector.value(), equalTo(17));
    
    selector = new DropLowest(dice, 2);
    assertThat(selector.value(), equalTo(12));
  }

  @Test(expected=IllegalArgumentException.class)
  public void test_num_dice_less_than_total_dice() {
    new DropLowest(dice, 3);
  }

  @Test(expected=IllegalArgumentException.class)
  public void test_num_dice_greater_than_zero() {
    new DropLowest(dice, 0);
  }
  
  @Test(expected=NullPointerException.class)
  public void test_dice_not_null() {
    new DropLowest(null, 1);
  }
  
}
