package org.devoware.simplesearch.lexer;

import static org.devoware.simplesearch.lexer.Token.Type.AND;
import static org.devoware.simplesearch.lexer.Token.Type.EOF;
import static org.devoware.simplesearch.lexer.Token.Type.LEFT_PAREN;
import static org.devoware.simplesearch.lexer.Token.Type.NOT;
import static org.devoware.simplesearch.lexer.Token.Type.OR;
import static org.devoware.simplesearch.lexer.Token.Type.RIGHT_PAREN;
import static org.devoware.simplesearch.lexer.Token.Type.WORD;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.devoware.simplesearch.lexer.LexicalAnalyzer;
import org.devoware.simplesearch.lexer.LexicalAnalyzerFactory;
import org.devoware.simplesearch.lexer.Token;
import org.junit.Test;

public class LexicalAnalyzerTest {

  @Test
  public void test_lexical_analysis () throws IOException {
    
    String s = "fox ANd \r\n\t nOt (brown oR black)";
    LexicalAnalyzerFactory factory = LexicalAnalyzerFactory.create();
    
    try (Reader in = new StringReader(s)) {
      LexicalAnalyzer lex = factory.create(in);
      
      Token tok = lex.nextToken();
      assertThat(tok.getType(), equalTo(WORD));
      assertTrue(tok.hasLexeme());
      assertThat(tok.getLexeme(), equalTo("fox"));
      
      tok = lex.nextToken();
      assertThat(tok.getType(), equalTo(AND));
      assertTrue(tok.hasLexeme());
      assertThat(tok.getLexeme(), equalTo("ANd"));
      
      tok = lex.nextToken();
      assertThat(tok.getType(), equalTo(NOT));
      assertTrue(tok.hasLexeme());
      assertThat(tok.getLexeme(), equalTo("nOt"));

      tok = lex.nextToken();
      assertThat(tok.getType(), equalTo(LEFT_PAREN));
      assertTrue(tok.hasLexeme());
      assertThat(tok.getLexeme(), equalTo("("));
    
      tok = lex.nextToken();
      assertThat(tok.getType(), equalTo(WORD));
      assertTrue(tok.hasLexeme());
      assertThat(tok.getLexeme(), equalTo("brown"));
      
      tok = lex.nextToken();
      assertThat(tok.getType(), equalTo(OR));
      assertTrue(tok.hasLexeme());
      assertThat(tok.getLexeme(), equalTo("oR"));

      tok = lex.nextToken();
      assertThat(tok.getType(), equalTo(WORD));
      assertTrue(tok.hasLexeme());
      assertThat(tok.getLexeme(), equalTo("black"));
    
      tok = lex.nextToken();
      assertThat(tok.getType(), equalTo(RIGHT_PAREN));
      assertTrue(tok.hasLexeme());
      assertThat(tok.getLexeme(), equalTo(")"));
      
      tok = lex.nextToken();
      assertThat(tok.getType(), equalTo(EOF));
      assertFalse(tok.hasLexeme());

      tok = lex.nextToken();
      assertThat(tok.getType(), equalTo(EOF));
      assertFalse(tok.hasLexeme());
    }
  }

}
