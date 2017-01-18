package org.devoware.dieroll.parser;

import static java.util.Objects.requireNonNull;
import static org.devoware.dieroll.lexer.Token.Type.DIE;
import static org.devoware.dieroll.lexer.Token.Type.DROP_HIGHEST;
import static org.devoware.dieroll.lexer.Token.Type.DROP_LOWEST;
import static org.devoware.dieroll.lexer.Token.Type.EOF;
import static org.devoware.dieroll.lexer.Token.Type.KEEP_HIGHEST;
import static org.devoware.dieroll.lexer.Token.Type.KEEP_LOWEST;
import static org.devoware.dieroll.lexer.Token.Type.LEFT_PAREN;
import static org.devoware.dieroll.lexer.Token.Type.MINUS;
import static org.devoware.dieroll.lexer.Token.Type.NUMBER;
import static org.devoware.dieroll.lexer.Token.Type.PLUS;
import static org.devoware.dieroll.lexer.Token.Type.RIGHT_PAREN;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.devoware.dieroll.lexer.LexicalAnalyzer;
import org.devoware.dieroll.lexer.LexicalAnalyzerFactory;
import org.devoware.dieroll.lexer.NumberToken;
import org.devoware.dieroll.lexer.Token;
import org.devoware.dieroll.lexer.Token.Type;
import org.devoware.dieroll.model.Dice;
import org.devoware.dieroll.model.Die;
import org.devoware.dieroll.model.DropHighest;
import org.devoware.dieroll.model.DropLowest;
import org.devoware.dieroll.model.KeepHighest;
import org.devoware.dieroll.model.KeepLowest;
import org.devoware.dieroll.model.Minus;
import org.devoware.dieroll.model.Modifier;
import org.devoware.dieroll.model.Plus;
import org.devoware.dieroll.model.UnaryMinus;
import org.devoware.dieroll.model.ValueGenerator;

public class ParserImpl implements Parser {

  private final LexicalAnalyzerFactory factory;
  private LexicalAnalyzer lexer;
  private Token token;
  
  static ParserImpl create (LexicalAnalyzerFactory factory) {
    return new ParserImpl(factory);
  }

  private ParserImpl(LexicalAnalyzerFactory factory) {
    this.factory = requireNonNull(factory, "factory cannot be null");
  }
  
  @Override
  public ValueGenerator parse(String expression) {
    try (Reader in = new StringReader(expression)) {
      return parse(in);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public ValueGenerator parse(Reader in) {
    lexer = this.factory.create(in);
    nextToken();
    ValueGenerator c = binaryCombination();
    expect(EOF);
    return c;
  }

  private ValueGenerator binaryCombination() {
    ValueGenerator c = unaryCombination();
    while (token.getType() == PLUS || token.getType() == MINUS) {
      if (token.getType() == PLUS) {
        nextToken();
        c = new Plus(c, unaryCombination());
      } else {
        nextToken();
        c = new Minus(c, unaryCombination());
      }
    }
    return c;
  }

  private ValueGenerator unaryCombination() {
    if (token.getType() == MINUS) {
      nextToken();
      return new UnaryMinus(simpleCombination());
    }
    return simpleCombination();
  }

  private ValueGenerator simpleCombination() {
    if (token.getType() == LEFT_PAREN) {
      nextToken();
      ValueGenerator c = binaryCombination();
      expect(RIGHT_PAREN);
      nextToken();
      return c;
    }
    return terminalCombination();
  }

  private ValueGenerator terminalCombination() {
    if (token.getType() != NUMBER) {
      expect(NUMBER);
    }
    int value = ((NumberToken) token).getValue();
    nextToken();
    if (token.getType() != DIE) {
      return new Modifier(value);
    }
    nextToken();
    if (token.getType() != NUMBER) {
      expect(NUMBER);
    }
    int dieType = ((NumberToken) token).getValue();
    Die die = Die.get(dieType);
    if (die == null) {
      throw new SyntaxException("Invalid die type: " + dieType);
    }
    ValueGenerator c = new Dice(value, die);
    nextToken();
    if (token.getType() == DROP_HIGHEST || token.getType() == DROP_LOWEST ||
        token.getType() == KEEP_HIGHEST || token.getType() == KEEP_LOWEST) {
      Type selector = token.getType();
      nextToken();
      if (token.getType() != NUMBER) {
        expect(NUMBER);
      }
      int numDice = ((NumberToken) token).getValue();
      switch (selector) {
        case DROP_HIGHEST:
          c = new DropHighest((Dice) c, numDice);
          break;
        case DROP_LOWEST:
          c = new DropLowest((Dice) c, numDice);
          break;
        case KEEP_HIGHEST:
          c = new KeepHighest((Dice) c, numDice);
          break;
        case KEEP_LOWEST:
          c = new KeepLowest((Dice) c, numDice);
          break;
        default:
          // This can never happen because of the surrounding if clause
          throw new AssertionError("Unexpected type: " + selector);
      }
      nextToken();
    }
    return c;
  }

  private void nextToken () {
    token = lexer.nextToken();
  }

  private void expect(Type type) {
    if (token.getType() != type) {
      throw new SyntaxException("found " + token.getType() + " when expecting " + type);
    }
  }

}
