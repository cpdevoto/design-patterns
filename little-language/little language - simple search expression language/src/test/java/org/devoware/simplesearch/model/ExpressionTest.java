package org.devoware.simplesearch.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.devoware.simplesearch.model.AndExpression;
import org.devoware.simplesearch.model.Expression;
import org.devoware.simplesearch.model.NotExpression;
import org.devoware.simplesearch.model.OrExpression;
import org.devoware.simplesearch.model.WordExpression;
import org.junit.Test;

public class ExpressionTest {


  @Test
  public void test_word_expression() {
    WordExpression w = new WordExpression("fox");
    
    assertTrue(w.search("The red fox crossed the street."));
    assertTrue(w.search("The brown fox crossed the street."));
    assertFalse(w.search("The red Fox crossed the street."));
    assertTrue(w.search("The red Fox crossed the street.", true));
    
    w = new WordExpression("red fox");
    
    assertTrue(w.search("The red fox crossed the street."));
    assertFalse(w.search("The brown fox crossed the street."));
    assertFalse(w.search("The red Fox crossed the street."));
    assertTrue(w.search("The red Fox crossed the street.", true));
  }
  
  @Test
  public void test_not_expression() {
    NotExpression n = new NotExpression(new WordExpression("fox"));

    assertFalse(n.search("The red fox crossed the street."));
    assertFalse(n.search("The brown fox crossed the street."));
    assertTrue(n.search("The red Fox crossed the street."));
    assertFalse(n.search("The red Fox crossed the street.", true));
    
    n = new NotExpression(new WordExpression("red fox"));
    
    assertFalse(n.search("The red fox crossed the street."));
    assertTrue(n.search("The brown fox crossed the street."));
    assertTrue(n.search("The red Fox crossed the street."));
    assertFalse(n.search("The red Fox crossed the street.", true));
  
  }
  
  @Test
  public void test_and_expression() {
    AndExpression n = new AndExpression(new WordExpression("red"), new WordExpression("fox"));
    
    assertTrue(n.search("The red fox crossed the street."));
    assertFalse(n.search("The brown fox crossed the street."));
    assertFalse(n.search("The red Fox crossed the street."));
    assertTrue(n.search("The red Fox crossed the street.", true));
    
    n = new AndExpression(new WordExpression("red"), new NotExpression(new WordExpression("fox")));
    
    assertFalse(n.search("The red fox crossed the street."));
    assertFalse(n.search("The brown fox crossed the street."));
    assertTrue(n.search("The red Fox crossed the street."));
    assertFalse(n.search("The red Fox crossed the street.", true));
    
  }

  @Test
  public void test_or_expression() {
    OrExpression n = new OrExpression(new WordExpression("red"), new WordExpression("fox"));
    
    assertTrue(n.search("The red fox crossed the street."));
    assertTrue(n.search("The brown fox crossed the street."));
    assertTrue(n.search("The red Fox crossed the street."));
    assertFalse(n.search("The Red Fox crossed the street."));
    assertTrue(n.search("The red Fox crossed the street.", true));
    
    n = new OrExpression(new WordExpression("red"), new NotExpression(new WordExpression("fox")));
    
    assertTrue(n.search("The red fox crossed the street."));
    assertFalse(n.search("The brown fox crossed the street."));
    assertTrue(n.search("The red Fox crossed the street."));
    assertFalse(n.search("The Red fox crossed the street."));
    assertTrue(n.search("The Red fox crossed the street.", true));
    
  }
  
  @Test
  public void test_expressions() {
    // First expression: fox and not (brown or black)

    Expression e = new AndExpression(
                     new WordExpression("fox"), 
                     new NotExpression(
                       new OrExpression(
                           new WordExpression("brown"), 
                           new WordExpression("black")
                       )
                     )
                   );

    assertTrue(e.search("The red fox raced across the road."));
    assertFalse(e.search("The Red Fox raced across the road."));           // search terms are case-sensitive!
    assertTrue(e.search("The Red Fox raced across the road.", true));  
    assertFalse(e.search("The brown fox raced across the road."));
    assertFalse(e.search("The black fox raced across the road."));
    assertFalse(e.search("The red cat raced across the road."));
    assertFalse(e.search("The brown cat raced across the road."));
    assertFalse(e.search("The black cat raced across the road."));
    assertFalse(e.search("Chris is a douche!"));
    
    // Second expression: fox and not brown or black

    e = new OrExpression(
          new AndExpression(
            new WordExpression("fox"), 
            new NotExpression(
              new WordExpression("brown")
            )
          ), 
          new WordExpression("black")
        );

    assertTrue(e.search("The red fox raced across the road."));
    assertFalse(e.search("The brown fox raced across the road."));
    assertTrue(e.search("The black fox raced across the road."));
    assertFalse(e.search("The red cat raced across the road."));
    assertFalse(e.search("The brown cat raced across the road."));
    assertTrue(e.search("The black cat raced across the road."));
    assertFalse(e.search("Chris is a douche!"));

    // Third expression: brown
    e = new WordExpression("brown");

    assertFalse(e.search("The red fox raced across the road."));
    assertTrue(e.search("The brown fox raced across the road."));
    assertFalse(e.search("The black fox raced across the road."));
    assertFalse(e.search("The red cat raced across the road."));
    assertTrue(e.search("The brown cat raced across the road."));
    assertFalse(e.search("The black cat raced across the road."));
    assertFalse(e.search("Chris is a douche!"));
    
    // Fourth expression: (brown or "red fox") and road
    e = new AndExpression(
          new OrExpression(
            new WordExpression("brown"), 
            new WordExpression("red fox")
          ), 
          new WordExpression("road")
        );

    assertTrue(e.search("The red fox raced across the road."));
    assertTrue(e.search("The brown cat raced across the road."));
    assertFalse(e.search("The red little fox raced across the road."));
    assertFalse(e.search("The red fox raced across the street."));

  }
}
