package org.dicegolem.parser;

import static org.dicegolem.model.fixtures.Assertions.assertRollRange;
import static org.dicegolem.model.fixtures.Assertions.assertSyntaxError;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.dicegolem.model.CompositeDice;
import org.dicegolem.model.DiceRollExpression;
import org.dicegolem.model.Die;
import org.dicegolem.model.DieRollAggregator;
import org.dicegolem.model.DropLowestAggregator;
import org.dicegolem.model.KeepHighestAggregator;
import org.dicegolem.model.Minus;
import org.dicegolem.model.NumericLiteral;
import org.dicegolem.model.Plus;
import org.dicegolem.model.RerollOnceModifier;
import org.dicegolem.model.SumAggregator;
import org.dicegolem.model.UnaryMinus;
import org.junit.Test;

public class DiceTest {

  @Test
  public void test_roll() {
    // Testing Dice.roll("1d6 + 3")
    assertRollRange(() -> Dice.roll("1d6 + 3"), 4, 9);
  }

  @Test
  public void test_parse() {

    DiceRollExpression result1 = Dice.parse("(4d6kh3 - 3d6dl1ro<2) + (-5 + 5d8ro<2)");

    assertThat(result1, instanceOf(Plus.class));
    Plus plus1 = Plus.class.cast(result1);
    DiceRollExpression operand1 = plus1.getOperand1();
    DiceRollExpression operand2 = plus1.getOperand2();

    assertThat(operand1, instanceOf(Minus.class));
    assertThat(operand2, instanceOf(Plus.class));

    Minus minus1 = Minus.class.cast(operand1);
    DiceRollExpression operand3 = minus1.getOperand1();
    DiceRollExpression operand4 = minus1.getOperand2();

    assertThat(operand3, instanceOf(CompositeDice.class));
    assertThat(operand4, instanceOf(CompositeDice.class));

    Plus plus2 = Plus.class.cast(operand2);
    DiceRollExpression operand5 = plus2.getOperand1();
    DiceRollExpression operand6 = plus2.getOperand2();

    assertThat(operand5, instanceOf(UnaryMinus.class));
    assertThat(operand6, instanceOf(CompositeDice.class));

    UnaryMinus unaryMinus1 = UnaryMinus.class.cast(operand5);
    DiceRollExpression operand7 = unaryMinus1.getOperand();

    assertThat(operand7, instanceOf(NumericLiteral.class));

    CompositeDice dice1 = CompositeDice.class.cast(operand3);
    assertThat(dice1.getNumDice(), equalTo(4));
    assertThat(dice1.getDie(), equalTo(Die.D6));
    DieRollAggregator aggregator1 = dice1.getAggregator();
    assertThat(aggregator1, notNullValue());
    assertThat(aggregator1, instanceOf(KeepHighestAggregator.class));
    KeepHighestAggregator keepHighest1 = KeepHighestAggregator.class.cast(aggregator1);
    assertThat(keepHighest1.getNumRolls(), equalTo(3));
    assertThat(dice1.getModifier().isPresent(), equalTo(false));

    CompositeDice dice2 = CompositeDice.class.cast(operand4);
    assertThat(dice2.getNumDice(), equalTo(3));
    assertThat(dice2.getDie(), equalTo(Die.D6));
    DieRollAggregator aggregator2 = dice2.getAggregator();
    assertThat(aggregator2, notNullValue());
    assertThat(aggregator2, instanceOf(DropLowestAggregator.class));
    DropLowestAggregator dropLowest1 = DropLowestAggregator.class.cast(aggregator2);
    assertThat(dropLowest1.getNumRolls(), equalTo(1));
    assertThat(dice2.getModifier().isPresent(), equalTo(true));
    assertThat(dice2.getModifier().get(), instanceOf(RerollOnceModifier.class));
    RerollOnceModifier rerollOnce1 = RerollOnceModifier.class.cast(dice2.getModifier().get());
    assertThat(rerollOnce1.getThreshold(), equalTo(2));

    NumericLiteral modifier1 = NumericLiteral.class.cast(operand7);
    assertThat(modifier1.getValue(), equalTo(5));

    CompositeDice dice3 = CompositeDice.class.cast(operand6);
    assertThat(dice3.getNumDice(), equalTo(5));
    assertThat(dice3.getDie(), equalTo(Die.D8));
    DieRollAggregator aggregator3 = dice3.getAggregator();
    assertThat(aggregator3, notNullValue());
    assertThat(aggregator3, instanceOf(SumAggregator.class));
    assertThat(dice3.getModifier().isPresent(), equalTo(true));
    assertThat(dice2.getModifier().get(), instanceOf(RerollOnceModifier.class));
    RerollOnceModifier rerollOnce2 = RerollOnceModifier.class.cast(dice3.getModifier().get());
    assertThat(rerollOnce2.getThreshold(), equalTo(2));

  }

  @Test
  public void test_invalid_expressions() {

    assertSyntaxError("4d6 + d4", 1, 7, "found 'd' when expecting a number");
    assertSyntaxError("4d6 - + 1d4", 1, 7, "found '+' when expecting a number");
    assertSyntaxError("(4d6 + (1d4 + 2)", 1, 17, "found end of string when expecting ')'");
    assertSyntaxError("4d6kh2dl1", 1, 7, "unexpected second aggregator dl1");
    assertSyntaxError("4d6ro<2kh2ro<3", 1, 11, "unexpected second modifier ro<3");

  }


}
