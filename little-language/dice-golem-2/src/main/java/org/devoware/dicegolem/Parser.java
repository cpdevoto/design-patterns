package org.devoware.dicegolem;

import static java.util.Objects.requireNonNull;
import static org.devoware.dicegolem.Token.Type.DIE;
import static org.devoware.dicegolem.Token.Type.DIVIDE;
import static org.devoware.dicegolem.Token.Type.EOS;
import static org.devoware.dicegolem.Token.Type.LEFT_PAREN;
import static org.devoware.dicegolem.Token.Type.MINUS;
import static org.devoware.dicegolem.Token.Type.MULTIPLY;
import static org.devoware.dicegolem.Token.Type.NUMBER;
import static org.devoware.dicegolem.Token.Type.PLUS;
import static org.devoware.dicegolem.Token.Type.RIGHT_PAREN;

import java.io.StringReader;

import org.devoware.dicegolem.Token.Type;

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
    Expression result = addExpression();
    expect(EOS);
    return result;
  }

  private Expression addExpression() {
    Expression c = multiplyExpression();
    while (token.getType() == PLUS || token.getType() == MINUS) {
      if (token.getType() == PLUS) {
        nextToken();
        c = new PlusExpression(c, multiplyExpression());
      } else {
        nextToken();
        c = new MinusExpression(c, multiplyExpression());
      }
    }
    return c;
  }

  private Expression multiplyExpression() {
    Expression c = parenthesisExpression();
    while (token.getType() == MULTIPLY || token.getType() == DIVIDE) {
      if (token.getType() == MULTIPLY) {
        nextToken();
        c = new MultiplyExpression(c, parenthesisExpression());
      } else {
        nextToken();
        c = new DivideExpression(c, parenthesisExpression());
      }
    }
    return c;
  }

  private Expression parenthesisExpression() {
    if (token.getType() == LEFT_PAREN) {
      nextToken();
      Expression c = addExpression();
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
    } else if (token.getType() == DIE) { // e.g. d4
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
