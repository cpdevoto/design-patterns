package org.devoware.dicegolem.dice;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.StringReader;

import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;

public class LexicalAnalyzerTest {

  @Test
  public void should_recognize_number_token() {
    // Given
    try (StringReader reader = new StringReader("1")) {
      LexicalAnalyzer lex = new LexicalAnalyzerImpl(reader);

      // When
      Token tok = lex.nextToken();

      // Then
      assertThat(tok)
          .isNotNull()
          .isInstanceOfSatisfying(NumberToken.class, t -> {
            assertThat(t.getValue()).isEqualTo(1);
            assertThat(t.getType()).isEqualTo(Token.Type.NUMBER);
            assertThat(t.getPosition()).satisfies(p -> {
              assertThat(p.getLine()).isEqualTo(1);
              assertThat(p.getCharacter()).isEqualTo(1);
            });
          });
    }
  }

  @Test
  public void should_recognize_negative_number_as_two_tokens() {
    // Given
    try (StringReader reader = new StringReader("-1")) {
      LexicalAnalyzer lex = new LexicalAnalyzerImpl(reader);

      // When
      Token tok = lex.nextToken();

      // Then
      assertThat(tok)
          .isNotNull()
          .satisfies(t -> {
            assertThat(t.getType()).isEqualTo(Token.Type.MINUS);
            assertThat(t.getPosition()).satisfies(p -> {
              assertThat(p.getLine()).isEqualTo(1);
              assertThat(p.getCharacter()).isEqualTo(1);
            });
          });

      // When
      tok = lex.nextToken();

      // Then
      assertThat(tok)
          .isNotNull()
          .isInstanceOfSatisfying(NumberToken.class, t -> {
            assertThat(t.getValue()).isEqualTo(1);
            assertThat(t.getType()).isEqualTo(Token.Type.NUMBER);
            assertThat(t.getPosition()).satisfies(p -> {
              assertThat(p.getLine()).isEqualTo(1);
              assertThat(p.getCharacter()).isEqualTo(2);
            });
          });
    }
  }

  @Test
  public void should_recognize_multi_digit_number_token() {
    // Given
    try (StringReader reader = new StringReader("12345")) {
      LexicalAnalyzer lex = new LexicalAnalyzerImpl(reader);

      // When
      Token tok = lex.nextToken();

      // Then
      assertThat(tok)
          .isNotNull()
          .isInstanceOfSatisfying(NumberToken.class, t -> {
            assertThat(t.getValue()).isEqualTo(12345);
            assertThat(t.getType()).isEqualTo(Token.Type.NUMBER);
            assertThat(t.getPosition()).satisfies(p -> {
              assertThat(p.getLine()).isEqualTo(1);
              assertThat(p.getCharacter()).isEqualTo(1);
            });
          });
    }
  }

  @Test
  public void should_recognize_lowercase_die_token() {
    // Given
    try (StringReader reader = new StringReader("d")) {
      LexicalAnalyzer lex = new LexicalAnalyzerImpl(reader);

      // When
      Token tok = lex.nextToken();

      // Then
      assertThat(tok)
          .isNotNull()
          .satisfies(t -> {
            assertThat(t.getType()).isEqualTo(Token.Type.DIE);
            assertThat(t.getPosition()).satisfies(p -> {
              assertThat(p.getLine()).isEqualTo(1);
              assertThat(p.getCharacter()).isEqualTo(1);
            });
          });
    }
  }

  @Test
  public void should_recognize_uppercase_die_token() {
    // Given
    try (StringReader reader = new StringReader("D")) {
      LexicalAnalyzer lex = new LexicalAnalyzerImpl(reader);

      // When
      Token tok = lex.nextToken();

      // Then
      assertThat(tok)
          .isNotNull()
          .satisfies(t -> {
            assertThat(t.getType()).isEqualTo(Token.Type.DIE);
            assertThat(t.getPosition()).satisfies(p -> {
              assertThat(p.getLine()).isEqualTo(1);
              assertThat(p.getCharacter()).isEqualTo(1);
            });
          });
    }
  }

  @Test
  public void should_recognize_left_paren_token() {
    // Given
    try (StringReader reader = new StringReader("(")) {
      LexicalAnalyzer lex = new LexicalAnalyzerImpl(reader);

      // When
      Token tok = lex.nextToken();

      // Then
      assertThat(tok)
          .isNotNull()
          .satisfies(t -> {
            assertThat(t.getType()).isEqualTo(Token.Type.LEFT_PAREN);
            assertThat(t.getPosition()).satisfies(p -> {
              assertThat(p.getLine()).isEqualTo(1);
              assertThat(p.getCharacter()).isEqualTo(1);
            });
          });
    }
  }

  @Test
  public void should_recognize_right_paren_token() {
    // Given
    try (StringReader reader = new StringReader(")")) {
      LexicalAnalyzer lex = new LexicalAnalyzerImpl(reader);

      // When
      Token tok = lex.nextToken();

      // Then
      assertThat(tok)
          .isNotNull()
          .satisfies(t -> {
            assertThat(t.getType()).isEqualTo(Token.Type.RIGHT_PAREN);
            assertThat(t.getPosition()).satisfies(p -> {
              assertThat(p.getLine()).isEqualTo(1);
              assertThat(p.getCharacter()).isEqualTo(1);
            });
          });
    }
  }

  @Test
  public void should_recognize_multiply_token() {
    // Given
    try (StringReader reader = new StringReader("*")) {
      LexicalAnalyzer lex = new LexicalAnalyzerImpl(reader);

      // When
      Token tok = lex.nextToken();

      // Then
      assertThat(tok)
          .isNotNull()
          .satisfies(t -> {
            assertThat(t.getType()).isEqualTo(Token.Type.MULTIPLY);
            assertThat(t.getPosition()).satisfies(p -> {
              assertThat(p.getLine()).isEqualTo(1);
              assertThat(p.getCharacter()).isEqualTo(1);
            });
          });
    }
  }

  @Test
  public void should_recognize_divide_token() {
    // Given
    try (StringReader reader = new StringReader("/")) {
      LexicalAnalyzer lex = new LexicalAnalyzerImpl(reader);

      // When
      Token tok = lex.nextToken();

      // Then
      assertThat(tok)
          .isNotNull()
          .satisfies(t -> {
            assertThat(t.getType()).isEqualTo(Token.Type.DIVIDE);
            assertThat(t.getPosition()).satisfies(p -> {
              assertThat(p.getLine()).isEqualTo(1);
              assertThat(p.getCharacter()).isEqualTo(1);
            });
          });
    }
  }


  @Test
  public void should_recognize_plus_token() {
    // Given
    try (StringReader reader = new StringReader("+")) {
      LexicalAnalyzer lex = new LexicalAnalyzerImpl(reader);

      // When
      Token tok = lex.nextToken();

      // Then
      assertThat(tok)
          .isNotNull()
          .satisfies(t -> {
            assertThat(t.getType()).isEqualTo(Token.Type.PLUS);
            assertThat(t.getPosition()).satisfies(p -> {
              assertThat(p.getLine()).isEqualTo(1);
              assertThat(p.getCharacter()).isEqualTo(1);
            });
          });
    }
  }

  @Test
  public void should_recognize_minus_token() {
    // Given
    try (StringReader reader = new StringReader("-")) {
      LexicalAnalyzer lex = new LexicalAnalyzerImpl(reader);

      // When
      Token tok = lex.nextToken();

      // Then
      assertThat(tok)
          .isNotNull()
          .satisfies(t -> {
            assertThat(t.getType()).isEqualTo(Token.Type.MINUS);
            assertThat(t.getPosition()).satisfies(p -> {
              assertThat(p.getLine()).isEqualTo(1);
              assertThat(p.getCharacter()).isEqualTo(1);
            });
          });
    }
  }

  @Test
  public void should_recognize_eos() {
    // Given
    try (StringReader reader = new StringReader("1")) {
      LexicalAnalyzer lex = new LexicalAnalyzerImpl(reader);

      // When
      Token tok = lex.nextToken();

      // Then
      assertThat(tok)
          .isNotNull()
          .isInstanceOfSatisfying(NumberToken.class, t -> {
            assertThat(t.getValue()).isEqualTo(1);
            assertThat(t.getType()).isEqualTo(Token.Type.NUMBER);
            assertThat(t.getPosition()).satisfies(p -> {
              assertThat(p.getLine()).isEqualTo(1);
              assertThat(p.getCharacter()).isEqualTo(1);
            });
          });

      // When
      tok = lex.nextToken();

      // Then
      assertThat(tok)
          .isNotNull()
          .satisfies(t -> {
            assertThat(t.getType()).isEqualTo(Token.Type.EOS);
            assertThat(t.getPosition()).satisfies(p -> {
              assertThat(p.getLine()).isEqualTo(1);
              assertThat(p.getCharacter()).isEqualTo(2);
            });
          });

    }
  }


  @Test
  public void should_generate_eos_token_when_expression_is_blank() {
    // Given
    try (StringReader reader = new StringReader("  ")) {
      LexicalAnalyzer lex = new LexicalAnalyzerImpl(reader);

      // When
      Token tok = lex.nextToken();

      // Then
      assertThat(tok)
          .isNotNull()
          .satisfies(t -> {
            assertThat(t.getType()).isEqualTo(Token.Type.EOS);
            assertThat(t.getPosition()).satisfies(p -> {
              assertThat(p.getLine()).isEqualTo(1);
              assertThat(p.getCharacter()).isEqualTo(3);
            });
          });

    }
  }

  @Test
  public void should_generate_eos_token_when_expression_is_empty() {
    // Given
    try (StringReader reader = new StringReader("")) {
      LexicalAnalyzer lex = new LexicalAnalyzerImpl(reader);

      // When
      Token tok = lex.nextToken();

      // Then
      assertThat(tok)
          .isNotNull()
          .satisfies(t -> {
            assertThat(t.getType()).isEqualTo(Token.Type.EOS);
            assertThat(t.getPosition()).satisfies(p -> {
              assertThat(p.getLine()).isEqualTo(1);
              assertThat(p.getCharacter()).isEqualTo(1);
            });
          });
    }
  }

  @Test
  public void should_generate_eos_token_again_when_nextToken_called_after_eos_reached() {
    // Given
    try (StringReader reader = new StringReader("")) {
      LexicalAnalyzer lex = new LexicalAnalyzerImpl(reader);

      // When
      Token tok = lex.nextToken();

      // Then
      assertThat(tok)
          .isNotNull()
          .satisfies(t -> {
            assertThat(t.getType()).isEqualTo(Token.Type.EOS);
            assertThat(t.getPosition()).satisfies(p -> {
              assertThat(p.getLine()).isEqualTo(1);
              assertThat(p.getCharacter()).isEqualTo(1);
            });
          });

      // When
      tok = lex.nextToken(); // Calling nextToken again after EOS reached!

      // Then
      assertThat(tok)
          .isNotNull()
          .satisfies(t -> {
            assertThat(t.getType()).isEqualTo(Token.Type.EOS);
            assertThat(t.getPosition()).satisfies(p -> {
              assertThat(p.getLine()).isEqualTo(1);
              assertThat(p.getCharacter()).isEqualTo(1);
            });
          });

    }
  }

  @Test
  public void should_ignore_whitespace_except_in_terms_of_tracking_position() {
    // Given
    try (StringReader reader = new StringReader("   -         1    ")) {
      LexicalAnalyzer lex = new LexicalAnalyzerImpl(reader);

      // When
      Token tok = lex.nextToken();

      // Then
      assertThat(tok)
          .isNotNull()
          .satisfies(t -> {
            assertThat(t.getType()).isEqualTo(Token.Type.MINUS);
            assertThat(t.getPosition()).satisfies(p -> {
              assertThat(p.getLine()).isEqualTo(1);
              assertThat(p.getCharacter()).isEqualTo(4);
            });
          });

      // When
      tok = lex.nextToken();

      // Then
      assertThat(tok)
          .isNotNull()
          .isInstanceOfSatisfying(NumberToken.class, t -> {
            assertThat(t.getValue()).isEqualTo(1);
            assertThat(t.getType()).isEqualTo(Token.Type.NUMBER);
            assertThat(t.getPosition()).satisfies(p -> {
              assertThat(p.getLine()).isEqualTo(1);
              assertThat(p.getCharacter()).isEqualTo(14);
            });
          });

      // When
      tok = lex.nextToken();

      // Then
      assertThat(tok)
          .isNotNull()
          .satisfies(t -> {
            assertThat(t.getType()).isEqualTo(Token.Type.EOS);
            assertThat(t.getPosition()).satisfies(p -> {
              assertThat(p.getLine()).isEqualTo(1);
              assertThat(p.getCharacter()).isEqualTo(19);
            });
          });

    }
  }

  @Test
  public void should_reflect_line_breaks_in_position_line_number() {
    // Given
    try (StringReader reader = new StringReader("\n\n+")) {
      LexicalAnalyzer lex = new LexicalAnalyzerImpl(reader);

      // When
      Token tok = lex.nextToken();

      // Then
      assertThat(tok)
          .isNotNull()
          .satisfies(t -> {
            assertThat(t.getType()).isEqualTo(Token.Type.PLUS);
            assertThat(t.getPosition()).satisfies(p -> {
              assertThat(p.getLine()).isEqualTo(3);
              assertThat(p.getCharacter()).isEqualTo(1);
            });
          });
    }

  }

  @Test
  public void should_throw_exception_when_expression_contains_invalid_token() {
    // Given
    try (StringReader reader = new StringReader(" 1b4")) {
      LexicalAnalyzer lex = new LexicalAnalyzerImpl(reader);

      // When
      Token tok = lex.nextToken();

      // Then
      assertThat(tok)
          .isNotNull()
          .isInstanceOfSatisfying(NumberToken.class, t -> {
            assertThat(t.getValue()).isEqualTo(1);
          });

      // When
      ThrowingCallable statement = () -> lex.nextToken();

      // Then
      assertThatThrownBy(statement)
          .isInstanceOfSatisfying(LexicalAnalysisException.class, ex -> {
            assertThat(ex.getCharacter()).isEqualTo('b');
            assertThat(ex.getPosition()).satisfies(pos -> {
              assertThat(pos.getLine()).isEqualTo(1);
              assertThat(pos.getCharacter()).isEqualTo(3);
            });
          });
    }
  }

}

