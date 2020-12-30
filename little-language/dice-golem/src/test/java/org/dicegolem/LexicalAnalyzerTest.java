package org.dicegolem;

import static org.dicegolem.Token.Type.DIE;
import static org.dicegolem.Token.Type.DROP_HIGHEST;
import static org.dicegolem.Token.Type.DROP_LOWEST;
import static org.dicegolem.Token.Type.EOF;
import static org.dicegolem.Token.Type.KEEP_HIGHEST;
import static org.dicegolem.Token.Type.KEEP_LOWEST;
import static org.dicegolem.Token.Type.LEFT_PAREN;
import static org.dicegolem.Token.Type.MINUS;
import static org.dicegolem.Token.Type.NUMBER;
import static org.dicegolem.Token.Type.PLUS;
import static org.dicegolem.Token.Type.REROLL_ONCE;
import static org.dicegolem.Token.Type.RIGHT_PAREN;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.StringReader;
import java.util.function.Consumer;

import org.dicegolem.LexicalAnalysisException;
import org.dicegolem.LexicalAnalyzer;
import org.dicegolem.NumberToken;
import org.dicegolem.Token;
import org.junit.Assert;
import org.junit.Test;

public class LexicalAnalyzerTest {

  @Test
  public void test_valid_expression() {

    String expression = "4d6kh3ro<2 + (2D4KL1 - 2D20dL1 + 3d20Dh2)";

    generateLexer(expression, lexer -> {
      Token token;
      NumberToken numToken;

      // Token 1: NUMBER(4)
      token = lexer.nextToken();
      assertThat(token, notNullValue());
      assertThat(token.getType(), equalTo(NUMBER));
      numToken = NumberToken.class.cast(token);
      assertThat(numToken.getValue(), equalTo(4));

      // Token 2: DIE
      token = lexer.nextToken();
      assertThat(token, notNullValue());
      assertThat(token.getType(), equalTo(DIE));

      // Token 3: NUMBER(6)
      token = lexer.nextToken();
      assertThat(token, notNullValue());
      assertThat(token.getType(), equalTo(NUMBER));
      numToken = NumberToken.class.cast(token);
      assertThat(numToken.getValue(), equalTo(6));

      // Token 4: KEEP_HIGHEST
      token = lexer.nextToken();
      assertThat(token, notNullValue());
      assertThat(token.getType(), equalTo(KEEP_HIGHEST));

      // Token 5: NUMBER(3)
      token = lexer.nextToken();
      assertThat(token, notNullValue());
      assertThat(token.getType(), equalTo(NUMBER));
      numToken = NumberToken.class.cast(token);
      assertThat(numToken.getValue(), equalTo(3));

      // Token 6: REROLL_ONCE
      token = lexer.nextToken();
      assertThat(token, notNullValue());
      assertThat(token.getType(), equalTo(REROLL_ONCE));

      // Token 7: NUMBER(2)
      token = lexer.nextToken();
      assertThat(token, notNullValue());
      assertThat(token.getType(), equalTo(NUMBER));
      numToken = NumberToken.class.cast(token);
      assertThat(numToken.getValue(), equalTo(2));

      // Token 8: PLUS
      token = lexer.nextToken();
      assertThat(token, notNullValue());
      assertThat(token.getType(), equalTo(PLUS));

      // Token 9: LEFT_PAREN
      token = lexer.nextToken();
      assertThat(token, notNullValue());
      assertThat(token.getType(), equalTo(LEFT_PAREN));

      // Token 10: NUMBER(2)
      token = lexer.nextToken();
      assertThat(token, notNullValue());
      assertThat(token.getType(), equalTo(NUMBER));
      numToken = NumberToken.class.cast(token);
      assertThat(numToken.getValue(), equalTo(2));

      // Token 11: DIE
      token = lexer.nextToken();
      assertThat(token, notNullValue());
      assertThat(token.getType(), equalTo(DIE));

      // Token 12: NUMBER(4)
      token = lexer.nextToken();
      assertThat(token, notNullValue());
      assertThat(token.getType(), equalTo(NUMBER));
      numToken = NumberToken.class.cast(token);
      assertThat(numToken.getValue(), equalTo(4));

      // Token 13: KEEP_LOWEST
      token = lexer.nextToken();
      assertThat(token, notNullValue());
      assertThat(token.getType(), equalTo(KEEP_LOWEST));

      // Token 14: NUMBER(1)
      token = lexer.nextToken();
      assertThat(token, notNullValue());
      assertThat(token.getType(), equalTo(NUMBER));
      numToken = NumberToken.class.cast(token);
      assertThat(numToken.getValue(), equalTo(1));

      // Token 15: MINUS
      token = lexer.nextToken();
      assertThat(token, notNullValue());
      assertThat(token.getType(), equalTo(MINUS));

      // Token 16: NUMBER(2)
      token = lexer.nextToken();
      assertThat(token, notNullValue());
      assertThat(token.getType(), equalTo(NUMBER));
      numToken = NumberToken.class.cast(token);
      assertThat(numToken.getValue(), equalTo(2));

      // Token 17: DIE
      token = lexer.nextToken();
      assertThat(token, notNullValue());
      assertThat(token.getType(), equalTo(DIE));

      // Token 18: NUMBER(20)
      token = lexer.nextToken();
      assertThat(token, notNullValue());
      assertThat(token.getType(), equalTo(NUMBER));
      numToken = NumberToken.class.cast(token);
      assertThat(numToken.getValue(), equalTo(20));

      // Token 19: DROP_LOWEST
      token = lexer.nextToken();
      assertThat(token, notNullValue());
      assertThat(token.getType(), equalTo(DROP_LOWEST));

      // Token 20: NUMBER(1)
      token = lexer.nextToken();
      assertThat(token, notNullValue());
      assertThat(token.getType(), equalTo(NUMBER));
      numToken = NumberToken.class.cast(token);
      assertThat(numToken.getValue(), equalTo(1));

      // Token 21: PLUS
      token = lexer.nextToken();
      assertThat(token, notNullValue());
      assertThat(token.getType(), equalTo(PLUS));

      // Token 22: NUMBER(3)
      token = lexer.nextToken();
      assertThat(token, notNullValue());
      assertThat(token.getType(), equalTo(NUMBER));
      numToken = NumberToken.class.cast(token);
      assertThat(numToken.getValue(), equalTo(3));

      // Token 23: DIE
      token = lexer.nextToken();
      assertThat(token, notNullValue());
      assertThat(token.getType(), equalTo(DIE));

      // Token 24: NUMBER(20)
      token = lexer.nextToken();
      assertThat(token, notNullValue());
      assertThat(token.getType(), equalTo(NUMBER));
      numToken = NumberToken.class.cast(token);
      assertThat(numToken.getValue(), equalTo(20));

      // Token 25: DROP_HIGHEST
      token = lexer.nextToken();
      assertThat(token, notNullValue());
      assertThat(token.getType(), equalTo(DROP_HIGHEST));

      // Token 26: NUMBER(2)
      token = lexer.nextToken();
      assertThat(token, notNullValue());
      assertThat(token.getType(), equalTo(NUMBER));
      numToken = NumberToken.class.cast(token);
      assertThat(numToken.getValue(), equalTo(2));

      // Token 27: RIGHT_PAREN
      token = lexer.nextToken();
      assertThat(token, notNullValue());
      assertThat(token.getType(), equalTo(RIGHT_PAREN));

      // Token 28: EOF
      token = lexer.nextToken();
      assertThat(token, notNullValue());
      assertThat(token.getType(), equalTo(EOF));

      // Test that nextToken() just keeps returning EOF
      token = lexer.nextToken();
      assertThat(token, notNullValue());
      assertThat(token.getType(), equalTo(EOF));
      token = lexer.nextToken();
      assertThat(token, notNullValue());
      assertThat(token.getType(), equalTo(EOF));

    });

  }

  @Test
  public void test_expression_with_invalid_characters() {
    assertLexicalAnalysisException("4d6 + $5", '$', 1, 7);
    assertLexicalAnalysisException("4d6kD4", 'D', 1, 5);
    assertLexicalAnalysisException("4d6dr1", '1', 1, 6);
    assertLexicalAnalysisException("4d6rO1", '1', 1, 6);
    assertLexicalAnalysisException("4d6rO>1", '>', 1, 6);
    assertLexicalAnalysisException("4d6rOD1", 'D', 1, 6);
    assertLexicalAnalysisException("4d6 + \n\n4 - %2", '%', 3, 5);

  }

  private void assertLexicalAnalysisException(String expression, char illegalCharacter,
      int line, int pos) {
    try {
      generateLexer(expression, lexer -> {
        Token token;
        do {
          token = lexer.nextToken();
        } while (token.getType() != EOF);
      });
      Assert.fail("Expected a LexicalAnalysisException");
    } catch (LexicalAnalysisException e) {
      String message = e.getMessage();
      String illegalCharacterString = "'" + illegalCharacter + "'";
      assertThat(
          "Expected an error message containing the substring \"" + illegalCharacterString
              + "\" but found the following error message instead: " + message,
          message.indexOf(illegalCharacterString) != -1, equalTo(true));
      String positionString = "at line " + line + ", character " + pos;
      assertThat(
          "Expected an error message containing the substring \"" + positionString
              + "\" but found the following error message instead: " + message,
          message.indexOf(positionString) != -1, equalTo(true));

    }

  }

  private void generateLexer(String expression, Consumer<LexicalAnalyzer> assertions) {
    try (StringReader in = new StringReader(expression)) {
      LexicalAnalyzer lexer = new LexicalAnalyzer(in);
      assertions.accept(lexer);
    }

  }

}
