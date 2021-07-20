package com.resolute.search;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.StringReader;

import org.junit.jupiter.api.Test;

public class LexicalAnalyzerTest {

  @Test
  public void test_complex_expression() {
    String s = "fox and not\n  (brown or black)";
    StringReader in = new StringReader(s);
    LexicalAnalyzer lexer = new LexicalAnalyzer(in);

    validateTokens(lexer);
  }

  @Test
  public void test_case_insensitivity() {
    String s = "fox AND nOt\n  (brown Or black)";
    StringReader in = new StringReader(s);
    LexicalAnalyzer lexer = new LexicalAnalyzer(in);

    validateTokens(lexer);
  }

  private void validateTokens(LexicalAnalyzer lexer) {
    // Validate next token: fox
    assertThat(lexer.nextToken()).satisfies(tok -> {
      assertThat(tok.getType()).isEqualTo(TokenType.WORD);
      assertThat(tok.getLexeme()).hasValue("fox");
      assertThat(tok.getPosition()).satisfies(pos -> {
        assertThat(pos.getLine()).isEqualTo(1);
        assertThat(pos.getCharacter()).isEqualTo(1);
      });
    });

    // Validate next token: and
    assertThat(lexer.nextToken()).satisfies(tok -> {
      assertThat(tok.getType()).isEqualTo(TokenType.AND);
      assertThat(tok.getLexeme()).isEmpty();
      assertThat(tok.getPosition()).satisfies(pos -> {
        assertThat(pos.getLine()).isEqualTo(1);
        assertThat(pos.getCharacter()).isEqualTo(5);
      });
    });

    // Validate next token: not
    assertThat(lexer.nextToken()).satisfies(tok -> {
      assertThat(tok.getType()).isEqualTo(TokenType.NOT);
      assertThat(tok.getLexeme()).isEmpty();
      assertThat(tok.getPosition()).satisfies(pos -> {
        assertThat(pos.getLine()).isEqualTo(1);
        assertThat(pos.getCharacter()).isEqualTo(9);
      });
    });

    // Validate next token: left paren
    assertThat(lexer.nextToken()).satisfies(tok -> {
      assertThat(tok.getType()).isEqualTo(TokenType.LEFT_PAREN);
      assertThat(tok.getLexeme()).isEmpty();
      assertThat(tok.getPosition()).satisfies(pos -> {
        assertThat(pos.getLine()).isEqualTo(2);
        assertThat(pos.getCharacter()).isEqualTo(3);
      });
    });

    // Validate next token: brown
    assertThat(lexer.nextToken()).satisfies(tok -> {
      assertThat(tok.getType()).isEqualTo(TokenType.WORD);
      assertThat(tok.getLexeme()).hasValue("brown");
      assertThat(tok.getPosition()).satisfies(pos -> {
        assertThat(pos.getLine()).isEqualTo(2);
        assertThat(pos.getCharacter()).isEqualTo(4);
      });
    });

    // Validate next token: or
    assertThat(lexer.nextToken()).satisfies(tok -> {
      assertThat(tok.getType()).isEqualTo(TokenType.OR);
      assertThat(tok.getLexeme()).isEmpty();
      assertThat(tok.getPosition()).satisfies(pos -> {
        assertThat(pos.getLine()).isEqualTo(2);
        assertThat(pos.getCharacter()).isEqualTo(10);
      });
    });

    // Validate next token: black
    assertThat(lexer.nextToken()).satisfies(tok -> {
      assertThat(tok.getType()).isEqualTo(TokenType.WORD);
      assertThat(tok.getLexeme()).hasValue("black");
      assertThat(tok.getPosition()).satisfies(pos -> {
        assertThat(pos.getLine()).isEqualTo(2);
        assertThat(pos.getCharacter()).isEqualTo(13);
      });
    });

    // Validate next token: right paren
    assertThat(lexer.nextToken()).satisfies(tok -> {
      assertThat(tok.getType()).isEqualTo(TokenType.RIGHT_PAREN);
      assertThat(tok.getLexeme()).isEmpty();
      assertThat(tok.getPosition()).satisfies(pos -> {
        assertThat(pos.getLine()).isEqualTo(2);
        assertThat(pos.getCharacter()).isEqualTo(18);
      });
    });
  }


}
