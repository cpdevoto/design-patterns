package org.devoware.dice;

import static org.devoware.dice.TokenType.DIE;
import static org.devoware.dice.TokenType.EOE;
import static org.devoware.dice.TokenType.FLOAT;
import static org.devoware.dice.TokenType.LEFT_PAREN;
import static org.devoware.dice.TokenType.MINUS;
import static org.devoware.dice.TokenType.MUTIPLY;
import static org.devoware.dice.TokenType.NO_CRIT;
import static org.devoware.dice.TokenType.NUMBER;
import static org.devoware.dice.TokenType.PLUS;
import static org.devoware.dice.TokenType.RIGHT_PAREN;

import java.io.StringReader;

class Parser {
  private final LexicalAnalyzer lexer;
  private Token<?> token;

  static DieRollExpression parse(String expression) {
    try (StringReader reader = new StringReader(expression)) {
      LexicalAnalyzer lexer = new LexicalAnalyzer(reader);
      Parser parser = new Parser(lexer);
      return parser.parse();
    }
  }

  Parser(LexicalAnalyzer lexer) {
    this.lexer = lexer;
  }

  private DieRollExpression parse() {
    nextToken();
    DieRollExpression e = arithmeticExpression();
    expect(EOE);
    return e;
  }


  private DieRollExpression arithmeticExpression() {
    DieRollExpression e = multiplicationExpression();
    while (token.getType() == PLUS || token.getType() == MINUS) {
      TokenType type = token.getType();
      nextToken();
      if (type == PLUS) {
        e = new PlusExpression(e, multiplicationExpression());
      } else {
        e = new MinusExpression(e, multiplicationExpression());
      }
    }
    return e;
  }

  private DieRollExpression multiplicationExpression() {
    DieRollExpression e = noCritExpression();
    while (token.getType() == MUTIPLY) {
      nextToken();
      e = new MultiplyExpression(e, noCritExpression());
    }
    return e;
  }


  private DieRollExpression noCritExpression() {
    DieRollExpression e = parenthesesExpression();
    if (token.getType() == NO_CRIT) {
      nextToken();
      e = new NoCriticalExpression(e);
    }
    return e;
  }

  private DieRollExpression parenthesesExpression() {
    if (token.getType() == LEFT_PAREN) {
      nextToken();
      DieRollExpression e = arithmeticExpression();
      expect(RIGHT_PAREN);
      nextToken();
      return new ParenthesesExpression(e);
    }
    return dieExpression();
  }

  private DieRollExpression dieExpression() {
    Position startPos = token.getPosition();
    double value = numberExpression();
    if (token.getType() == DIE) {
      int numDice = expectPositiveInt(value, startPos);
      nextToken();
      startPos = token.getPosition();
      Die die = expectDie(numberExpression(), startPos);
      Integer rerollOnceThreshold = null;
      if (token.getType() == TokenType.REROLL_ONCE) {
        nextToken();
        rerollOnceThreshold = expectPositiveInt(numberExpression(), startPos);
      }
      return new Dice(numDice, die, rerollOnceThreshold);
    }
    if (value == (int) value) {
      return new NumberExpression((int) value);
    }
    return new FloatExpression(value);
  }

  @SuppressWarnings("unchecked")
  private double numberExpression() {
    int baseVal = 1;
    if (token.getType() == MINUS) {
      baseVal = -1;
      nextToken();
    }
    if (token.getType() == NUMBER) {
      double value = baseVal * ((Token<Integer>) token).getLexeme();
      nextToken();
      return value;
    } else if (token.getType() == FLOAT) {
      double value = baseVal * ((Token<Double>) token).getLexeme();
      nextToken();
      return value;
    }
    throw new SyntaxException("Syntax error at " + token.getPosition() + ": found "
        + token.getType() + " when expecting a positive number");
  }

  private void nextToken() {
    token = lexer.nextToken();
  }

  private void expect(TokenType type) {
    if (token.getType() != type) {
      throw new SyntaxException("Syntax error at " + token.getPosition() + ": found "
          + token.getType() + " when expecting " + type.getValue());
    }
  }

  private int expectPositiveInt(double value, Position pos) {
    if (value <= 0 || value != (int) value) {
      throw new SyntaxException("Syntax error at " + pos + ": found "
          + value + " when expecting a positive integer");
    }
    return (int) value;
  }

  private Die expectDie(double value, Position pos) {
    int intValue = expectPositiveInt(value, pos);
    return Die.get(intValue)
        .orElseThrow(() -> new SyntaxException("Syntax error at " + token.getPosition() + ": found "
            + value + " when expecting a valid die type"));
  }

}
