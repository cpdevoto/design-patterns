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
      assertThat((token = lexer.nextToken()).getType(), equalTo(IF));
      assertPosition(token, 1, 1);
      assertThat((token = lexer.nextToken()).getType(), equalTo(LEFT_PAREN));
      assertPosition(token, 1, 3);
      assertThat((token = lexer.nextToken()).getType(), equalTo(NOT));
      assertPosition(token, 1, 4);
      assertThat((token = lexer.nextToken()).getType(), equalTo(VARIABLE));
      assertPosition(token, 1, 5);
      assertThat(((VariableToken) token).id(), equalTo(VariableId.WEEK_DAY));
      assertThat((token = lexer.nextToken()).getType(), equalTo(AND));
      assertPosition(token, 1, 14);
      assertThat((token = lexer.nextToken()).getType(), equalTo(TRUE));
      assertPosition(token, 1, 17);
      assertThat((token = lexer.nextToken()).getType(), equalTo(AND));
      assertPosition(token, 1, 22);
      assertThat((token = lexer.nextToken()).getType(), equalTo(NOT));
      assertPosition(token, 1, 25);
      assertThat((token = lexer.nextToken()).getType(), equalTo(FALSE));
      assertPosition(token, 1, 26);
      assertThat((token = lexer.nextToken()).getType(), equalTo(RIGHT_PAREN));
      assertPosition(token, 1, 31);

      assertThat((token = lexer.nextToken()).getType(), equalTo(IF));
      assertPosition(token, 2, 7);
      assertThat((token = lexer.nextToken()).getType(), equalTo(LEFT_PAREN));
      assertPosition(token, 2, 9);
      assertThat((token = lexer.nextToken()).getType(), equalTo(LEFT_PAREN));
      assertPosition(token, 2, 10);
      assertThat((token = lexer.nextToken()).getType(), equalTo(VARIABLE));
      assertPosition(token, 2, 11);
      assertThat(((VariableToken) token).id(), equalTo(VariableId.AVG_DAILY_TEMP));
      assertThat((token = lexer.nextToken()).getType(), equalTo(LESS_THAN));
      assertPosition(token, 2, 26);
      assertThat((token = lexer.nextToken()).getType(), equalTo(DOUBLE));
      assertPosition(token, 2, 28);
      assertThat(((DoubleToken) token).value(), equalTo(51.76));
      assertThat((token = lexer.nextToken()).getType(), equalTo(OR));
      assertPosition(token, 2, 34);
      assertThat((token = lexer.nextToken()).getType(), equalTo(VARIABLE));
      assertPosition(token, 2, 37);
      assertThat(((VariableToken) token).id(), equalTo(VariableId.AVG_DAILY_TEMP));
      assertThat((token = lexer.nextToken()).getType(), equalTo(GREATER_THAN_OR_EQUALS));
      assertPosition(token, 2, 52);
      assertThat((token = lexer.nextToken()).getType(), equalTo(DOUBLE));
      assertPosition(token, 2, 55);
      assertThat(((DoubleToken) token).value(), equalTo(100.0));
      assertThat((token = lexer.nextToken()).getType(), equalTo(OR));
      assertPosition(token, 2, 59);
      assertThat((token = lexer.nextToken()).getType(), equalTo(VARIABLE));
      assertPosition(token, 2, 62);
      assertThat(((VariableToken) token).id(), equalTo(VariableId.AVG_DAILY_TEMP));
      assertThat((token = lexer.nextToken()).getType(), equalTo(EQUALS));
      assertPosition(token, 2, 77);
      assertThat((token = lexer.nextToken()).getType(), equalTo(DOUBLE));
      assertPosition(token, 2, 80);
      assertThat(((DoubleToken) token).value(), equalTo(75.0));
      assertThat((token = lexer.nextToken()).getType(), equalTo(RIGHT_PAREN));
      assertPosition(token, 2, 82);
      assertThat((token = lexer.nextToken()).getType(), equalTo(AND));
      assertPosition(token, 2, 84);
      assertThat((token = lexer.nextToken()).getType(), equalTo(VARIABLE));
      assertPosition(token, 2, 87);
      assertThat(((VariableToken) token).id(), equalTo(VariableId.AVG_DAILY_TEMP));
      assertThat((token = lexer.nextToken()).getType(), equalTo(NOT_EQUALS));
      assertPosition(token, 2, 102);
      assertThat((token = lexer.nextToken()).getType(), equalTo(MINUS));
      assertPosition(token, 2, 105);
      assertThat((token = lexer.nextToken()).getType(), equalTo(DOUBLE));
      assertPosition(token, 2, 106);
      assertThat(((DoubleToken) token).value(), equalTo(1.0));
      assertThat((token = lexer.nextToken()).getType(), equalTo(RIGHT_PAREN));
      assertPosition(token, 2, 109);

      assertThat((token = lexer.nextToken()).getType(), equalTo(DOUBLE));
      assertPosition(token, 3, 9);
      assertThat(((DoubleToken) token).value(), equalTo(42070.53));
      assertThat((token = lexer.nextToken()).getType(), equalTo(MINUS));
      assertPosition(token, 3, 17);
      assertThat((token = lexer.nextToken()).getType(), equalTo(DOUBLE));
      assertPosition(token, 3, 18);
      assertThat(((DoubleToken) token).value(), equalTo(115.43));
      assertThat((token = lexer.nextToken()).getType(), equalTo(DIVIDE));
      assertPosition(token, 3, 24);
      assertThat((token = lexer.nextToken()).getType(), equalTo(LEFT_PAREN));
      assertPosition(token, 3, 25);
      assertThat((token = lexer.nextToken()).getType(), equalTo(DOUBLE));
      assertPosition(token, 3, 26);
      assertThat(((DoubleToken) token).value(), equalTo(51.76));
      assertThat((token = lexer.nextToken()).getType(), equalTo(MINUS));
      assertPosition(token, 3, 32);
      assertThat((token = lexer.nextToken()).getType(), equalTo(VARIABLE));
      assertPosition(token, 3, 34);
      assertThat(((VariableToken) token).id(), equalTo(VariableId.AVG_DAILY_TEMP));
      assertThat((token = lexer.nextToken()).getType(), equalTo(RIGHT_PAREN));
      assertPosition(token, 3, 48);

      assertThat((token = lexer.nextToken()).getType(), equalTo(ELSE));
      assertPosition(token, 4, 7);

      assertThat((token = lexer.nextToken()).getType(), equalTo(DOUBLE));
      assertPosition(token, 5, 9);
      assertThat(((DoubleToken) token).value(), equalTo(42070.53));
      assertThat((token = lexer.nextToken()).getType(), equalTo(PLUS));
      assertPosition(token, 5, 17);
      assertThat((token = lexer.nextToken()).getType(), equalTo(DOUBLE));
      assertPosition(token, 5, 18);
      assertThat(((DoubleToken) token).value(), equalTo(576.01));
      assertThat((token = lexer.nextToken()).getType(), equalTo(MULTIPLY));
      assertPosition(token, 5, 24);
      assertThat((token = lexer.nextToken()).getType(), equalTo(LEFT_PAREN));
      assertPosition(token, 5, 25);
      assertThat((token = lexer.nextToken()).getType(), equalTo(VARIABLE));
      assertPosition(token, 5, 26);
      assertThat(((VariableToken) token).id(), equalTo(VariableId.AVG_DAILY_TEMP));
      assertThat((token = lexer.nextToken()).getType(), equalTo(MINUS));
      assertPosition(token, 5, 41);
      assertThat((token = lexer.nextToken()).getType(), equalTo(DOUBLE));
      assertPosition(token, 5, 43);
      assertThat(((DoubleToken) token).value(), equalTo(51.76));
      assertThat((token = lexer.nextToken()).getType(), equalTo(RIGHT_PAREN));
      assertPosition(token, 5, 48);

      assertThat((token = lexer.nextToken()).getType(), equalTo(ELSE));
      assertPosition(token, 6, 1);
      
      
      assertThat((token = lexer.nextToken()).getType(), equalTo(IF));
      assertPosition(token, 7, 7);
      assertThat((token = lexer.nextToken()).getType(), equalTo(LEFT_PAREN));
      assertPosition(token, 7, 9);
      assertThat((token = lexer.nextToken()).getType(), equalTo(VARIABLE));
      assertPosition(token, 7, 10);
      assertThat(((VariableToken) token).id(), equalTo(VariableId.AVG_DAILY_TEMP));
      assertThat((token = lexer.nextToken()).getType(), equalTo(GREATER_THAN));
      assertPosition(token, 7, 25);
      assertThat((token = lexer.nextToken()).getType(), equalTo(DOUBLE));
      assertPosition(token, 7, 27);
      assertThat(((DoubleToken) token).value(), equalTo(50.92));
      assertThat((token = lexer.nextToken()).getType(), equalTo(AND));
      assertPosition(token, 7, 33);
      assertThat((token = lexer.nextToken()).getType(), equalTo(VARIABLE));
      assertPosition(token, 7, 36);
      assertThat(((VariableToken) token).id(), equalTo(VariableId.AVG_DAILY_TEMP));
      assertThat((token = lexer.nextToken()).getType(), equalTo(DIVIDE));
      assertPosition(token, 7, 51);
      assertThat((token = lexer.nextToken()).getType(), equalTo(DOUBLE));
      assertPosition(token, 7, 53);
      assertThat(((DoubleToken) token).value(), equalTo(10.0));
      assertThat((token = lexer.nextToken()).getType(), equalTo(LESS_THAN_OR_EQUALS));
      assertPosition(token, 7, 56);
      assertThat((token = lexer.nextToken()).getType(), equalTo(DOUBLE));
      assertPosition(token, 7, 59);
      assertThat(((DoubleToken) token).value(), equalTo(100.0));
      assertThat((token = lexer.nextToken()).getType(), equalTo(RIGHT_PAREN));
      assertPosition(token, 7, 64);
      
      assertThat((token = lexer.nextToken()).getType(), equalTo(DOUBLE));
      assertPosition(token, 8, 9);
      assertThat(((DoubleToken) token).value(), equalTo(39660.37));
      assertThat((token = lexer.nextToken()).getType(), equalTo(MINUS));
      assertPosition(token, 8, 17);
      assertThat((token = lexer.nextToken()).getType(), equalTo(DOUBLE));
      assertPosition(token, 8, 18);
      assertThat(((DoubleToken) token).value(), equalTo(111.91));
      assertThat((token = lexer.nextToken()).getType(), equalTo(MULTIPLY));
      assertPosition(token, 8, 24);
      assertThat((token = lexer.nextToken()).getType(), equalTo(LEFT_PAREN));
      assertPosition(token, 8, 25);
      assertThat((token = lexer.nextToken()).getType(), equalTo(DOUBLE));
      assertPosition(token, 8, 26);
      assertThat(((DoubleToken) token).value(), equalTo(50.92));
      assertThat((token = lexer.nextToken()).getType(), equalTo(MINUS));
      assertPosition(token, 8, 32);
      assertThat((token = lexer.nextToken()).getType(), equalTo(VARIABLE));
      assertPosition(token, 8, 34);
      assertThat(((VariableToken) token).id(), equalTo(VariableId.AVG_DAILY_TEMP));
      assertThat((token = lexer.nextToken()).getType(), equalTo(RIGHT_PAREN));
      assertPosition(token, 8, 48);
      
      assertThat((token = lexer.nextToken()).getType(), equalTo(ELSE));
      assertPosition(token, 9, 7);

      assertThat((token = lexer.nextToken()).getType(), equalTo(DOUBLE));
      assertPosition(token, 10, 9);
      assertThat(((DoubleToken) token).value(), equalTo( 39660.37));
      assertThat((token = lexer.nextToken()).getType(), equalTo(PLUS));
      assertPosition(token, 10, 17);
      assertThat((token = lexer.nextToken()).getType(), equalTo(DOUBLE));
      assertPosition(token, 10, 18);
      assertThat(((DoubleToken) token).value(), equalTo(564.63));
      assertThat((token = lexer.nextToken()).getType(), equalTo(MULTIPLY));
      assertPosition(token, 10, 24);
      assertThat((token = lexer.nextToken()).getType(), equalTo(LEFT_PAREN));
      assertPosition(token, 10, 25);
      assertThat((token = lexer.nextToken()).getType(), equalTo(VARIABLE));
      assertPosition(token, 10, 26);
      assertThat(((VariableToken) token).id(), equalTo(VariableId.AVG_DAILY_TEMP));
      assertThat((token = lexer.nextToken()).getType(), equalTo(MINUS));
      assertPosition(token, 10, 41);
      assertThat((token = lexer.nextToken()).getType(), equalTo(DOUBLE));
      assertPosition(token, 10, 43);
      assertThat(((DoubleToken) token).value(), equalTo(50.92));
      assertThat((token = lexer.nextToken()).getType(), equalTo(RIGHT_PAREN));
      assertPosition(token, 10, 48);

      assertThat((token = lexer.nextToken()).getType(), equalTo(EOF));
      assertPosition(token, 10, 49);
      assertThat((token = lexer.nextToken()).getType(), equalTo(EOF));
      assertPosition(token, 10, 49);
 
    }
    
  }
  
  @Test 
  public void test_invalid_word_tokenization () throws IOException {
    try {
      tokenize("hello");
      fail("Expected a LexicalAnalysisException because the string is not a reserved word or known variable");
    } catch (LexicalAnalysisException ex) {
      assertTrue("Invalid exception message: " + ex.getMessage(), ex.getMessage().contains("line 1, character 1"));
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
      assertTrue("Invalid exception message: " + ex.getMessage(), ex.getMessage().contains("line 1, character 1"));
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
      assertTrue("Invalid exception message: " + ex.getMessage(), ex.getMessage().contains("line 1, character 1"));
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
      assertTrue("Invalid exception message: " + ex.getMessage(), ex.getMessage().contains("line 2, character 4"));
    }

    try {
      tokenizeDouble("1.25E+");
      fail("Expected a LexicalAnalysisException because at least one digit is expected after the E");
    } catch (LexicalAnalysisException ex) {
      assertTrue("Invalid exception message: " + ex.getMessage(), ex.getMessage().contains("line 1, character 1"));
    }
  
    try {
      tokenizeDouble(" 1.25E-");
      fail("Expected a LexicalAnalysisException because at least one digit is expected after the E");
    } catch (LexicalAnalysisException ex) {
      assertTrue("Invalid exception message: " + ex.getMessage(), ex.getMessage().contains("line 1, character 2"));
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
  
  private void assertPosition(Token token, int lineNumber, int charNumber) {
    assertThat(token.getPosition().getLine(), equalTo(lineNumber));
    assertThat(token.getPosition().getCharacter(), equalTo(charNumber));
    
  }
  
}
