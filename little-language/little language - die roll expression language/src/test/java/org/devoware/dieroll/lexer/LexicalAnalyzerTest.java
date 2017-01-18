package org.devoware.dieroll.lexer;

import static org.devoware.dieroll.lexer.Token.Type.DIE;
import static org.devoware.dieroll.lexer.Token.Type.MINUS;
import static org.devoware.dieroll.lexer.Token.Type.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;

import java.io.StringReader;

import org.devoware.dieroll.lexer.Token.Type;
import org.junit.Test;

public class LexicalAnalyzerTest {

  @Test
  public void test_next_token () {
    LexicalAnalyzerFactory factory = LexicalAnalyzerFactory.create();
    
    String s = "-5 + 4D6DL1 - 2d20 kh 1";
    try (StringReader in = new StringReader(s)) {
      LexicalAnalyzer lexer = factory.create(in);

      assertToken(lexer, MINUS);
      assertNumber(lexer, 5);
      assertToken(lexer, PLUS);
      assertNumber(lexer, 4);
      assertToken(lexer, DIE);
      assertNumber(lexer, 6);
      assertToken(lexer, DROP_LOWEST);
      assertNumber(lexer, 1);
      assertToken(lexer, MINUS);
      assertNumber(lexer, 2);
      assertToken(lexer, DIE);
      assertNumber(lexer, 20);
      assertToken(lexer, KEEP_HIGHEST);
      assertNumber(lexer, 1);
      assertToken(lexer, EOF);
      assertToken(lexer, EOF);
    }
  }

  private void assertToken(LexicalAnalyzer lexer, Type expected) {
    assertThat(lexer.nextToken().getType(), equalTo(expected));
  }

  private void assertNumber(LexicalAnalyzer lexer, int expected) {
    Token t = lexer.nextToken();
    assertThat(t, instanceOf(NumberToken.class));
    assertThat(((NumberToken) t).getValue(), equalTo(expected));
  }

}
