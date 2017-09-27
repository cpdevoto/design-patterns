package com.resolutebi.baseline.expr;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.resolutebi.baseline.expr.AdditionOperator;
import com.resolutebi.baseline.expr.AndOperator;
import com.resolutebi.baseline.expr.BooleanLiteral;
import com.resolutebi.baseline.expr.DivisionOperator;
import com.resolutebi.baseline.expr.EqualsOperator;
import com.resolutebi.baseline.expr.Expression;
import com.resolutebi.baseline.expr.GreaterThanOperator;
import com.resolutebi.baseline.expr.GreaterThanOrEqualsOperator;
import com.resolutebi.baseline.expr.IfExpression;
import com.resolutebi.baseline.expr.Inputs;
import com.resolutebi.baseline.expr.LessThanOperator;
import com.resolutebi.baseline.expr.LessThanOrEqualsOperator;
import com.resolutebi.baseline.expr.MultiplicationOperator;
import com.resolutebi.baseline.expr.NotEqualsOperator;
import com.resolutebi.baseline.expr.NotOperator;
import com.resolutebi.baseline.expr.NumericLiteral;
import com.resolutebi.baseline.expr.OrOperator;
import com.resolutebi.baseline.expr.SubtractionOperator;
import com.resolutebi.baseline.expr.Variable;
import com.resolutebi.baseline.expr.VariableId;

public class ModelTest {

  @Test
  public void test_if() {
    Expression<Double> expr = IfExpression.create(
        Variable.create(VariableId.WEEK_DAY), 
        MultiplicationOperator.create(Variable.create(VariableId.AVG_DAILY_TEMP), NumericLiteral.create(5.0)), 
        MultiplicationOperator.create(Variable.create(VariableId.AVG_DAILY_TEMP), NumericLiteral.create(10.0)));
    
    Inputs inputs = Inputs.builder()
        .withInput(VariableId.WEEK_DAY, true)
        .withInput(VariableId.AVG_DAILY_TEMP, 5.0)
        .build();
    
    assertThat(expr.value(inputs), equalTo(25.0));

    inputs = Inputs.builder()
        .withInput(VariableId.WEEK_DAY, false)
        .withInput(VariableId.AVG_DAILY_TEMP, 5.0)
        .build();
    
    assertThat(expr.value(inputs), equalTo(50.0));
  
  }

  @Test
  public void test_if_else_if() {
    Expression<Double> expr = IfExpression.create(
        GreaterThanOperator.create(Variable.create(VariableId.AVG_DAILY_TEMP), NumericLiteral.create(20.0)), 
        MultiplicationOperator.create(Variable.create(VariableId.AVG_DAILY_TEMP), NumericLiteral.create(5.0)), 
        IfExpression.create(
            GreaterThanOperator.create(Variable.create(VariableId.AVG_DAILY_TEMP), NumericLiteral.create(10.0)), 
            MultiplicationOperator.create(Variable.create(VariableId.AVG_DAILY_TEMP), NumericLiteral.create(10.0)), 
            MultiplicationOperator.create(Variable.create(VariableId.AVG_DAILY_TEMP), NumericLiteral.create(20.0))));
    
    Inputs inputs = Inputs.builder()
        .withInput(VariableId.WEEK_DAY, true)
        .withInput(VariableId.AVG_DAILY_TEMP, 25.0)
        .build();
    
    assertThat(expr.value(inputs), equalTo(125.0));

    inputs = Inputs.builder()
        .withInput(VariableId.WEEK_DAY, false)
        .withInput(VariableId.AVG_DAILY_TEMP, 20.0)
        .build();
    
    assertThat(expr.value(inputs), equalTo(200.0));
  
    inputs = Inputs.builder()
        .withInput(VariableId.WEEK_DAY, false)
        .withInput(VariableId.AVG_DAILY_TEMP, 10.0)
        .build();
    
    assertThat(expr.value(inputs), equalTo(200.0));
  }
  
  @Test
  public void test_equals() {
    Expression<Boolean> expr = EqualsOperator.create(Variable.create(VariableId.WEEK_DAY), BooleanLiteral.TRUE);
  
    Inputs inputs = Inputs.builder()
        .withInput(VariableId.WEEK_DAY, true)
        .withInput(VariableId.AVG_DAILY_TEMP, 25.0)
        .build();
    
    assertThat(expr.value(inputs), equalTo(true));

    inputs = Inputs.builder()
        .withInput(VariableId.WEEK_DAY, false)
        .withInput(VariableId.AVG_DAILY_TEMP, 20.0)
        .build();

    assertThat(expr.value(inputs), equalTo(false));
    
    expr = EqualsOperator.create(Variable.create(VariableId.AVG_DAILY_TEMP), NumericLiteral.create(25.0));

    inputs = Inputs.builder()
        .withInput(VariableId.WEEK_DAY, true)
        .withInput(VariableId.AVG_DAILY_TEMP, 25.0)
        .build();
    
    assertThat(expr.value(inputs), equalTo(true));

    inputs = Inputs.builder()
        .withInput(VariableId.WEEK_DAY, false)
        .withInput(VariableId.AVG_DAILY_TEMP, 20.0)
        .build();

    assertThat(expr.value(inputs), equalTo(false));
  
  }
  
  @Test
  public void test_not_equals() {
    Expression<Boolean> expr = NotEqualsOperator.create(Variable.create(VariableId.WEEK_DAY), BooleanLiteral.TRUE);
    
    Inputs inputs = Inputs.builder()
        .withInput(VariableId.WEEK_DAY, true)
        .withInput(VariableId.AVG_DAILY_TEMP, 25.0)
        .build();
    
    assertThat(expr.value(inputs), equalTo(false));

    inputs = Inputs.builder()
        .withInput(VariableId.WEEK_DAY, false)
        .withInput(VariableId.AVG_DAILY_TEMP, 20.0)
        .build();

    assertThat(expr.value(inputs), equalTo(true));
    
    expr = NotEqualsOperator.create(Variable.create(VariableId.AVG_DAILY_TEMP), NumericLiteral.create(25.0));

    inputs = Inputs.builder()
        .withInput(VariableId.WEEK_DAY, true)
        .withInput(VariableId.AVG_DAILY_TEMP, 25.0)
        .build();
    
    assertThat(expr.value(inputs), equalTo(false));

    inputs = Inputs.builder()
        .withInput(VariableId.WEEK_DAY, false)
        .withInput(VariableId.AVG_DAILY_TEMP, 20.0)
        .build();

    assertThat(expr.value(inputs), equalTo(true));
  }
  
  @Test
  public void test_greater_than() {
    Expression<Boolean> expr = GreaterThanOperator.create(Variable.create(VariableId.AVG_DAILY_TEMP), NumericLiteral.create(25.0));

    Inputs inputs = Inputs.builder()
        .withInput(VariableId.WEEK_DAY, true)
        .withInput(VariableId.AVG_DAILY_TEMP, 30.0)
        .build();
    
    assertThat(expr.value(inputs), equalTo(true));

    inputs = Inputs.builder()
        .withInput(VariableId.WEEK_DAY, true)
        .withInput(VariableId.AVG_DAILY_TEMP, 25.0)
        .build();
    
    assertThat(expr.value(inputs), equalTo(false));

  }
  
  @Test
  public void test_greater_than_or_equal() {
    Expression<Boolean> expr = GreaterThanOrEqualsOperator.create(Variable.create(VariableId.AVG_DAILY_TEMP), NumericLiteral.create(25.0));

    Inputs inputs = Inputs.builder()
        .withInput(VariableId.WEEK_DAY, true)
        .withInput(VariableId.AVG_DAILY_TEMP, 30.0)
        .build();
    
    assertThat(expr.value(inputs), equalTo(true));

    inputs = Inputs.builder()
        .withInput(VariableId.WEEK_DAY, true)
        .withInput(VariableId.AVG_DAILY_TEMP, 25.0)
        .build();
    
    assertThat(expr.value(inputs), equalTo(true));

    inputs = Inputs.builder()
        .withInput(VariableId.WEEK_DAY, true)
        .withInput(VariableId.AVG_DAILY_TEMP, 24.0)
        .build();
    
    assertThat(expr.value(inputs), equalTo(false));
  }
  
  @Test
  public void test_less_than() {
    Expression<Boolean> expr = LessThanOperator.create(Variable.create(VariableId.AVG_DAILY_TEMP), NumericLiteral.create(25.0));

    Inputs inputs = Inputs.builder()
        .withInput(VariableId.WEEK_DAY, true)
        .withInput(VariableId.AVG_DAILY_TEMP, 25.0)
        .build();
    
    assertThat(expr.value(inputs), equalTo(false));

    inputs = Inputs.builder()
        .withInput(VariableId.WEEK_DAY, true)
        .withInput(VariableId.AVG_DAILY_TEMP, 24.0)
        .build();
    
    assertThat(expr.value(inputs), equalTo(true));

  }
  
  @Test
  public void test_less_than_or_equal() {
    Expression<Boolean> expr = LessThanOrEqualsOperator.create(Variable.create(VariableId.AVG_DAILY_TEMP), NumericLiteral.create(25.0));

    Inputs inputs = Inputs.builder()
        .withInput(VariableId.WEEK_DAY, true)
        .withInput(VariableId.AVG_DAILY_TEMP, 30.0)
        .build();
    
    assertThat(expr.value(inputs), equalTo(false));

    inputs = Inputs.builder()
        .withInput(VariableId.WEEK_DAY, true)
        .withInput(VariableId.AVG_DAILY_TEMP, 25.0)
        .build();
    
    assertThat(expr.value(inputs), equalTo(true));

    inputs = Inputs.builder()
        .withInput(VariableId.WEEK_DAY, true)
        .withInput(VariableId.AVG_DAILY_TEMP, 24.0)
        .build();
    
    assertThat(expr.value(inputs), equalTo(true));
  }
  
  @Test
  public void test_and() {
    Expression<Boolean> expr = AndOperator.create(
        EqualsOperator.create(Variable.create(VariableId.AVG_DAILY_TEMP), NumericLiteral.create(25.0)),
        Variable.create(VariableId.WEEK_DAY));

    Inputs inputs = Inputs.builder()
        .withInput(VariableId.WEEK_DAY, true)
        .withInput(VariableId.AVG_DAILY_TEMP, 25.0)
        .build();
    
    assertThat(expr.value(inputs), equalTo(true));

    inputs = Inputs.builder()
        .withInput(VariableId.WEEK_DAY, false)
        .withInput(VariableId.AVG_DAILY_TEMP, 25.0)
        .build();
    
    assertThat(expr.value(inputs), equalTo(false));

    inputs = Inputs.builder()
        .withInput(VariableId.WEEK_DAY, true)
        .withInput(VariableId.AVG_DAILY_TEMP, 24.0)
        .build();
    
    assertThat(expr.value(inputs), equalTo(false));

    inputs = Inputs.builder()
        .withInput(VariableId.WEEK_DAY, false)
        .withInput(VariableId.AVG_DAILY_TEMP, 24.0)
        .build();
    
    assertThat(expr.value(inputs), equalTo(false));
  }

  @Test
  public void test_or() {
    Expression<Boolean> expr = OrOperator.create(
        EqualsOperator.create(Variable.create(VariableId.AVG_DAILY_TEMP), NumericLiteral.create(25.0)),
        Variable.create(VariableId.WEEK_DAY));

    Inputs inputs = Inputs.builder()
        .withInput(VariableId.WEEK_DAY, true)
        .withInput(VariableId.AVG_DAILY_TEMP, 25.0)
        .build();
    
    assertThat(expr.value(inputs), equalTo(true));

    inputs = Inputs.builder()
        .withInput(VariableId.WEEK_DAY, false)
        .withInput(VariableId.AVG_DAILY_TEMP, 25.0)
        .build();
    
    assertThat(expr.value(inputs), equalTo(true));

    inputs = Inputs.builder()
        .withInput(VariableId.WEEK_DAY, true)
        .withInput(VariableId.AVG_DAILY_TEMP, 24.0)
        .build();
    
    assertThat(expr.value(inputs), equalTo(true));

    inputs = Inputs.builder()
        .withInput(VariableId.WEEK_DAY, false)
        .withInput(VariableId.AVG_DAILY_TEMP, 24.0)
        .build();
    
    assertThat(expr.value(inputs), equalTo(false));
  }
  
  @Test
  public void test_not() {
    Expression<Boolean> expr = NotOperator.create(
        Variable.create(VariableId.WEEK_DAY));

    Inputs inputs = Inputs.builder()
        .withInput(VariableId.WEEK_DAY, true)
        .withInput(VariableId.AVG_DAILY_TEMP, 25.0)
        .build();
    
    assertThat(expr.value(inputs), equalTo(false));

    inputs = Inputs.builder()
        .withInput(VariableId.WEEK_DAY, false)
        .withInput(VariableId.AVG_DAILY_TEMP, 25.0)
        .build();
    
    assertThat(expr.value(inputs), equalTo(true));
  }
 
  @Test
  public void test_addition() {
    Expression<Double> expr = AdditionOperator.create(Variable.create(VariableId.AVG_DAILY_TEMP), NumericLiteral.create(25.0));

    Inputs inputs = Inputs.builder()
        .withInput(VariableId.WEEK_DAY, true)
        .withInput(VariableId.AVG_DAILY_TEMP, 25.0)
        .build();
    
    assertThat(expr.value(inputs), equalTo(50.0));

  }
  
  @Test
  public void test_subtraction() {
    Expression<Double> expr = SubtractionOperator.create(Variable.create(VariableId.AVG_DAILY_TEMP), NumericLiteral.create(25.0));

    Inputs inputs = Inputs.builder()
        .withInput(VariableId.WEEK_DAY, true)
        .withInput(VariableId.AVG_DAILY_TEMP, 25.0)
        .build();
    
    assertThat(expr.value(inputs), equalTo(0.0));

  }
  
  @Test
  public void test_multiplication() {
    Expression<Double> expr = MultiplicationOperator.create(Variable.create(VariableId.AVG_DAILY_TEMP), NumericLiteral.create(5.0));

    Inputs inputs = Inputs.builder()
        .withInput(VariableId.WEEK_DAY, true)
        .withInput(VariableId.AVG_DAILY_TEMP, 25.0)
        .build();
    
    assertThat(expr.value(inputs), equalTo(125.0));

  }

  @Test
  public void test_division() {
    Expression<Double> expr = DivisionOperator.create(Variable.create(VariableId.AVG_DAILY_TEMP), NumericLiteral.create(5.0));

    Inputs inputs = Inputs.builder()
        .withInput(VariableId.WEEK_DAY, true)
        .withInput(VariableId.AVG_DAILY_TEMP, 25.0)
        .build();
    
    assertThat(expr.value(inputs), equalTo(5.0));

  }

  @Test
  public void test_variable() {
    Expression<Double> expr1 = Variable.create(VariableId.AVG_DAILY_TEMP);
    Expression<Boolean> expr2 = Variable.create(VariableId.WEEK_DAY);

    Inputs inputs = Inputs.builder()
        .withInput(VariableId.WEEK_DAY, true)
        .withInput(VariableId.AVG_DAILY_TEMP, 25.0)
        .build();
    
    assertThat(expr1.value(inputs), equalTo(25.0));
    assertThat(expr2.value(inputs), equalTo(true));

  }

  @Test
  public void test_numeric_literal() {
    Expression<Double> expr = NumericLiteral.create(10.0);

    Inputs inputs = Inputs.builder()
        .withInput(VariableId.WEEK_DAY, true)
        .withInput(VariableId.AVG_DAILY_TEMP, 25.0)
        .build();
    
    assertThat(expr.value(inputs), equalTo(10.0));

  }

  @Test
  public void test_boolean_literal() {
    Expression<Boolean> expr = BooleanLiteral.TRUE;

    Inputs inputs = Inputs.builder()
        .withInput(VariableId.WEEK_DAY, true)
        .withInput(VariableId.AVG_DAILY_TEMP, 25.0)
        .build();
    
    assertThat(expr.value(inputs), equalTo(true));
    
    expr = BooleanLiteral.FALSE;
    
    assertThat(expr.value(inputs), equalTo(false));
    

  }
}
