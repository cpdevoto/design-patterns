package org.dicegolem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
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

import java.io.StringReader;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;

public class LexicalAnalyzerTest {

  @Test
  public void test_valid_expression() {

    String expression = "4d6kh3ro<2 + (2D4KL1 - 2D20dL1 + 3d20Dh2)";

    generateLexer(expression, lexer -> {
      Token token;
      NumberToken numToken;

      // Token 1: NUMBER(4)
      token = lexer.nextToken();
      assertThat(token).isNotNull();
      assertThat(token.getType()).isEqualTo(NUMBER);
      numToken = NumberToken.class.cast(token);
      assertThat(numToken.getValue()).isEqualTo(4);

      // Token 2: DIE
      token = lexer.nextToken();
      assertThat(token).isNotNull();
      assertThat(token.getType()).isEqualTo(DIE);

      // Token 3: NUMBER(6)
      token = lexer.nextToken();
      assertThat(token).isNotNull();
      assertThat(token.getType()).isEqualTo(NUMBER);
      numToken = NumberToken.class.cast(token);
      assertThat(numToken.getValue()).isEqualTo(6);

      // Token 4: KEEP_HIGHEST
      token = lexer.nextToken();
      assertThat(token).isNotNull();
      assertThat(token.getType()).isEqualTo(KEEP_HIGHEST);

      // Token 5: NUMBER(3)
      token = lexer.nextToken();
      assertThat(token).isNotNull();
      assertThat(token.getType()).isEqualTo(NUMBER);
      numToken = NumberToken.class.cast(token);
      assertThat(numToken.getValue()).isEqualTo(3);

      // Token 6: REROLL_ONCE
      token = lexer.nextToken();
      assertThat(token).isNotNull();
      assertThat(token.getType()).isEqualTo(REROLL_ONCE);

      // Token 7: NUMBER(2)
      token = lexer.nextToken();
      assertThat(token).isNotNull();
      assertThat(token.getType()).isEqualTo(NUMBER);
      numToken = NumberToken.class.cast(token);
      assertThat(numToken.getValue()).isEqualTo(2);

      // Token 8: PLUS
      token = lexer.nextToken();
      assertThat(token).isNotNull();
      assertThat(token.getType()).isEqualTo(PLUS);

      // Token 9: LEFT_PAREN
      token = lexer.nextToken();
      assertThat(token).isNotNull();
      assertThat(token.getType()).isEqualTo(LEFT_PAREN);

      // Token 10: NUMBER(2)
      token = lexer.nextToken();
      assertThat(token).isNotNull();
      assertThat(token.getType()).isEqualTo(NUMBER);
      numToken = NumberToken.class.cast(token);
      assertThat(numToken.getValue()).isEqualTo(2);

      // Token 11: DIE
      token = lexer.nextToken();
      assertThat(token).isNotNull();
      assertThat(token.getType()).isEqualTo(DIE);

      // Token 12: NUMBER(4)
      token = lexer.nextToken();
      assertThat(token).isNotNull();
      assertThat(token.getType()).isEqualTo(NUMBER);
      numToken = NumberToken.class.cast(token);
      assertThat(numToken.getValue()).isEqualTo(4);

      // Token 13: KEEP_LOWEST
      token = lexer.nextToken();
      assertThat(token).isNotNull();
      assertThat(token.getType()).isEqualTo(KEEP_LOWEST);

      // Token 14: NUMBER(1)
      token = lexer.nextToken();
      assertThat(token).isNotNull();
      assertThat(token.getType()).isEqualTo(NUMBER);
      numToken = NumberToken.class.cast(token);
      assertThat(numToken.getValue()).isEqualTo(1);

      // Token 15: MINUS
      token = lexer.nextToken();
      assertThat(token).isNotNull();
      assertThat(token.getType()).isEqualTo(MINUS);

      // Token 16: NUMBER(2)
      token = lexer.nextToken();
      assertThat(token).isNotNull();
      assertThat(token.getType()).isEqualTo(NUMBER);
      numToken = NumberToken.class.cast(token);
      assertThat(numToken.getValue()).isEqualTo(2);

      // Token 17: DIE
      token = lexer.nextToken();
      assertThat(token).isNotNull();
      assertThat(token.getType()).isEqualTo(DIE);

      // Token 18: NUMBER(20)
      token = lexer.nextToken();
      assertThat(token).isNotNull();
      assertThat(token.getType()).isEqualTo(NUMBER);
      numToken = NumberToken.class.cast(token);
      assertThat(numToken.getValue()).isEqualTo(20);

      // Token 19: DROP_LOWEST
      token = lexer.nextToken();
      assertThat(token).isNotNull();
      assertThat(token.getType()).isEqualTo(DROP_LOWEST);

      // Token 20: NUMBER(1)
      token = lexer.nextToken();
      assertThat(token).isNotNull();
      assertThat(token.getType()).isEqualTo(NUMBER);
      numToken = NumberToken.class.cast(token);
      assertThat(numToken.getValue()).isEqualTo(1);

      // Token 21: PLUS
      token = lexer.nextToken();
      assertThat(token).isNotNull();
      assertThat(token.getType()).isEqualTo(PLUS);

      // Token 22: NUMBER(3)
      token = lexer.nextToken();
      assertThat(token).isNotNull();
      assertThat(token.getType()).isEqualTo(NUMBER);
      numToken = NumberToken.class.cast(token);
      assertThat(numToken.getValue()).isEqualTo(3);

      // Token 23: DIE
      token = lexer.nextToken();
      assertThat(token).isNotNull();
      assertThat(token.getType()).isEqualTo(DIE);

      // Token 24: NUMBER(20)
      token = lexer.nextToken();
      assertThat(token).isNotNull();
      assertThat(token.getType()).isEqualTo(NUMBER);
      numToken = NumberToken.class.cast(token);
      assertThat(numToken.getValue()).isEqualTo(20);

      // Token 25: DROP_HIGHEST
      token = lexer.nextToken();
      assertThat(token).isNotNull();
      assertThat(token.getType()).isEqualTo(DROP_HIGHEST);

      // Token 26: NUMBER(2)
      token = lexer.nextToken();
      assertThat(token).isNotNull();
      assertThat(token.getType()).isEqualTo(NUMBER);
      numToken = NumberToken.class.cast(token);
      assertThat(numToken.getValue()).isEqualTo(2);

      // Token 27: RIGHT_PAREN
      token = lexer.nextToken();
      assertThat(token).isNotNull();
      assertThat(token.getType()).isEqualTo(RIGHT_PAREN);

      // Token 28: EOF
      token = lexer.nextToken();
      assertThat(token).isNotNull();
      assertThat(token.getType()).isEqualTo(EOF);

      // Test that nextToken() just keeps returning EOF
      token = lexer.nextToken();
      assertThat(token).isNotNull();
      assertThat(token.getType()).isEqualTo(EOF);
      token = lexer.nextToken();
      assertThat(token).isNotNull();
      assertThat(token.getType()).isEqualTo(EOF);

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
      fail("Expected a LexicalAnalysisException");
    } catch (LexicalAnalysisException e) {
      String actualMessage = e.getMessage();

      String expectedIllegalCharacterString = "'" + illegalCharacter + "'";
      assertThat(actualMessage.indexOf(expectedIllegalCharacterString)).isNotEqualTo(-1)
          .as("Expected an error message containing the substring \"%s\" but found the following error message instead: %s",
              expectedIllegalCharacterString, actualMessage);

      String expectedPositionString = "at line " + line + ", character " + pos;
      assertThat(actualMessage.indexOf(expectedPositionString)).isNotEqualTo(-1)
          .as("Expected an error message containing the substring \"%s\" but found the following error message instead: %s",
              expectedPositionString, actualMessage);

    }

  }

  private void generateLexer(String expression, Consumer<LexicalAnalyzer> assertions) {
    try (StringReader in = new StringReader(expression)) {
      LexicalAnalyzer lexer = new LexicalAnalyzer(in);
      assertions.accept(lexer);
    }

  }

}
