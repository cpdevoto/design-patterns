package org.devoware.dice;

import static org.devoware.dice.TokenType.DIE;
import static org.devoware.dice.TokenType.EOE;
import static org.devoware.dice.TokenType.MINUS;
import static org.devoware.dice.TokenType.NUMBER;
import static org.devoware.dice.TokenType.PLUS;

import java.io.StringReader;

class Parser {
  private final LexicalAnalyzer lexer;
  private Token token;

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
    DieRollExpression e = dieExpression();
    while (token.getType() == PLUS || token.getType() == MINUS) {
      TokenType type = token.getType();
      nextToken();
      if (type == PLUS) {
        e = new PlusExpression(e, dieExpression());
      } else {
        e = new MinusExpression(e, dieExpression());
      }
    }
    return e;
  }

  private DieRollExpression dieExpression() {
    Position startPos = token.getPosition();
    int value = numberExpression();
    if (token.getType() == DIE) {
      int numDice = expectPositiveInt(value, startPos);
      nextToken();
      Die die = expectDie(numberExpression());
      boolean isWeapon = false;
      if (token.getType() == TokenType.WEAPON) {
        isWeapon = true;
        nextToken();
      }
      return new Dice(numDice, die, isWeapon);
    }
    return new Modifier(value);
  }

  private int numberExpression() {
    int baseVal = 1;
    if (token.getType() == MINUS) {
      baseVal = -1;
      nextToken();
    }
    expect(NUMBER);
    int value = baseVal * Integer.parseInt(token.getLexeme());
    nextToken();
    return value;
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

  private int expectPositiveInt(int value, Position pos) {
    if (value < 1) {
      throw new SyntaxException("Syntax error at " + pos + ": found "
          + value + " when expecting a positive integer");
    }
    return value;
  }

  private Die expectDie(int value) {
    return Die.get(value)
        .orElseThrow(() -> new SyntaxException("Syntax error at " + token.getPosition() + ": found "
            + value + " when expecting a valid die type"));
  }

}
