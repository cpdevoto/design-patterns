package org.devoware.simplesearch.parser;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.devoware.simplesearch.lexer.LexicalAnalyzerFactory;
import org.devoware.simplesearch.model.AndExpression;
import org.devoware.simplesearch.model.Expression;
import org.devoware.simplesearch.model.NotExpression;
import org.devoware.simplesearch.model.OrExpression;
import org.devoware.simplesearch.model.WordExpression;
import org.devoware.simplesearch.parser.Parser;
import org.junit.Test;

public class ParserTest {

  @Test
  public void test_parse () {

    LexicalAnalyzerFactory factory = LexicalAnalyzerFactory.create();
    Parser parser = Parser.create(factory);
    
    Expression e = parser.parse("fox and not (brown or black)");


    Expression expected = new AndExpression(
                            new WordExpression("fox"), 
                            new NotExpression(
                              new OrExpression(
                                new WordExpression("brown"), 
                                new WordExpression("black")
                              )
                            )
                          );
      
    assertThat(e, equalTo(expected));  
  }
  
}
