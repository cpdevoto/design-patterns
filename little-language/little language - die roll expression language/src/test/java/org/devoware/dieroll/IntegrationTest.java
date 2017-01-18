package org.devoware.dieroll;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import org.devoware.dieroll.lexer.LexicalAnalyzerFactory;
import org.devoware.dieroll.model.ValueGenerator;
import org.devoware.dieroll.parser.Parser;
import org.junit.Test;

public class IntegrationTest {

  @Test
  public void test_parse() {
    LexicalAnalyzerFactory factory = LexicalAnalyzerFactory.create();
    Parser parser = Parser.create(factory);

    ValueGenerator gen = parser.parse("4d6 dl 1");
    
    for (int i = 0; i < 10000; i++) {
      int value = gen.value();
      assertThat(value, greaterThanOrEqualTo(3));
      assertThat(value, lessThanOrEqualTo(18));
    }
    
    gen = parser.parse("2d20 kh 1 + 5");
    
    for (int i = 0; i < 10000; i++) {
      int value = gen.value();
      assertThat(value, greaterThanOrEqualTo(6));
      assertThat(value, lessThanOrEqualTo(25));
    }
    
  }

}
