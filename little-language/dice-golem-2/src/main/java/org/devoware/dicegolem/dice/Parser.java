package org.devoware.dicegolem.dice;

import static java.util.Objects.requireNonNull;
import static org.devoware.dicegolem.dice.Token.Type.DIE;
import static org.devoware.dicegolem.dice.Token.Type.EOS;
import static org.devoware.dicegolem.dice.Token.Type.LEFT_PAREN;
import static org.devoware.dicegolem.dice.Token.Type.MINUS;
import static org.devoware.dicegolem.dice.Token.Type.NUMBER;
import static org.devoware.dicegolem.dice.Token.Type.PLUS;
import static org.devoware.dicegolem.dice.Token.Type.RIGHT_PAREN;

import java.io.StringReader;

import org.devoware.dicegolem.dice.Token.Type;

class Parser {

  private final LexicalAnalyzer lexer;
  private Token token;


  static Expression parse(String expression) {
    requireNonNull(expression, "expression cannot be null");
    try (StringReader in = new StringReader(expression)) {
      LexicalAnalyzer lexer = new LexicalAnalyzerImpl(in);
      Parser parser = new Parser(lexer);
      return parser.parse();
    }
  }

  private Parser(LexicalAnalyzer lexer) {
    this.lexer = lexer;
  }

  private Expression parse() {
    nextToken();
    Expression result = binaryExpression();
    expect(EOS);
    return result;
  }

  private Expression binaryExpression() {
    Expression c = simpleExpression();
    while (token.getType() == PLUS || token.getType() == MINUS) {
      if (token.getType() == PLUS) {
        nextToken();
        c = new PlusExpression(c, simpleExpression());
      } else {
        nextToken();
        c = new MinusExpression(c, simpleExpression());
      }
    }
    return c;
  }

  private Expression simpleExpression() {
    if (token.getType() == LEFT_PAREN) {
      nextToken();
      Expression c = binaryExpression();
      expect(RIGHT_PAREN);
      nextToken();
      return c;
    }
    return unaryExpression();
  }

  private Expression unaryExpression() {
    if (token.getType() == MINUS) {
      nextToken();
      expect(NUMBER);
      int value = NumberToken.class.cast(token).getValue();
      nextToken();
      if (token.getType() == DIE) {
        expect(NUMBER);
      }
      return new UnaryMinusExpression(new ValueExpression(value));
    } else if (token.getType() == DIE) {
      nextToken();
      expect(NUMBER);
      Die die = numericTokenToDie();
      nextToken();
      return new DiceExpression(1, die);
    }
    return terminalExpression();
  }

  private Expression terminalExpression() {
    expect(NUMBER);
    int value = NumberToken.class.cast(token).getValue();
    nextToken();
    if (token.getType() != DIE) {
      return new ValueExpression(value);
    }
    nextToken();
    expect(NUMBER);
    Die die = numericTokenToDie();
    nextToken();
    return new DiceExpression(value, die);
  }

  private void nextToken() {
    token = lexer.nextToken();
  }

  private void expect(Type type) {
    if (token.getType() != type) {
      throw new UnexpectedTokenException(token, type);
    }
  }

  private Die numericTokenToDie() {
    int dieType = NumberToken.class.cast(token).getValue();
    Die die = Die.get(dieType);
    if (die == null) {
      throw new InvalidDieTypeException(token, dieType);
    }
    return die;
  }


}
