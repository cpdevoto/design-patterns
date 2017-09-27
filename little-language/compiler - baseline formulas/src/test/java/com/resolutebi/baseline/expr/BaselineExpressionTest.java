package com.resolutebi.baseline.expr;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;

public class BaselineExpressionTest {

  @Test
  public void test_parse_file_and_get_value() throws IOException {
    String expressionString = null;
    try (InputStream in = BaselineExpressionTest.class.getResourceAsStream("actual-baseline-expr.txt")) {
      expressionString = CharStreams.toString(new InputStreamReader(
          in, Charsets.UTF_8));
      
    }
    BaselineExpression exp = BaselineExpression.parse(expressionString);
    
    Inputs inputs = Inputs.builder()
        .withInput(VariableId.WEEK_DAY, true)
        .withInput(VariableId.AVG_DAILY_TEMP, 32.0)
        .build();
    
    assertThat(exp.value(inputs), equalTo(39789.6332));
    
    inputs = Inputs.builder()
        .withInput(VariableId.WEEK_DAY, true)
        .withInput(VariableId.AVG_DAILY_TEMP, 75.0)
        .build();
    
    assertThat(exp.value(inputs), equalTo(55457.0024));
    
    inputs = Inputs.builder()
        .withInput(VariableId.WEEK_DAY, false)
        .withInput(VariableId.AVG_DAILY_TEMP, 32.0)
        .build();

    assertThat(exp.value(inputs), equalTo(37543.0328));
    
    inputs = Inputs.builder()
        .withInput(VariableId.WEEK_DAY, false)
        .withInput(VariableId.AVG_DAILY_TEMP, 75.0)
        .build();

    assertThat(exp.value(inputs), equalTo(53256.6604));
  }
  
  @Test
  public void test_parse() {
      // Test number literal 
      Expression<Double> exp = BaselineExpression.parse("51.76").getExpr();
      assertNotNull(exp);
      assertThat(exp, instanceOf(NumericLiteral.class));

      // Test unary minus
      exp = BaselineExpression.parse("-51.76").getExpr();
      assertNotNull(exp);
      assertThat(exp, instanceOf(UnaryMinusOperator.class));
      assertThat(UnaryMinusOperator.class.cast(exp).getLiteral().getValue(), equalTo(51.76));
      
      // Test variable
      exp = BaselineExpression.parse("AVG_DAILY_TEMP").getExpr();
      assertNotNull(exp);
      assertThat(exp, instanceOf(Variable.class));
      assertThat(Variable.class.cast(exp).getId(), equalTo(VariableId.AVG_DAILY_TEMP));

      // Test if expression
      exp = BaselineExpression.parse("IF (WEEK_DAY) 50 ELSE 100").getExpr();
      assertNotNull(exp);
      assertThat(exp, instanceOf(IfExpression.class));
      assertThat(getCondition(exp), instanceOf(Variable.class));
      assertThat(Variable.class.cast(getCondition(exp)).getId(), equalTo(VariableId.WEEK_DAY));
      assertThat(getElseBody(exp), instanceOf(NumericLiteral.class));
      assertThat(getIfBody(exp), equalTo(NumericLiteral.create(50.0)));
      assertThat(getElseBody(exp), instanceOf(NumericLiteral.class));
      assertThat(getElseBody(exp), equalTo(NumericLiteral.create(100.0)));

      // Test boolean literals
      exp = BaselineExpression.parse("IF (TRUE) 50 ELSE 100").getExpr();
      assertThat(getCondition(exp), instanceOf(BooleanLiteral.class));
      BooleanLiteral bool = BooleanLiteral.class.cast(getCondition(exp));
      assertThat(bool, equalTo(BooleanLiteral.TRUE));
      
      exp = BaselineExpression.parse("IF (FALSE) 50 ELSE 100").getExpr();
      assertThat(getCondition(exp), instanceOf(BooleanLiteral.class));
      bool = BooleanLiteral.class.cast(getCondition(exp));
      assertThat(bool, equalTo(BooleanLiteral.FALSE));
      
      // Test not operator
      exp = BaselineExpression.parse("IF (!TRUE) 50 ELSE 100").getExpr();
      assertThat(getCondition(exp), instanceOf(NotOperator.class));
      NotOperator not = NotOperator.class.cast(getCondition(exp));
      assertThat(not.getExpr(), equalTo(BooleanLiteral.TRUE));

      // Test or operator
      exp = BaselineExpression.parse("IF (TRUE || FALSE) 50 ELSE 100").getExpr();
      assertThat(getCondition(exp), instanceOf(OrOperator.class));
      OrOperator or = OrOperator.class.cast(getCondition(exp));
      assertThat(or.getExpr1(), equalTo(BooleanLiteral.TRUE));
      assertThat(or.getExpr2(), equalTo(BooleanLiteral.FALSE));
      
      // Test and operator
      exp = BaselineExpression.parse("IF (TRUE && FALSE) 50 ELSE 100").getExpr();
      assertThat(getCondition(exp), instanceOf(AndOperator.class));
      AndOperator and = AndOperator.class.cast(getCondition(exp));
      assertThat(and.getExpr1(), equalTo(BooleanLiteral.TRUE));
      assertThat(and.getExpr2(), equalTo(BooleanLiteral.FALSE));

      // Test equals operator with booleans
      exp = BaselineExpression.parse("IF (TRUE == FALSE) 50 ELSE 100").getExpr();
      assertThat(getCondition(exp), instanceOf(EqualsOperator.class));
      EqualsOperator<?> equals = EqualsOperator.class.cast(getCondition(exp));
      assertThat(equals.getExpr1(), equalTo(BooleanLiteral.TRUE));
      assertThat(equals.getExpr2(), equalTo(BooleanLiteral.FALSE));
  
      // Test equals operator with numbers
      exp = BaselineExpression.parse("IF (10 == 5) 50 ELSE 100").getExpr();
      assertThat(getCondition(exp), instanceOf(EqualsOperator.class));
      equals = EqualsOperator.class.cast(getCondition(exp));
      assertThat(equals.getExpr1(), equalTo(NumericLiteral.create(10)));
      assertThat(equals.getExpr2(), equalTo(NumericLiteral.create(5)));

      // Test not equals operator
      exp = BaselineExpression.parse("IF (TRUE != FALSE) 50 ELSE 100").getExpr();
      assertThat(getCondition(exp), instanceOf(NotEqualsOperator.class));
      NotEqualsOperator<?> notEquals = NotEqualsOperator.class.cast(getCondition(exp));
      assertThat(notEquals.getExpr1(), equalTo(BooleanLiteral.TRUE));
      assertThat(notEquals.getExpr2(), equalTo(BooleanLiteral.FALSE));

      // Test equals operator with numbers
      exp = BaselineExpression.parse("IF (10 != 5) 50 ELSE 100").getExpr();
      assertThat(getCondition(exp), instanceOf(NotEqualsOperator.class));
      notEquals = NotEqualsOperator.class.cast(getCondition(exp));
      assertThat(notEquals.getExpr1(), equalTo(NumericLiteral.create(10)));
      assertThat(notEquals.getExpr2(), equalTo(NumericLiteral.create(5)));

      // Test addition operator
      exp = BaselineExpression.parse("IF (TRUE) 50 + 60 ELSE 100").getExpr();
      assertThat(getIfBody(exp), instanceOf(AdditionOperator.class));
      AdditionOperator plus = AdditionOperator.class.cast(getIfBody(exp));
      assertThat(plus.getExpr1(), equalTo(NumericLiteral.create(50.0)));
      assertThat(plus.getExpr2(), equalTo(NumericLiteral.create(60.0)));

      // Test subtraction operator
      exp = BaselineExpression.parse("IF (TRUE) 50 - 60 ELSE 100").getExpr();
      assertThat(getIfBody(exp), instanceOf(SubtractionOperator.class));
      SubtractionOperator minus = SubtractionOperator.class.cast(getIfBody(exp));
      assertThat(minus.getExpr1(), equalTo(NumericLiteral.create(50.0)));
      assertThat(minus.getExpr2(), equalTo(NumericLiteral.create(60.0)));

      // Test multiplication operator
      exp = BaselineExpression.parse("IF (TRUE) 50 * 60 ELSE 100").getExpr();
      assertThat(getIfBody(exp), instanceOf(MultiplicationOperator.class));
      MultiplicationOperator mult = MultiplicationOperator.class.cast(getIfBody(exp));
      assertThat(mult.getExpr1(), equalTo(NumericLiteral.create(50.0)));
      assertThat(mult.getExpr2(), equalTo(NumericLiteral.create(60.0)));

      // Test division operator
      exp = BaselineExpression.parse("IF (TRUE) 50 / 60 ELSE 100").getExpr();
      assertThat(getIfBody(exp), instanceOf(DivisionOperator.class));
      DivisionOperator div = DivisionOperator.class.cast(getIfBody(exp));
      assertThat(div.getExpr1(), equalTo(NumericLiteral.create(50.0)));
      assertThat(div.getExpr2(), equalTo(NumericLiteral.create(60.0)));
  }

  @Test
  public void test_parse_operator_precedence() {
    // Test "and" takes precedence over "or"
    Expression<Double> exp = BaselineExpression.parse("IF (TRUE && FALSE || TRUE) 50 ELSE 100").getExpr();
    assertThat(getCondition(exp), instanceOf(OrOperator.class));
    OrOperator or = OrOperator.class.cast(getCondition(exp));
    assertThat(or.getExpr1(), instanceOf(AndOperator.class));
    assertThat(or.getExpr2(), equalTo(BooleanLiteral.TRUE));
    AndOperator and = AndOperator.class.cast(or.getExpr1());
    assertThat(and.getExpr1(), equalTo(BooleanLiteral.TRUE));
    assertThat(and.getExpr2(), equalTo(BooleanLiteral.FALSE));

    // Test "and" precedence over "or" can be overridden with parentheses
    exp = BaselineExpression.parse("IF (TRUE && (FALSE || TRUE)) 50 ELSE 100").getExpr();
    assertThat(getCondition(exp), instanceOf(AndOperator.class));
    and = AndOperator.class.cast(getCondition(exp));
    assertThat(and.getExpr1(), equalTo(BooleanLiteral.TRUE));
    assertThat(and.getExpr2(), instanceOf(OrOperator.class));
    or = OrOperator.class.cast(and.getExpr2());
    assertThat(or.getExpr1(), equalTo(BooleanLiteral.FALSE));
    assertThat(or.getExpr2(), equalTo(BooleanLiteral.TRUE));

    // Test "not" precedence over "and" 
    exp = BaselineExpression.parse("IF (!FALSE && TRUE) 50 ELSE 100").getExpr();
    assertThat(getCondition(exp), instanceOf(AndOperator.class));
    and = AndOperator.class.cast(getCondition(exp));
    assertThat(and.getExpr1(), instanceOf(NotOperator.class));
    assertThat(and.getExpr2(), equalTo(BooleanLiteral.TRUE));
    NotOperator not = NotOperator.class.cast(and.getExpr1());
    assertThat(not.getExpr(), equalTo(BooleanLiteral.FALSE));

    // Test "not" precedence over "and" can be overridden with parentheses
    exp = BaselineExpression.parse("IF (!(FALSE && TRUE)) 50 ELSE 100").getExpr();
    assertThat(getCondition(exp), instanceOf(NotOperator.class));
    not = NotOperator.class.cast(getCondition(exp));
    assertThat(not.getExpr(), instanceOf(AndOperator.class));
    and = AndOperator.class.cast(not.getExpr());
    assertThat(and.getExpr1(), equalTo(BooleanLiteral.FALSE));
    assertThat(and.getExpr2(), equalTo(BooleanLiteral.TRUE));

    // Test "equals" precedence over "and"
    exp = BaselineExpression.parse("IF (TRUE == FALSE && TRUE) 50 ELSE 100").getExpr();
    assertThat(getCondition(exp), instanceOf(AndOperator.class));
    and = AndOperator.class.cast(getCondition(exp));
    assertThat(and.getExpr1(), instanceOf(EqualsOperator.class));
    assertThat(and.getExpr2(), equalTo(BooleanLiteral.TRUE));
    EqualsOperator<?> eq = EqualsOperator.class.cast(and.getExpr1());
    assertThat(eq.getExpr1(), equalTo(BooleanLiteral.TRUE));
    assertThat(eq.getExpr2(), equalTo(BooleanLiteral.FALSE));
  
    // Test "equals" precedence over "and" can be overridden with parentheses
    exp = BaselineExpression.parse("IF (TRUE == (FALSE && TRUE)) 50 ELSE 100").getExpr();
    assertThat(getCondition(exp), instanceOf(EqualsOperator.class));
    eq = EqualsOperator.class.cast(getCondition(exp));
    assertThat(eq.getExpr1(), equalTo(BooleanLiteral.TRUE));
    assertThat(eq.getExpr2(), instanceOf(AndOperator.class));
    and = AndOperator.class.cast(eq.getExpr2());
    assertThat(and.getExpr1(), equalTo(BooleanLiteral.FALSE));
    assertThat(and.getExpr2(), equalTo(BooleanLiteral.TRUE));

    // Test "greater than" precedence over "equals"
    exp = BaselineExpression.parse("IF (10 > 5 == TRUE) 50 ELSE 100").getExpr();
    assertThat(getCondition(exp), instanceOf(EqualsOperator.class));
    eq = EqualsOperator.class.cast(getCondition(exp));
    assertThat(eq.getExpr1(), instanceOf(GreaterThanOperator.class));
    assertThat(eq.getExpr2(), equalTo(BooleanLiteral.TRUE));
    GreaterThanOperator gt = GreaterThanOperator.class.cast(eq.getExpr1());
    assertThat(gt.getExpr1(), equalTo(NumericLiteral.create(10)));
    assertThat(gt.getExpr2(), equalTo(NumericLiteral.create(5)));
  
    // Test "addition" precedence over "greater than"
    exp = BaselineExpression.parse("IF (2 + 4 > 5) 50 ELSE 100").getExpr();
    assertThat(getCondition(exp), instanceOf(GreaterThanOperator.class));
    gt = GreaterThanOperator.class.cast(getCondition(exp));
    assertThat(gt.getExpr1(), instanceOf(AdditionOperator.class));
    assertThat(gt.getExpr2(), equalTo(NumericLiteral.create(5)));
    AdditionOperator ad = AdditionOperator.class.cast(gt.getExpr1());
    assertThat(ad.getExpr1(), equalTo(NumericLiteral.create(2)));
    assertThat(ad.getExpr2(), equalTo(NumericLiteral.create(4)));
    
    // Test "multiplication" precedence over "addition"
    exp = BaselineExpression.parse("IF (true) 10 * 20 + 30 ELSE 100").getExpr();
    assertThat(getIfBody(exp), instanceOf(AdditionOperator.class));
    ad = AdditionOperator.class.cast(getIfBody(exp));
    assertThat(ad.getExpr1(), instanceOf(MultiplicationOperator.class));
    assertThat(ad.getExpr2(), equalTo(NumericLiteral.create(30)));
    MultiplicationOperator mu = MultiplicationOperator.class.cast(ad.getExpr1());
    assertThat(mu.getExpr1(), equalTo(NumericLiteral.create(10)));
    assertThat(mu.getExpr2(), equalTo(NumericLiteral.create(20)));

    // Test "multiplication" precedence over "addition" can be overridden with parentheses
    exp = BaselineExpression.parse("IF (true) 10 * (20 + 30) ELSE 100").getExpr();
    assertThat(getIfBody(exp), instanceOf(MultiplicationOperator.class));
    mu = MultiplicationOperator.class.cast(getIfBody(exp));
    assertThat(mu.getExpr1(), equalTo(NumericLiteral.create(10)));
    assertThat(mu.getExpr2(), instanceOf(AdditionOperator.class));
    ad = AdditionOperator.class.cast(mu.getExpr2());
    assertThat(ad.getExpr1(), equalTo(NumericLiteral.create(20)));
    assertThat(ad.getExpr2(), equalTo(NumericLiteral.create(30)));

    // Test "unary minus" precedence over "multiplication"
    exp = BaselineExpression.parse("IF (true) -10 * 20 ELSE 100").getExpr();
    assertThat(getIfBody(exp), instanceOf(MultiplicationOperator.class));
    mu = MultiplicationOperator.class.cast(getIfBody(exp));
    assertThat(mu.getExpr1(), instanceOf(UnaryMinusOperator.class));
    assertThat(mu.getExpr2(), equalTo(NumericLiteral.create(20)));
    UnaryMinusOperator um = UnaryMinusOperator.class.cast(mu.getExpr1());
    assertThat(um.getLiteral(), equalTo(NumericLiteral.create(10)));
  
  }
  
  @Test
  public void test_parse_enforces_type_checks() {
    // Test that "or" requires two boolean expressions
    try {
      BaselineExpression.parse("IF (1.0 || TRUE) 50 ELSE 100").getExpr();
      fail("Expected a ParseException");
    } catch (ParseException ex) {
      assertThat("Invalid error message: " + ex.getMessage(), ex.getMessage().contains("line 1, character 9"), equalTo(true));
      assertThat("Invalid error message: " + ex.getMessage(), ex.getMessage().contains("unexpected '||' operator following a non-boolean expression"), equalTo(true));
    }
      
    try {
      BaselineExpression.parse("IF (TRUE || 1.0) 50 ELSE 100").getExpr();
      fail("Expected a ParseException");
    } catch (ParseException ex) {
      assertThat("Invalid error message: " + ex.getMessage(), ex.getMessage().contains("line 1, character 13"), equalTo(true));
      assertThat("Invalid error message: " + ex.getMessage(), ex.getMessage().contains("unexpected non-boolean expression"), equalTo(true));
    }

    // Test that "and" requires two boolean expressions
    try {
      BaselineExpression.parse("IF (1.0 && TRUE) 50 ELSE 100").getExpr();
      fail("Expected a ParseException");
    } catch (ParseException ex) {
      assertThat("Invalid error message: " + ex.getMessage(), ex.getMessage().contains("line 1, character 9"), equalTo(true));
      assertThat("Invalid error message: " + ex.getMessage(), ex.getMessage().contains("unexpected '&&' operator following a non-boolean expression"), equalTo(true));
    }
      
    try {
      BaselineExpression.parse("IF (TRUE && 1.0) 50 ELSE 100").getExpr();
      fail("Expected a ParseException");
    } catch (ParseException ex) {
      assertThat("Invalid error message: " + ex.getMessage(), ex.getMessage().contains("line 1, character 13"), equalTo(true));
      assertThat("Invalid error message: " + ex.getMessage(), ex.getMessage().contains("unexpected non-boolean expression"), equalTo(true));
    }
    
    // Test that "not" requires a boolean expression
    try {
      BaselineExpression.parse("IF (!1.0) 50 ELSE 100").getExpr();
      fail("Expected a ParseException");
    } catch (ParseException ex) {
      assertThat("Invalid error message: " + ex.getMessage(), ex.getMessage().contains("line 1, character 6"), equalTo(true));
      assertThat("Invalid error message: " + ex.getMessage(), ex.getMessage().contains("unexpected non-boolean expression"), equalTo(true));
    }
    
    // Test that "equals" requires two expressions of the same type
    try {
      BaselineExpression.parse("IF (1.0 == TRUE) 50 ELSE 100").getExpr();
      fail("Expected a ParseException");
    } catch (ParseException ex) {
      assertThat("Invalid error message: " + ex.getMessage(), ex.getMessage().contains("line 1, character 12"), equalTo(true));
      assertThat("Invalid error message: " + ex.getMessage(), ex.getMessage().contains("unexpected non-numeric expression"), equalTo(true));
    }
      
    try {
      BaselineExpression.parse("IF (TRUE == 1.0) 50 ELSE 100").getExpr();
      fail("Expected a ParseException");
    } catch (ParseException ex) {
      assertThat("Invalid error message: " + ex.getMessage(), ex.getMessage().contains("line 1, character 13"), equalTo(true));
      assertThat("Invalid error message: " + ex.getMessage(), ex.getMessage().contains("unexpected non-boolean expression"), equalTo(true));
    }
    
    // Test that "not equals" requires two expressions of the same type
    try {
      BaselineExpression.parse("IF (1.0 != TRUE) 50 ELSE 100").getExpr();
      fail("Expected a ParseException");
    } catch (ParseException ex) {
      assertThat("Invalid error message: " + ex.getMessage(), ex.getMessage().contains("line 1, character 12"), equalTo(true));
      assertThat("Invalid error message: " + ex.getMessage(), ex.getMessage().contains("unexpected non-numeric expression"), equalTo(true));
    }
      
    try {
      BaselineExpression.parse("IF (TRUE != 1.0) 50 ELSE 100").getExpr();
      fail("Expected a ParseException");
    } catch (ParseException ex) {
      assertThat("Invalid error message: " + ex.getMessage(), ex.getMessage().contains("line 1, character 13"), equalTo(true));
      assertThat("Invalid error message: " + ex.getMessage(), ex.getMessage().contains("unexpected non-boolean expression"), equalTo(true));
    }
    
    // Test that "greater than" requires two numeric expressions
    try {
      BaselineExpression.parse("IF (TRUE > 1.0) 50 ELSE 100").getExpr();
      fail("Expected a ParseException");
    } catch (ParseException ex) {
      assertThat("Invalid error message: " + ex.getMessage(), ex.getMessage().contains("line 1, character 10"), equalTo(true));
      assertThat("Invalid error message: " + ex.getMessage(), ex.getMessage().contains("unexpected '>' operator following a non-numeric expression"), equalTo(true));
    }
      
    try {
      BaselineExpression.parse("IF (1.0 > TRUE) 50 ELSE 100").getExpr();
      fail("Expected a ParseException");
    } catch (ParseException ex) {
      assertThat("Invalid error message: " + ex.getMessage(), ex.getMessage().contains("line 1, character 11"), equalTo(true));
      assertThat("Invalid error message: " + ex.getMessage(), ex.getMessage().contains("unexpected non-numeric expression"), equalTo(true));
    }
    
    // Test that "greater than or equals" requires two numeric expressions
    try {
      BaselineExpression.parse("IF (TRUE >= 1.0) 50 ELSE 100").getExpr();
      fail("Expected a ParseException");
    } catch (ParseException ex) {
      assertThat("Invalid error message: " + ex.getMessage(), ex.getMessage().contains("line 1, character 10"), equalTo(true));
      assertThat("Invalid error message: " + ex.getMessage(), ex.getMessage().contains("unexpected '>=' operator following a non-numeric expression"), equalTo(true));
    }
      
    try {
      BaselineExpression.parse("IF (1.0 >= TRUE) 50 ELSE 100").getExpr();
      fail("Expected a ParseException");
    } catch (ParseException ex) {
      assertThat("Invalid error message: " + ex.getMessage(), ex.getMessage().contains("line 1, character 12"), equalTo(true));
      assertThat("Invalid error message: " + ex.getMessage(), ex.getMessage().contains("unexpected non-numeric expression"), equalTo(true));
    }

    // Test that "less than" requires two numeric expressions
    try {
      BaselineExpression.parse("IF (TRUE < 1.0) 50 ELSE 100").getExpr();
      fail("Expected a ParseException");
    } catch (ParseException ex) {
      assertThat("Invalid error message: " + ex.getMessage(), ex.getMessage().contains("line 1, character 10"), equalTo(true));
      assertThat("Invalid error message: " + ex.getMessage(), ex.getMessage().contains("unexpected '<' operator following a non-numeric expression"), equalTo(true));
    }
      
    try {
      BaselineExpression.parse("IF (1.0 < TRUE) 50 ELSE 100").getExpr();
      fail("Expected a ParseException");
    } catch (ParseException ex) {
      assertThat("Invalid error message: " + ex.getMessage(), ex.getMessage().contains("line 1, character 11"), equalTo(true));
      assertThat("Invalid error message: " + ex.getMessage(), ex.getMessage().contains("unexpected non-numeric expression"), equalTo(true));
    }
    
    // Test that "less than or equals" requires two numeric expressions
    try {
      BaselineExpression.parse("IF (TRUE <= 1.0) 50 ELSE 100").getExpr();
      fail("Expected a ParseException");
    } catch (ParseException ex) {
      assertThat("Invalid error message: " + ex.getMessage(), ex.getMessage().contains("line 1, character 10"), equalTo(true));
      assertThat("Invalid error message: " + ex.getMessage(), ex.getMessage().contains("unexpected '<=' operator following a non-numeric expression"), equalTo(true));
    }
      
    try {
      BaselineExpression.parse("IF (1.0 <= TRUE) 50 ELSE 100").getExpr();
      fail("Expected a ParseException");
    } catch (ParseException ex) {
      assertThat("Invalid error message: " + ex.getMessage(), ex.getMessage().contains("line 1, character 12"), equalTo(true));
      assertThat("Invalid error message: " + ex.getMessage(), ex.getMessage().contains("unexpected non-numeric expression"), equalTo(true));
    }

    // Test that "addition" requires two numeric expressions
    try {
      BaselineExpression.parse("IF (TRUE) TRUE + 50 ELSE 100").getExpr();
      fail("Expected a ParseException");
    } catch (ParseException ex) {
      assertThat("Invalid error message: " + ex.getMessage(), ex.getMessage().contains("line 1, character 16"), equalTo(true));
      assertThat("Invalid error message: " + ex.getMessage(), ex.getMessage().contains("unexpected '+' operator following a non-numeric expression"), equalTo(true));
    }
      
    try {
      BaselineExpression.parse("IF (TRUE) 50 + TRUE ELSE 100").getExpr();
      fail("Expected a ParseException");
    } catch (ParseException ex) {
      assertThat("Invalid error message: " + ex.getMessage(), ex.getMessage().contains("line 1, character 16"), equalTo(true));
      assertThat("Invalid error message: " + ex.getMessage(), ex.getMessage().contains("unexpected non-numeric expression"), equalTo(true));
    }
    
    // Test that "subtraction" requires two numeric expressions
    try {
      BaselineExpression.parse("IF (TRUE) TRUE - 50 ELSE 100").getExpr();
      fail("Expected a ParseException");
    } catch (ParseException ex) {
      assertThat("Invalid error message: " + ex.getMessage(), ex.getMessage().contains("line 1, character 16"), equalTo(true));
      assertThat("Invalid error message: " + ex.getMessage(), ex.getMessage().contains("unexpected '-' operator following a non-numeric expression"), equalTo(true));
    }
      
    try {
      BaselineExpression.parse("IF (TRUE) 50 - TRUE ELSE 100").getExpr();
      fail("Expected a ParseException");
    } catch (ParseException ex) {
      assertThat("Invalid error message: " + ex.getMessage(), ex.getMessage().contains("line 1, character 16"), equalTo(true));
      assertThat("Invalid error message: " + ex.getMessage(), ex.getMessage().contains("unexpected non-numeric expression"), equalTo(true));
    }
    
    // Test that "multiplication" requires two numeric expressions
    try {
      BaselineExpression.parse("IF (TRUE) TRUE * 50 ELSE 100").getExpr();
      fail("Expected a ParseException");
    } catch (ParseException ex) {
      assertThat("Invalid error message: " + ex.getMessage(), ex.getMessage().contains("line 1, character 16"), equalTo(true));
      assertThat("Invalid error message: " + ex.getMessage(), ex.getMessage().contains("unexpected '*' operator following a non-numeric expression"), equalTo(true));
    }
      
    try {
      BaselineExpression.parse("IF (TRUE) 50 * TRUE ELSE 100").getExpr();
      fail("Expected a ParseException");
    } catch (ParseException ex) {
      assertThat("Invalid error message: " + ex.getMessage(), ex.getMessage().contains("line 1, character 16"), equalTo(true));
      assertThat("Invalid error message: " + ex.getMessage(), ex.getMessage().contains("unexpected non-numeric expression"), equalTo(true));
    }
    
    // Test that "division" requires two numeric expressions
    try {
      BaselineExpression.parse("IF (TRUE) TRUE / 50 ELSE 100").getExpr();
      fail("Expected a ParseException");
    } catch (ParseException ex) {
      assertThat("Invalid error message: " + ex.getMessage(), ex.getMessage().contains("line 1, character 16"), equalTo(true));
      assertThat("Invalid error message: " + ex.getMessage(), ex.getMessage().contains("unexpected '/' operator following a non-numeric expression"), equalTo(true));
    }
      
    try {
      BaselineExpression.parse("IF (TRUE) 50 / TRUE ELSE 100").getExpr();
      fail("Expected a ParseException");
    } catch (ParseException ex) {
      assertThat("Invalid error message: " + ex.getMessage(), ex.getMessage().contains("line 1, character 16"), equalTo(true));
      assertThat("Invalid error message: " + ex.getMessage(), ex.getMessage().contains("unexpected non-numeric expression"), equalTo(true));
    }

    // Test that unary minus must be followed by a numeric literal
    try {
      BaselineExpression.parse("IF (TRUE) -TRUE ELSE 100").getExpr();
      fail("Expected a ParseException");
    } catch (ParseException ex) {
      assertThat("Invalid error message: " + ex.getMessage(), ex.getMessage().contains("line 1, character 12"), equalTo(true));
      assertThat("Invalid error message: " + ex.getMessage(), ex.getMessage().contains("found true when expecting a number"), equalTo(true));
    }
      
    // Test that an if-condition must be a boolean expression
    try {
      BaselineExpression.parse("IF (1.0) 50 ELSE 100").getExpr();
      fail("Expected a ParseException");
    } catch (ParseException ex) {
      assertThat("Invalid error message: " + ex.getMessage(), ex.getMessage().contains("line 1, character 5"), equalTo(true));
      assertThat("Invalid error message: " + ex.getMessage(), ex.getMessage().contains("expected a boolean expression"), equalTo(true));
    }

    // Test that an if-body must be a numeric expression
    try {
      BaselineExpression.parse("IF (TRUE) FALSE ELSE 100").getExpr();
      fail("Expected a ParseException");
    } catch (ParseException ex) {
      assertThat("Invalid error message: " + ex.getMessage(), ex.getMessage().contains("line 1, character 11"), equalTo(true));
      assertThat("Invalid error message: " + ex.getMessage(), ex.getMessage().contains("expected a numeric expression"), equalTo(true));
    }

    // Test that an else-body must be a numeric expression
    try {
      BaselineExpression.parse("IF (TRUE) 1.0 ELSE TRUE").getExpr();
      fail("Expected a ParseException");
    } catch (ParseException ex) {
      assertThat("Invalid error message: " + ex.getMessage(), ex.getMessage().contains("line 1, character 20"), equalTo(true));
      assertThat("Invalid error message: " + ex.getMessage(), ex.getMessage().contains("expected a numeric expression"), equalTo(true));
    }
  
    // Test that the entire expression is a numeric expression
    try {
      BaselineExpression.parse("10 > 5").getExpr();
      fail("Expected a ParseException");
    } catch (ParseException ex) {
      assertThat("Invalid error message: " + ex.getMessage(), ex.getMessage().contains("line 1, character 1"), equalTo(true));
      assertThat("Invalid error message: " + ex.getMessage(), ex.getMessage().contains("expected a numeric expression"), equalTo(true));
    }
    
  }
  
  @Test
  public void test_parse_if_requires_else() {
    try {
      BaselineExpression.parse("IF (TRUE) 1.0").getExpr();
      fail("Expected a ParseException");
    } catch (ParseException ex) {
      assertThat("Invalid error message: " + ex.getMessage(), ex.getMessage().contains("line 1, character 14"), equalTo(true));
      assertThat("Invalid error message: " + ex.getMessage(), ex.getMessage().contains("found end of baseline expression when expecting else"), equalTo(true));
    }
    
  }

  @Test
  public void test_parse_invalid_token() {
    try {
      BaselineExpression.parse("IF (TRUE) hello").getExpr();
      fail("Expected a ParseException");
    } catch (ParseException ex) {
      assertThat("Invalid error message: " + ex.getMessage(), ex.getMessage().equals("Unrecognized string 'hello' at line 1, character 11"), equalTo(true));
    }
    
  }
  
  @Test
  public void test_parse_invalid_character() {
    try {
      BaselineExpression.parse("IF (TRUE @ FALSE) 1.0").getExpr();
      fail("Expected a ParseException");
    } catch (ParseException ex) {
      assertThat("Invalid error message: " + ex.getMessage(), ex.getMessage().equals("Unexpected character '@' at line 1, character 10"), equalTo(true));
    }
    
  }

  @Test
  public void test_parse_invalid_number() {
    try {
      BaselineExpression.parse("IF (TRUE) 08").getExpr();
      fail("Expected a ParseException");
    } catch (ParseException ex) {
      assertThat("Invalid error message: " + ex.getMessage(), ex.getMessage().equals("Invalid number starting at line 1, character 11: numbers with multiple digits cannot start with 0"), equalTo(true));
    }
    
    try {
      BaselineExpression.parse("IF (TRUE) 8.").getExpr();
      fail("Expected a ParseException");
    } catch (ParseException ex) {
      assertThat("Invalid error message: " + ex.getMessage(), ex.getMessage().equals("Invalid number starting at line 1, character 11: expected at least one digit after the '.'"), equalTo(true));
    }
    
    try {
      BaselineExpression.parse("IF (TRUE) 1.25E").getExpr();
      fail("Expected a ParseException");
    } catch (ParseException ex) {
      assertThat("Invalid error message: " + ex.getMessage(), ex.getMessage().equals("Invalid number starting at line 1, character 11: expected at least one digit after the 'E'"), equalTo(true));
    }
    
  }

  @Test
  public void test_parse_file () throws IOException {
    String expressionString = null;
    try (InputStream in = BaselineExpressionTest.class.getResourceAsStream("baseline-expression.txt")) {
      expressionString = CharStreams.toString(new InputStreamReader(
          in, Charsets.UTF_8));
      
    }
    BaselineExpression exp = BaselineExpression.parse(expressionString);
    String expected = 
           "if ("
        +     "("
        +       "(!(week_day)) && (true)"
        +     ") && ("
        +      "!(false)"
        +     ")"
        +  ") "
        +  "("
        +    "if ("
        +       "("
        +         "("
        +           "("
        +             "(avg_daily_temp) < (51.7600)"
        +           ") || ("
        +             "(avg_daily_temp) >= (100)"
        +           ")"
        +         ") || ("
        +           "(avg_daily_temp) == (75)"
        +         ")"
        +       ") && ("
        +         "(avg_daily_temp) != (-1)"
        +       ")"
        +     ") "
        +     "("
        +       "(42070.5300) - ("
        +         "(115.4300) / ("
        +           "(51.7600) - (avg_daily_temp)"
        +         ")"
        +       ")"
        +     ") else ("
        +       "(42070.5300) + ("
        +         "(576.0100) * ("
        +           "(avg_daily_temp) - (51.7600)"
        +         ")"
        +       ")"
        +     ")"
        +   ") else ("
        +     "if ("
        +       "("
        +         "(avg_daily_temp) > (50.9200)"
        +       ") && ("
        +         "("
        +           "(avg_daily_temp) / (10)"
        +          ") <= (100)"
        +       ")"
        +     ") "
        +     "("
        +       "(39660.3700) - ("
        +         "(111.9100) * ("
        +           "(50.9200) - (avg_daily_temp)"
        +         ")"
        +       ")"
        +     ") else ("
        +       "(39660.3700) + ("
        +         "(564.6300) * ("
        +           "(avg_daily_temp) - (50.9200)"
        +         ")"
        +       ")"
        +     ")"
        +   ")";
    
    assertThat(exp.toString(), equalTo(expected));
  }

  private Expression<Boolean> getCondition(Expression<Double> exp) {
    return IfExpression.class.cast(exp).getCondition();
  }

  private Expression<Double> getIfBody(Expression<Double> exp) {
    return IfExpression.class.cast(exp).getIfBody();
  }

  private Expression<Double> getElseBody(Expression<Double> exp) {
    return IfExpression.class.cast(exp).getElseBody();
  }
  
  
}
