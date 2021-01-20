package org.dicegolem;

import static java.util.Objects.requireNonNull;
import static org.dicegolem.Token.Type.DIE;
import static org.dicegolem.Token.Type.DROP_HIGHEST;
import static org.dicegolem.Token.Type.DROP_LOWEST;
import static org.dicegolem.Token.Type.EOF;
import static org.dicegolem.Token.Type.KEEP_HIGHEST;
import static org.dicegolem.Token.Type.KEEP_LOWEST;
import static org.dicegolem.Token.Type.LEFT_PAREN;
import static org.dicegolem.Token.Type.MINUS;
import static org.dicegolem.Token.Type.NUMBER;
import static org.dicegolem.Token.Type.PLUS;
import static org.dicegolem.Token.Type.REROLL_ONCE;
import static org.dicegolem.Token.Type.RIGHT_PAREN;

import java.io.StringReader;

import org.dicegolem.Token.Type;
import org.dicegolem.model.CompositeDice;
import org.dicegolem.model.DiceRollExpression;
import org.dicegolem.model.Die;
import org.dicegolem.model.DropHighestAggregator;
import org.dicegolem.model.DropLowestAggregator;
import org.dicegolem.model.KeepHighestAggregator;
import org.dicegolem.model.KeepLowestAggregator;
import org.dicegolem.model.Minus;
import org.dicegolem.model.NumericLiteral;
import org.dicegolem.model.Plus;
import org.dicegolem.model.RerollOnceModifier;
import org.dicegolem.model.UnaryMinus;

public class Dice {

  private LexicalAnalyzer lexer;
  private Token token;

  public static int roll(String expression) {
    DiceRollExpression exp = parse(expression);
    return exp.roll();
  }

  public static double average(String expression) {
    DiceRollExpression exp = parse(expression);
    return exp.average();
  }

  public static double averageDiceOnly(String expression) {
    DiceRollExpression exp = parse(expression);
    return exp.averageDiceOnly();
  }

  public static DiceRollExpression parse(String expression) {
    requireNonNull(expression, "expression cannot be null");
    try (StringReader in = new StringReader(expression)) {
      LexicalAnalyzer lexer = new LexicalAnalyzer(in);
      Dice parser = new Dice(lexer);
      return parser.parse();
    }
  }

  private Dice(LexicalAnalyzer lexer) {
    this.lexer = lexer;
  }

  private DiceRollExpression parse() {
    nextToken();
    DiceRollExpression result = binaryExpression();
    expect(EOF);
    return result;
  }


  private DiceRollExpression binaryExpression() {
    DiceRollExpression c = unaryExpression();
    while (token.getType() == PLUS || token.getType() == MINUS) {
      if (token.getType() == PLUS) {
        nextToken();
        c = new Plus(c, unaryExpression());
      } else {
        nextToken();
        c = new Minus(c, unaryExpression());
      }
    }
    return c;
  }

  private DiceRollExpression unaryExpression() {
    if (token.getType() == MINUS) {
      nextToken();
      return new UnaryMinus(simpleExpression());
    }
    return simpleExpression();
  }

  private DiceRollExpression simpleExpression() {
    if (token.getType() == LEFT_PAREN) {
      nextToken();
      DiceRollExpression c = binaryExpression();
      expect(RIGHT_PAREN);
      nextToken();
      return c;
    }
    return terminalExpression();
  }

  private DiceRollExpression terminalExpression() {
    if (token.getType() != NUMBER) {
      expect(NUMBER);
    }
    int value = NumberToken.class.cast(token).getValue();
    nextToken();
    if (token.getType() != DIE) {
      return new NumericLiteral(value);
    }
    return diceExpression(value);
  }

  private DiceRollExpression diceExpression(int value) throws AssertionError {
    nextToken();
    if (token.getType() != NUMBER) {
      expect(NUMBER);
    }
    int dieType = NumberToken.class.cast(token).getValue();
    Die die = Die.get(dieType);
    if (die == null) {
      throw new SyntaxException("Invalid die type: " + dieType);
    }
    CompositeDice.Builder builder = CompositeDice.builder()
        .withNumDice(value)
        .withDie(die);

    nextToken();
    diceModifierExpression(builder);
    return builder.build();
  }

  private void diceModifierExpression(CompositeDice.Builder builder) throws AssertionError {
    boolean aggregatorFound = false, modifierFound = false;
    while (isAggregator() ||
        isModifier()) {
      Token diceModifier = token;
      nextToken();
      if (token.getType() != NUMBER) {
        expect(NUMBER);
      }
      int num = NumberToken.class.cast(token).getValue();
      if (isAggregator(diceModifier.getType())) {
        if (aggregatorFound) {
          throw new SyntaxException("Syntax error at " + diceModifier.getPosition()
              + ": unexpected second aggregator " + diceModifier.getType() + num);
        }
        aggregatorFound = true;
        switch (diceModifier.getType()) {
          case DROP_HIGHEST:
            builder.withAggregator(new DropHighestAggregator(num));
            break;
          case DROP_LOWEST:
            builder.withAggregator(new DropLowestAggregator(num));
            break;
          case KEEP_HIGHEST:
            builder.withAggregator(new KeepHighestAggregator(num));
            break;
          case KEEP_LOWEST:
            builder.withAggregator(new KeepLowestAggregator(num));
            break;
          default:
            // This can never happen because of the surrounding if clause
            throw new AssertionError("Unexpected type: " + diceModifier.getType());
        }
      } else {
        if (modifierFound) {
          throw new SyntaxException("Syntax error at " + diceModifier.getPosition()
              + ": unexpected second modifier " + diceModifier.getType() + num);
        }
        modifierFound = true;
        builder.withModifier(new RerollOnceModifier(num));
      }
      nextToken();
    }
  }

  private boolean isModifier() {
    return isModifier(token.getType());
  }

  private boolean isAggregator() {
    return isAggregator(token.getType());
  }

  private boolean isModifier(Token.Type type) {
    return type == REROLL_ONCE;
  }

  private boolean isAggregator(Token.Type type) {
    return type == DROP_HIGHEST || type == DROP_LOWEST ||
        type == KEEP_HIGHEST || type == KEEP_LOWEST;
  }

  private void nextToken() {
    token = lexer.nextToken();
  }

  private void expect(Type type) {
    if (token.getType() != type) {
      throw new SyntaxException("Syntax error at " + token.getPosition() + ": found "
          + token.getType() + " when expecting " + type);
    }
  }

}
