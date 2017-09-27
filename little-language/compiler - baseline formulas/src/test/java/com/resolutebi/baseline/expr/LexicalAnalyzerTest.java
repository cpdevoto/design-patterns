package com.resolutebi.baseline.expr;

import static com.resolutebi.baseline.expr.Token.Type.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;

import org.junit.Test;

import com.resolutebi.baseline.expr.DoubleToken;
import com.resolutebi.baseline.expr.LexicalAnalysisException;
import com.resolutebi.baseline.expr.LexicalAnalyzer;
import com.resolutebi.baseline.expr.LexicalAnalyzerFactory;
import com.resolutebi.baseline.expr.Token;
import com.resolutebi.baseline.expr.VariableId;
import com.resolutebi.baseline.expr.VariableToken;

public class LexicalAnalyzerTest {
  
  private LexicalAnalyzerFactory factory = LexicalAnalyzerFactory.create();
  
  @Test
  public void test_next_token () throws IOException {
    try (Reader in = new InputStreamReader(LexicalAnalyzerTest.class.getResourceAsStream("baseline-expression.txt"))) {
      LexicalAnalyzer lexer = factory.create(in);
      
      Token token = null;
      assertThat(lexer.nextToken().getType(), equalTo(IF));
      assertThat(lexer.nextToken().getType(), equalTo(LEFT_PAREN));
      assertThat(lexer.nextToken().getType(), equalTo(NOT));
      assertThat((token = lexer.nextToken()).getType(), equalTo(VARIABLE));
      assertThat(((VariableToken) token).id(), equalTo(VariableId.WEEK_DAY));
      assertThat(lexer.nextToken().getType(), equalTo(AND));
      assertThat(lexer.nextToken().getType(), equalTo(TRUE));
      assertThat(lexer.nextToken().getType(), equalTo(AND));
      assertThat(lexer.nextToken().getType(), equalTo(NOT));
      assertThat(lexer.nextToken().getType(), equalTo(FALSE));
      assertThat(lexer.nextToken().getType(), equalTo(RIGHT_PAREN));
      
      assertThat(lexer.nextToken().getType(), equalTo(IF));
      assertThat(lexer.nextToken().getType(), equalTo(LEFT_PAREN));
      assertThat(lexer.nextToken().getType(), equalTo(LEFT_PAREN));
      assertThat((token = lexer.nextToken()).getType(), equalTo(VARIABLE));
      assertThat(((VariableToken) token).id(), equalTo(VariableId.AVG_DAILY_TEMP));
      assertThat(lexer.nextToken().getType(), equalTo(LESS_THAN));
      assertThat((token = lexer.nextToken()).getType(), equalTo(DOUBLE));
      assertThat(((DoubleToken) token).value(), equalTo(51.76));
      assertThat(lexer.nextToken().getType(), equalTo(OR));
      assertThat((token = lexer.nextToken()).getType(), equalTo(VARIABLE));
      assertThat(((VariableToken) token).id(), equalTo(VariableId.AVG_DAILY_TEMP));
      assertThat(lexer.nextToken().getType(), equalTo(GREATER_THAN_OR_EQUALS));
      assertThat((token = lexer.nextToken()).getType(), equalTo(DOUBLE));
      assertThat(((DoubleToken) token).value(), equalTo(100.0));
      assertThat(lexer.nextToken().getType(), equalTo(OR));
      assertThat((token = lexer.nextToken()).getType(), equalTo(VARIABLE));
      assertThat(((VariableToken) token).id(), equalTo(VariableId.AVG_DAILY_TEMP));
      assertThat(lexer.nextToken().getType(), equalTo(EQUALS));
      assertThat((token = lexer.nextToken()).getType(), equalTo(DOUBLE));
      assertThat(((DoubleToken) token).value(), equalTo(75.0));
      assertThat(lexer.nextToken().getType(), equalTo(RIGHT_PAREN));
      assertThat(lexer.nextToken().getType(), equalTo(AND));
      assertThat((token = lexer.nextToken()).getType(), equalTo(VARIABLE));
      assertThat(((VariableToken) token).id(), equalTo(VariableId.AVG_DAILY_TEMP));
      assertThat(lexer.nextToken().getType(), equalTo(NOT_EQUALS));
      assertThat(lexer.nextToken().getType(), equalTo(MINUS));
      assertThat((token = lexer.nextToken()).getType(), equalTo(DOUBLE));
      assertThat(((DoubleToken) token).value(), equalTo(1.0));
      assertThat(lexer.nextToken().getType(), equalTo(RIGHT_PAREN));
      
      assertThat((token = lexer.nextToken()).getType(), equalTo(DOUBLE));
      assertThat(((DoubleToken) token).value(), equalTo(42070.53));
      assertThat(lexer.nextToken().getType(), equalTo(MINUS));
      assertThat((token = lexer.nextToken()).getType(), equalTo(DOUBLE));
      assertThat(((DoubleToken) token).value(), equalTo(115.43));
      assertThat(lexer.nextToken().getType(), equalTo(DIVIDE));
      assertThat(lexer.nextToken().getType(), equalTo(LEFT_PAREN));
      assertThat((token = lexer.nextToken()).getType(), equalTo(DOUBLE));
      assertThat(((DoubleToken) token).value(), equalTo(51.76));
      assertThat(lexer.nextToken().getType(), equalTo(MINUS));
      assertThat((token = lexer.nextToken()).getType(), equalTo(VARIABLE));
      assertThat(((VariableToken) token).id(), equalTo(VariableId.AVG_DAILY_TEMP));
      assertThat(lexer.nextToken().getType(), equalTo(RIGHT_PAREN));
      
      assertThat(lexer.nextToken().getType(), equalTo(ELSE));

      assertThat((token = lexer.nextToken()).getType(), equalTo(DOUBLE));
      assertThat(((DoubleToken) token).value(), equalTo(42070.53));
      assertThat(lexer.nextToken().getType(), equalTo(PLUS));
      assertThat((token = lexer.nextToken()).getType(), equalTo(DOUBLE));
      assertThat(((DoubleToken) token).value(), equalTo(576.01));
      assertThat(lexer.nextToken().getType(), equalTo(MULTIPLY));
      assertThat(lexer.nextToken().getType(), equalTo(LEFT_PAREN));
      assertThat((token = lexer.nextToken()).getType(), equalTo(VARIABLE));
      assertThat(((VariableToken) token).id(), equalTo(VariableId.AVG_DAILY_TEMP));
      assertThat(lexer.nextToken().getType(), equalTo(MINUS));
      assertThat((token = lexer.nextToken()).getType(), equalTo(DOUBLE));
      assertThat(((DoubleToken) token).value(), equalTo(51.76));
      assertThat(lexer.nextToken().getType(), equalTo(RIGHT_PAREN));


      assertThat(lexer.nextToken().getType(), equalTo(ELSE));
      assertThat(lexer.nextToken().getType(), equalTo(IF));
      assertThat(lexer.nextToken().getType(), equalTo(LEFT_PAREN));
      assertThat((token = lexer.nextToken()).getType(), equalTo(VARIABLE));
      assertThat(((VariableToken) token).id(), equalTo(VariableId.AVG_DAILY_TEMP));
      assertThat(lexer.nextToken().getType(), equalTo(GREATER_THAN));
      assertThat((token = lexer.nextToken()).getType(), equalTo(DOUBLE));
      assertThat(((DoubleToken) token).value(), equalTo(50.92));
      assertThat(lexer.nextToken().getType(), equalTo(AND));
      assertThat((token = lexer.nextToken()).getType(), equalTo(VARIABLE));
      assertThat(((VariableToken) token).id(), equalTo(VariableId.AVG_DAILY_TEMP));
      assertThat(lexer.nextToken().getType(), equalTo(DIVIDE));
      assertThat((token = lexer.nextToken()).getType(), equalTo(DOUBLE));
      assertThat(((DoubleToken) token).value(), equalTo(10.0));
      assertThat(lexer.nextToken().getType(), equalTo(LESS_THAN_OR_EQUALS));
      assertThat((token = lexer.nextToken()).getType(), equalTo(DOUBLE));
      assertThat(((DoubleToken) token).value(), equalTo(100.0));
      assertThat(lexer.nextToken().getType(), equalTo(RIGHT_PAREN));
      
      assertThat((token = lexer.nextToken()).getType(), equalTo(DOUBLE));
      assertThat(((DoubleToken) token).value(), equalTo(39660.37));
      assertThat(lexer.nextToken().getType(), equalTo(MINUS));
      assertThat((token = lexer.nextToken()).getType(), equalTo(DOUBLE));
      assertThat(((DoubleToken) token).value(), equalTo(111.91));
      assertThat(lexer.nextToken().getType(), equalTo(DIVIDE));
      assertThat(lexer.nextToken().getType(), equalTo(LEFT_PAREN));
      assertThat((token = lexer.nextToken()).getType(), equalTo(DOUBLE));
      assertThat(((DoubleToken) token).value(), equalTo(50.92));
      assertThat(lexer.nextToken().getType(), equalTo(MINUS));
      assertThat((token = lexer.nextToken()).getType(), equalTo(VARIABLE));
      assertThat(((VariableToken) token).id(), equalTo(VariableId.AVG_DAILY_TEMP));
      assertThat(lexer.nextToken().getType(), equalTo(RIGHT_PAREN));
      
      assertThat(lexer.nextToken().getType(), equalTo(ELSE));

      assertThat((token = lexer.nextToken()).getType(), equalTo(DOUBLE));
      assertThat(((DoubleToken) token).value(), equalTo( 39660.37));
      assertThat(lexer.nextToken().getType(), equalTo(PLUS));
      assertThat((token = lexer.nextToken()).getType(), equalTo(DOUBLE));
      assertThat(((DoubleToken) token).value(), equalTo(564.63));
      assertThat(lexer.nextToken().getType(), equalTo(MULTIPLY));
      assertThat(lexer.nextToken().getType(), equalTo(LEFT_PAREN));
      assertThat((token = lexer.nextToken()).getType(), equalTo(VARIABLE));
      assertThat(((VariableToken) token).id(), equalTo(VariableId.AVG_DAILY_TEMP));
      assertThat(lexer.nextToken().getType(), equalTo(MINUS));
      assertThat((token = lexer.nextToken()).getType(), equalTo(DOUBLE));
      assertThat(((DoubleToken) token).value(), equalTo(50.92));
      assertThat(lexer.nextToken().getType(), equalTo(RIGHT_PAREN));

      assertThat(lexer.nextToken().getType(), equalTo(EOF));
      assertThat(lexer.nextToken().getType(), equalTo(EOF));
 
    }
    
  }
  
  @Test 
  public void test_invalid_word_tokenization () throws IOException {
    try {
      tokenize("hello");
      fail("Expected a LexicalAnalysisException because the string is not a reserved word or known variable");
    } catch (LexicalAnalysisException ex) {
      assertTrue("Invalid exception message: " + ex.getMessage(), ex.getMessage().contains("line 1, character 0"));
    }
    
  }

  @Test
  public void test_variable_tokenization () throws IOException {
    
    assertThat(tokenizeVariable("AVG_DAILY_TEMP"), equalTo(VariableId.AVG_DAILY_TEMP));
    assertThat(tokenizeVariable("avg_daily_temp"), equalTo(VariableId.AVG_DAILY_TEMP));
    assertThat(tokenizeVariable("Avg_Daily_Temp"), equalTo(VariableId.AVG_DAILY_TEMP));
    
    assertThat(tokenizeVariable("WEEK_DAY"), equalTo(VariableId.WEEK_DAY));
    assertThat(tokenizeVariable("week_day"), equalTo(VariableId.WEEK_DAY));
    assertThat(tokenizeVariable("Week_Day"), equalTo(VariableId.WEEK_DAY));
   
  }
  
  @Test
  public void test_number_tokenization () throws IOException {
    
    try {
      tokenizeDouble("07");
      fail("Expected a LexicalAnalysisException because multi-digit numbers cannot start with 0");
    } catch (LexicalAnalysisException ex) {
      assertTrue("Invalid exception message: " + ex.getMessage(), ex.getMessage().contains("line 1, character 0"));
    }
    
    assertThat(tokenizeDouble("125"), equalTo(125.0)); 
    assertThat(tokenizeDouble("1.25"), equalTo(1.25)); 
    assertThat(tokenizeDouble("0.0"), equalTo(0.0)); 
    assertThat(tokenizeDouble("1.0"), equalTo(1.0)); 
    assertThat(tokenizeDouble("17.5"), equalTo(17.5)); 
    
    try {
      tokenizeDouble("17.");
      fail("Expected a LexicalAnalysisException because at least one digit is expected after the .");
    } catch (LexicalAnalysisException ex) {
      assertTrue("Invalid exception message: " + ex.getMessage(), ex.getMessage().contains("line 1, character 0"));
    }

    assertThat(tokenizeDouble("1.25E-02"), equalTo(0.0125)); 
    assertThat(tokenizeDouble("1.25e-02"), equalTo(0.0125)); 
    assertThat(tokenizeDouble("1.25E-2"), equalTo(0.0125)); 
    assertThat(tokenizeDouble("1.25e-2"), equalTo(0.0125)); 

    assertThat(tokenizeDouble("1.25E+0"), equalTo(1.25)); 
    assertThat(tokenizeDouble("1.25E+00"), equalTo(1.25)); 
    assertThat(tokenizeDouble("1.25E0"), equalTo(1.25)); 
    assertThat(tokenizeDouble("1.25E00"), equalTo(1.25)); 
    assertThat(tokenizeDouble("1.25E-0"), equalTo(1.25)); 
    assertThat(tokenizeDouble("1.25E-00"), equalTo(1.25)); 

    
    try {
      tokenizeDouble("\n\t  1.25E");
      fail("Expected a LexicalAnalysisException because at least one digit is expected after the E");
    } catch (LexicalAnalysisException ex) {
      assertTrue("Invalid exception message: " + ex.getMessage(), ex.getMessage().contains("line 2, character 3"));
    }

    try {
      tokenizeDouble("1.25E+");
      fail("Expected a LexicalAnalysisException because at least one digit is expected after the E");
    } catch (LexicalAnalysisException ex) {
      assertTrue("Invalid exception message: " + ex.getMessage(), ex.getMessage().contains("line 1, character 0"));
    }
  
    try {
      tokenizeDouble(" 1.25E-");
      fail("Expected a LexicalAnalysisException because at least one digit is expected after the E");
    } catch (LexicalAnalysisException ex) {
      assertTrue("Invalid exception message: " + ex.getMessage(), ex.getMessage().contains("line 1, character 1"));
    }
  }
  
  private Token tokenize(String string) throws IOException {
    try (Reader in = new StringReader(string)) {
      LexicalAnalyzer lexer = factory.create(in);
      return lexer.nextToken();
    }
  }

  private VariableId<?> tokenizeVariable(String string) throws IOException {
    try (Reader in = new StringReader(string)) {
      LexicalAnalyzer lexer = factory.create(in);
      Token token = lexer.nextToken();
      assertThat(token, instanceOf(VariableToken.class));
      return VariableToken.class.cast(token).id();
    }
  }

  private double tokenizeDouble(String string) throws IOException {
    try (Reader in = new StringReader(string)) {
      LexicalAnalyzer lexer = factory.create(in);
      Token token = lexer.nextToken();
      assertThat(token, instanceOf(DoubleToken.class));
      return DoubleToken.class.cast(token).value();
    }
  }
  
}
