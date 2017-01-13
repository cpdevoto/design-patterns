package org.devoware.simplesearch;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.devoware.simplesearch.lexer.LexicalAnalyzerFactory;
import org.devoware.simplesearch.model.Expression;
import org.devoware.simplesearch.parser.Parser;
import org.junit.Test;

public class IntegrationTest {

  @Test
  public void test_parse () {
    LexicalAnalyzerFactory factory = LexicalAnalyzerFactory.create();
    Parser parser = Parser.create(factory);

    // First expression
    Expression e = parser.parse("fox and not (brown or black)");

    assertTrue(e.search("The red fox raced across the road."));
    assertFalse(e.search("The Red Fox raced across the road."));           // search terms are case-sensitive!
    assertTrue(e.search("The Red Fox raced across the road.", true));  
    assertFalse(e.search("The brown fox raced across the road."));
    assertFalse(e.search("The black fox raced across the road."));
    assertFalse(e.search("The red cat raced across the road."));
    assertFalse(e.search("The brown cat raced across the road."));
    assertFalse(e.search("The black cat raced across the road."));
    assertFalse(e.search("Chris is a douche!"));
    
    // Second expression
    e = parser.parse("fox and not brown or black");

    assertTrue(e.search("The red fox raced across the road."));
    assertFalse(e.search("The brown fox raced across the road."));
    assertTrue(e.search("The black fox raced across the road."));
    assertFalse(e.search("The red cat raced across the road."));
    assertFalse(e.search("The brown cat raced across the road."));
    assertTrue(e.search("The black cat raced across the road."));
    assertFalse(e.search("Chris is a douche!"));

    // Third expression
    e = parser.parse("brown");

    assertFalse(e.search("The red fox raced across the road."));
    assertTrue(e.search("The brown fox raced across the road."));
    assertFalse(e.search("The black fox raced across the road."));
    assertFalse(e.search("The red cat raced across the road."));
    assertTrue(e.search("The brown cat raced across the road."));
    assertFalse(e.search("The black cat raced across the road."));
    assertFalse(e.search("Chris is a douche!"));
    
    // Fourth expression
    
    e = parser.parse("(brown or \"red fox\") and road");

    assertTrue(e.search("The red fox raced across the road."));
    assertTrue(e.search("The brown cat raced across the road."));
    assertFalse(e.search("The red little fox raced across the road."));
    assertFalse(e.search("The red fox raced across the street."));
  
  }

}
