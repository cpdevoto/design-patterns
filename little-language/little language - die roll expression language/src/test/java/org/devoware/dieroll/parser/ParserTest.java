package org.devoware.dieroll.parser;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.devoware.dieroll.lexer.LexicalAnalyzerFactory;
import org.devoware.dieroll.model.Dice;
import org.devoware.dieroll.model.Die;
import org.devoware.dieroll.model.DropLowest;
import org.devoware.dieroll.model.KeepHighest;
import org.devoware.dieroll.model.Minus;
import org.devoware.dieroll.model.Modifier;
import org.devoware.dieroll.model.Plus;
import org.devoware.dieroll.model.UnaryMinus;
import org.devoware.dieroll.model.ValueGenerator;
import org.junit.Test;

public class ParserTest {

  @Test
  public void test_parse() {
    LexicalAnalyzerFactory factory = LexicalAnalyzerFactory.create();
    Parser parser = Parser.create(factory);

    ValueGenerator gen = parser.parse("-5 + 4d6 dl 1 - (2d20 kh 1 + 4)");
    
    ValueGenerator expected = new Minus(
                                new Plus(
                                  new UnaryMinus(new Modifier(5)), 
                                  new DropLowest(new Dice(4, Die.D6), 1)
                                ), 
                                new Plus(
                                  new KeepHighest(new Dice(2, Die.D20), 1),
                                  new Modifier(4)
                                )
                              );
    assertThat(gen, equalTo(expected));
    
    gen = parser.parse("-(5 + 4d6dl1 - (2d20kh1 + 4))");
    expected = new UnaryMinus( 
                new Minus(
                  new Plus(
                    new Modifier(5), 
                    new DropLowest(new Dice(4, Die.D6), 1)
                  ), 
                  new Plus(
                    new KeepHighest(new Dice(2, Die.D20), 1),
                    new Modifier(4)
                  )
                )
              );
    
    assertThat(gen, equalTo(expected));
  }
}
