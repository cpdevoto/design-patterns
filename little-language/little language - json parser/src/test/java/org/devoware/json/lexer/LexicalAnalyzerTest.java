package org.devoware.json.lexer;

import static org.devoware.json.symbols.Token.Type.COLON;
import static org.devoware.json.symbols.Token.Type.COMMA;
import static org.devoware.json.symbols.Token.Type.DOUBLE;
import static org.devoware.json.symbols.Token.Type.EOF;
import static org.devoware.json.symbols.Token.Type.FALSE;
import static org.devoware.json.symbols.Token.Type.LEFT_CURLY_BRACKET;
import static org.devoware.json.symbols.Token.Type.LEFT_SQUARE_BRACKET;
import static org.devoware.json.symbols.Token.Type.LONG;
import static org.devoware.json.symbols.Token.Type.NULL;
import static org.devoware.json.symbols.Token.Type.RIGHT_CURLY_BRACKET;
import static org.devoware.json.symbols.Token.Type.RIGHT_SQUARE_BRACKET;
import static org.devoware.json.symbols.Token.Type.STRING;
import static org.devoware.json.symbols.Token.Type.TRUE;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.devoware.json.symbols.DoubleToken;
import org.devoware.json.symbols.LongToken;
import org.devoware.json.symbols.StringToken;
import org.devoware.json.symbols.Token;
import org.junit.Test;

public class LexicalAnalyzerTest {
  
  private LexicalAnalyzerFactory factory = LexicalAnalyzerFactory.create();
  
  @Test
  public void test_next_token () throws IOException {
    String s = "\t\r{\n"
        + "\"string1\": \"kWh\",\n"
        + "\"number1\": 1.25e-2,\n"
        + "\"boolean1\":true,"
        + "\"boolean2\":false,"
        + "\"var1\":null,"
        + "\"array1\": [true, false, null, \"string\", 0],"
        + "\"object1\": {\"var1\": true, \"var2\": \"hello\"}}";
    try (Reader in = new StringReader(s)) {
      LexicalAnalyzer lexer = factory.create(in);
      
      Token token = null;
      assertThat(lexer.nextToken().getType(), equalTo(LEFT_CURLY_BRACKET));
      assertThat((token = lexer.nextToken()).getType(), equalTo(STRING));
      assertThat(((StringToken) token).value(), equalTo("string1"));
      assertThat(lexer.nextToken().getType(), equalTo(COLON));
      assertThat((token = lexer.nextToken()).getType(), equalTo(STRING));
      assertThat(((StringToken) token).value(), equalTo("kWh"));
      assertThat(lexer.nextToken().getType(), equalTo(COMMA));
      assertThat((token = lexer.nextToken()).getType(), equalTo(STRING));
      assertThat(((StringToken) token).value(), equalTo("number1"));
      assertThat(lexer.nextToken().getType(), equalTo(COLON));
      assertThat((token = lexer.nextToken()).getType(), equalTo(DOUBLE));
      assertThat(((DoubleToken) token).value(), equalTo(0.0125));
      assertThat(lexer.nextToken().getType(), equalTo(COMMA));
      assertThat((token = lexer.nextToken()).getType(), equalTo(STRING));
      assertThat(((StringToken) token).value(), equalTo("boolean1"));
      assertThat(lexer.nextToken().getType(), equalTo(COLON));
      assertThat(lexer.nextToken().getType(), equalTo(TRUE));
      assertThat(lexer.nextToken().getType(), equalTo(COMMA));
      assertThat((token = lexer.nextToken()).getType(), equalTo(STRING));
      assertThat(((StringToken) token).value(), equalTo("boolean2"));
      assertThat(lexer.nextToken().getType(), equalTo(COLON));
      assertThat(lexer.nextToken().getType(), equalTo(FALSE));
      assertThat(lexer.nextToken().getType(), equalTo(COMMA));
      assertThat((token = lexer.nextToken()).getType(), equalTo(STRING));
      assertThat(((StringToken) token).value(), equalTo("var1"));
      assertThat(lexer.nextToken().getType(), equalTo(COLON));
      assertThat(lexer.nextToken().getType(), equalTo(NULL));
      assertThat(lexer.nextToken().getType(), equalTo(COMMA));
      assertThat((token = lexer.nextToken()).getType(), equalTo(STRING));
      assertThat(((StringToken) token).value(), equalTo("array1"));
      assertThat(lexer.nextToken().getType(), equalTo(COLON));
      assertThat(lexer.nextToken().getType(), equalTo(LEFT_SQUARE_BRACKET));
      assertThat(lexer.nextToken().getType(), equalTo(TRUE));
      assertThat(lexer.nextToken().getType(), equalTo(COMMA));
      assertThat(lexer.nextToken().getType(), equalTo(FALSE));
      assertThat(lexer.nextToken().getType(), equalTo(COMMA));
      assertThat(lexer.nextToken().getType(), equalTo(NULL));
      assertThat(lexer.nextToken().getType(), equalTo(COMMA));
      assertThat((token = lexer.nextToken()).getType(), equalTo(STRING));
      assertThat(((StringToken) token).value(), equalTo("string"));
      assertThat(lexer.nextToken().getType(), equalTo(COMMA));
      assertThat((token = lexer.nextToken()).getType(), equalTo(LONG));
      assertThat(((LongToken) token).value(), equalTo(0L));
      assertThat(lexer.nextToken().getType(), equalTo(RIGHT_SQUARE_BRACKET));
      assertThat(lexer.nextToken().getType(), equalTo(COMMA));
      assertThat((token = lexer.nextToken()).getType(), equalTo(STRING));
      assertThat(((StringToken) token).value(), equalTo("object1"));
      assertThat(lexer.nextToken().getType(), equalTo(COLON));
      assertThat(lexer.nextToken().getType(), equalTo(LEFT_CURLY_BRACKET));
      assertThat((token = lexer.nextToken()).getType(), equalTo(STRING));
      assertThat(((StringToken) token).value(), equalTo("var1"));
      assertThat(lexer.nextToken().getType(), equalTo(COLON));
      assertThat(lexer.nextToken().getType(), equalTo(TRUE));
      assertThat(lexer.nextToken().getType(), equalTo(COMMA));
      assertThat((token = lexer.nextToken()).getType(), equalTo(STRING));
      assertThat(((StringToken) token).value(), equalTo("var2"));
      assertThat(lexer.nextToken().getType(), equalTo(COLON));
      assertThat((token = lexer.nextToken()).getType(), equalTo(STRING));
      assertThat(((StringToken) token).value(), equalTo("hello"));
      assertThat(lexer.nextToken().getType(), equalTo(RIGHT_CURLY_BRACKET));
      assertThat(lexer.nextToken().getType(), equalTo(RIGHT_CURLY_BRACKET));
      assertThat(lexer.nextToken().getType(), equalTo(EOF));
      assertThat(lexer.nextToken().getType(), equalTo(EOF));

    }
    
  }
  
  @Test 
  public void test_invalid_word_tokenization () throws IOException {
    try {
      tokenize("hello");
      fail("Expected a LexicalAnalysisException because the unquoted string is not a reserved word");
    } catch (LexicalAnalysisException ex) {
      assertTrue("Invalid exception message: " + ex.getMessage(), ex.getMessage().contains("line 1, character 0"));
    }
    
  }

  @Test
  public void test_string_tokenization () throws IOException {
    assertThat(tokenizeString("\"\\\"\""), equalTo("\"")); // escaped quote
    assertThat(tokenizeString("\"\\\\\""), equalTo("\\")); // escaped back-slash
    assertThat(tokenizeString("\"\\/\""), equalTo("/"));   // escaped forward-slash
    assertThat(tokenizeString("\"\\t\""), equalTo("\t"));  // tab
    assertThat(tokenizeString("\"\\b\""), equalTo("\b"));  // backspace
    assertThat(tokenizeString("\"\\f\""), equalTo("\f"));  // formfeed
    assertThat(tokenizeString("\"\\n\""), equalTo("\n"));  // linefeed
    assertThat(tokenizeString("\"\\r\""), equalTo("\r"));  // carriage return
    assertThat(tokenizeString("\"\\u005B\""), equalTo("["));  // unicode escape
    
    String s = tokenizeString("\"\\\"\\\\\\/\\t\\b\\f\\n\\r\\u005Bhello\\u005D\"");
    assertThat(s, equalTo("\"\\/\t\b\f\n\r[hello]"));
    
    assertThat(tokenizeString("\"Kilroy\\n\\twas here!\""), equalTo("Kilroy\n\twas here!"));

    try {
      tokenizeString("\"unclosed string");
      fail("Expected a LexicalAnalysisException because of an unclosed quotation mark");
    } catch (LexicalAnalysisException ex) {
      assertTrue("Invalid exception message: " + ex.getMessage(), ex.getMessage().contains("line 1, character 0"));
    }
    
    try {
      tokenizeString("\"string with invalid escape sequence: \\x\"");
      fail("Expected a LexicalAnalysisException because of an invalid escape sequence");
    } catch (LexicalAnalysisException ex) {
      assertTrue("Invalid exception message: " + ex.getMessage(), ex.getMessage().contains("line 1, character 38"));
    }
    
    try {
      tokenizeString("\"string with invalid escape sequence: \\uA09XX\"");
      fail("Expected a LexicalAnalysisException because of an invalid unicode escape sequence");
    } catch (LexicalAnalysisException ex) {
      assertTrue("Invalid exception message: " + ex.getMessage(), ex.getMessage().contains("line 1, character 38"));
    }
 
    try {
      tokenizeString("\"string with invalid escape sequence: \\");
      fail("Expected a LexicalAnalysisException because of an unterminated escape sequence");
    } catch (LexicalAnalysisException ex) {
      assertTrue("Invalid exception message: " + ex.getMessage(), ex.getMessage().contains("line 1, character 38"));
    }
   
  }
  
  @Test
  public void test_number_tokenization () throws IOException {
    assertThat(tokenizeLong("1"), equalTo(1L)); 
    assertThat(tokenizeLong("0"), equalTo(0L)); 
    assertThat(tokenizeLong("-1"), equalTo(-1L)); 
    assertThat(tokenizeLong("17"), equalTo(17L)); 
    
    try {
      tokenizeDouble("07");
      fail("Expected a LexicalAnalysisException because multi-digit numbers cannot start with 0");
    } catch (LexicalAnalysisException ex) {
      assertTrue("Invalid exception message: " + ex.getMessage(), ex.getMessage().contains("line 1, character 0"));
    }
    
    assertThat(tokenizeDouble("1.25"), equalTo(1.25)); 
    assertThat(tokenizeDouble("0.0"), equalTo(0.0)); 
    assertThat(tokenizeDouble("-1.0"), equalTo(-1.0)); 
    assertThat(tokenizeDouble("17.5"), equalTo(17.5)); 
    
    try {
      tokenizeDouble("-17.");
      fail("Expected a LexicalAnalysisException because at least one digit is expected after the .");
    } catch (LexicalAnalysisException ex) {
      assertTrue("Invalid exception message: " + ex.getMessage(), ex.getMessage().contains("line 1, character 0"));
    }

    assertThat(tokenizeLong("1.25E+02"), equalTo(125L)); 
    assertThat(tokenizeLong("1.25e+02"), equalTo(125L)); 
    assertThat(tokenizeLong("1.25E02"), equalTo(125L)); 
    assertThat(tokenizeLong("1.25e02"), equalTo(125L)); 
    assertThat(tokenizeLong("1.25E2"), equalTo(125L)); 
    assertThat(tokenizeLong("1.25e2"), equalTo(125L)); 
    
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

  private String tokenizeString(String string) throws IOException {
    try (Reader in = new StringReader(string)) {
      LexicalAnalyzer lexer = factory.create(in);
      Token token = lexer.nextToken();
      assertThat(token, instanceOf(StringToken.class));
      return StringToken.class.cast(token).value();
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
  
  private long tokenizeLong(String string) throws IOException {
    try (Reader in = new StringReader(string)) {
      LexicalAnalyzer lexer = factory.create(in);
      Token token = lexer.nextToken();
      assertThat(token, instanceOf(LongToken.class));
      return LongToken.class.cast(token).value();
    }
  }

}
