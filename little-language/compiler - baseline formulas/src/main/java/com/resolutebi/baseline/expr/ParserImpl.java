package com.resolutebi.baseline.expr;

import static com.resolutebi.baseline.expr.Token.Type.AND;
import static com.resolutebi.baseline.expr.Token.Type.DIVIDE;
import static com.resolutebi.baseline.expr.Token.Type.DOUBLE;
import static com.resolutebi.baseline.expr.Token.Type.ELSE;
import static com.resolutebi.baseline.expr.Token.Type.EOF;
import static com.resolutebi.baseline.expr.Token.Type.EQUALS;
import static com.resolutebi.baseline.expr.Token.Type.GREATER_THAN;
import static com.resolutebi.baseline.expr.Token.Type.GREATER_THAN_OR_EQUALS;
import static com.resolutebi.baseline.expr.Token.Type.IF;
import static com.resolutebi.baseline.expr.Token.Type.LEFT_PAREN;
import static com.resolutebi.baseline.expr.Token.Type.LESS_THAN;
import static com.resolutebi.baseline.expr.Token.Type.LESS_THAN_OR_EQUALS;
import static com.resolutebi.baseline.expr.Token.Type.MINUS;
import static com.resolutebi.baseline.expr.Token.Type.MULTIPLY;
import static com.resolutebi.baseline.expr.Token.Type.NOT;
import static com.resolutebi.baseline.expr.Token.Type.NOT_EQUALS;
import static com.resolutebi.baseline.expr.Token.Type.OR;
import static com.resolutebi.baseline.expr.Token.Type.PLUS;
import static com.resolutebi.baseline.expr.Token.Type.RIGHT_PAREN;
import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import com.resolutebi.baseline.expr.Token.Type;

class ParserImpl implements Parser {

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
  public Expression<Double> parse(String expression) {
    try (Reader in = new StringReader(expression)) {
      return parse(in);
    } catch (IOException e) {
      // This should never happen, but if it does, throw an assertion error
      throw new AssertionError("Unexpected exception", e);
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public Expression<Double> parse(Reader in) throws IOException {
    lexer = this.factory.create(in);
    nextToken();
    Expression<?> e = ifExpression();
    if (e.getType() != Double.class) {
      throw new SyntaxException("Syntax error: expected a numeric expression");
    }
    expect(EOF);
    return (Expression<Double>) e;
  }
  
  @SuppressWarnings("unchecked")
  private Expression<?> ifExpression() throws IOException {
    if (token.getType() == IF) {
      nextToken();
      expect(LEFT_PAREN);
      Token start = token;
      nextToken();
      Expression<?> condition = orExpression();
      if (condition.getType() != Boolean.class) {
        throw new SyntaxException("Syntax error at " + start.getPosition() + ": expected a boolean expression");
      }
      expect(RIGHT_PAREN);
      nextToken();
      start = token;
      Expression<?> ifBody = ifExpression();
      if (ifBody.getType() != Double.class) {
        throw new SyntaxException("Syntax error at " + start.getPosition() + ": expected a numeric expression");
      }
      expect(ELSE);
      nextToken();
      start = token;
      Expression<?> elseBody = ifExpression();
      if (elseBody.getType() != Double.class) {
        throw new SyntaxException("Syntax error at " + start.getPosition() + ": expected a numeric expression");
      }

      Expression<Double> ifExpression = IfExpression.create((Expression<Boolean>) condition, (Expression<Double>) ifBody, (Expression<Double>) elseBody);
      return ifExpression;
    }
    return orExpression();
  }
  
  @SuppressWarnings("unchecked")
  private Expression<?> orExpression() throws IOException {
    Expression<?> expression = andExpression();
    while (token.getType() == OR) {
      if (expression.getType() != Boolean.class) {
        throw new SyntaxException("Syntax error at " + token.getPosition() + ": the logical " + token.getType() + " operator can only be used with two boolean expressions");
      }
      Token opToken = token;
      nextToken();
      Expression<?> expression2 = andExpression();
      if (expression2.getType() != Boolean.class) {
        throw new SyntaxException("Syntax error at " + opToken.getPosition() + ": the logical " + opToken.getType() + " operator can only be used with two numeric expressions");
      }
      expression = OrOperator.create((Expression<Boolean>) expression, (Expression<Boolean>) expression2);
    }
    return expression;
  }
  
  @SuppressWarnings("unchecked")
  private Expression<?> andExpression() throws IOException {
    Expression<?> expression = equalityExpression();
    while (token.getType() == AND) {
      if (expression.getType() != Boolean.class) {
        throw new SyntaxException("Syntax error at " + token.getPosition() + ": the logical " + token.getType() + " operator can only be used with two boolean expressions");
      }
      Token opToken = token;
      nextToken();
      Expression<?> expression2 = equalityExpression();
      if (expression2.getType() != Boolean.class) {
        throw new SyntaxException("Syntax error at " + opToken.getPosition() + ": the logical " + opToken.getType() + " operator can only be used with two boolean expressions");
      }
      expression = AndOperator.create((Expression<Boolean>) expression, (Expression<Boolean>) expression2);
    }
    return expression;
  }

  @SuppressWarnings("unchecked")
  private Expression<?> equalityExpression() throws IOException {
    Expression<?> expression = relationalExpression();
    while (token.getType() == EQUALS || token.getType() == NOT_EQUALS) {
      Token opToken = token;
      nextToken();
      Expression<?> expression2 = relationalExpression();
      if (!expression.getType().equals(expression2.getType())) {
        throw new SyntaxException("Syntax error at " + opToken.getPosition() + ": the logical " + opToken.getType() + " operator can only be used with two expressions of the same type (i.e. numeric to numeric or boolean to boolean)");
      }
      switch (opToken.getType()) {
        case EQUALS:
          if (expression.getType().equals(Boolean.class)) {
            expression = EqualsOperator.create((Expression<Boolean>) expression, (Expression<Boolean>) expression2);
          } else {
            expression = EqualsOperator.create((Expression<Double>) expression, (Expression<Double>) expression2);
          }
          break;
        case NOT_EQUALS:
          if (expression.getType().equals(Boolean.class)) {
            expression = NotEqualsOperator.create((Expression<Boolean>) expression, (Expression<Boolean>) expression2);
          } else {
            expression = NotEqualsOperator.create((Expression<Double>) expression, (Expression<Double>) expression2);
          }
          break;
        default:
          throw new AssertionError("Expected the token to be one of " + EQUALS + " or " + NOT_EQUALS);
      }
    }
    return expression;
  }

  @SuppressWarnings("unchecked")
  private Expression<?> relationalExpression() throws IOException {
    Expression<?> expression = arithmeticExpression();
    while (token.getType() == GREATER_THAN || token.getType() == GREATER_THAN_OR_EQUALS || token.getType() == LESS_THAN || token.getType() == LESS_THAN_OR_EQUALS) {
      if (expression.getType() != Double.class) {
        throw new SyntaxException("Syntax error at " + token.getPosition() + ": the logical " + token.getType() + " operator can only be used to compare two numeric expressions");
      }
      Token opToken = token;
      nextToken();
      Expression<?> expression2 = arithmeticExpression();
      if (expression2.getType() != Double.class) {
        throw new SyntaxException("Syntax error at " + opToken.getPosition() + ": the logical " + opToken.getType() + " operator can only be used to compare two numeric expressions");
      }
      switch (opToken.getType()) {
        case GREATER_THAN:
          expression = GreaterThanOperator.create((Expression<Double>) expression, (Expression<Double>) expression2);
          break;
        case GREATER_THAN_OR_EQUALS:
          expression = GreaterThanOrEqualsOperator.create((Expression<Double>) expression, (Expression<Double>) expression2);
          break;
        case LESS_THAN:
          expression = LessThanOperator.create((Expression<Double>) expression, (Expression<Double>) expression2);
          break;
        case LESS_THAN_OR_EQUALS:
          expression = LessThanOrEqualsOperator.create((Expression<Double>) expression, (Expression<Double>) expression2);
          break;
        default:
          throw new AssertionError("Expected the token to be " + GREATER_THAN + ", " + GREATER_THAN_OR_EQUALS + ", " + LESS_THAN + ", or " + LESS_THAN_OR_EQUALS);
      }
    }
    return expression;
  }

  @SuppressWarnings("unchecked")
  private Expression<?> arithmeticExpression() throws IOException {
    Expression<?> expression = termExpression();
    while (token.getType() == PLUS || token.getType() == MINUS) {
      if (expression.getType() != Double.class) {
        throw new SyntaxException("Syntax error at " + token.getPosition() + ": the arithmetic " + token.getType() + " operator can only be used to with two numeric expressions");
      }
      Token opToken = token;
      nextToken();
      Expression<?> expression2 = termExpression();
      if (expression2.getType() != Double.class) {
        throw new SyntaxException("Syntax error at " + opToken.getPosition() + ": the arithmetic " + opToken.getType() + " operator can only be used with two numeric expressions");
      }
      switch (opToken.getType()) {
        case PLUS:
          expression = AdditionOperator.create((Expression<Double>) expression, (Expression<Double>) expression2);
          break;
        case MINUS:
          expression = SubtractionOperator.create((Expression<Double>) expression, (Expression<Double>) expression2);
          break;
        default:
          throw new AssertionError("Expected the token to be " + PLUS + " or " + MINUS);
      }
    }
    return expression;
  }

  @SuppressWarnings("unchecked")
  private Expression<?> termExpression() throws IOException {
    Expression<?> expression = unaryExpression();
    while (token.getType() == MULTIPLY || token.getType() == DIVIDE) {
      if (expression.getType() != Double.class) {
        throw new SyntaxException("Syntax error at " + token.getPosition() + ": the arithmetic " + token.getType() + " operator can only be used to with two numeric expressions");
      }
      Token opToken = token;
      nextToken();
      Expression<?> expression2 = unaryExpression();
      if (expression2.getType() != Double.class) {
        throw new SyntaxException("Syntax error at " + opToken.getPosition() + ": the arithmetic " + opToken.getType() + " operator can only be used with two numeric expressions");
      }
      switch (opToken.getType()) {
        case MULTIPLY:
          expression = MultiplicationOperator.create((Expression<Double>) expression, (Expression<Double>) expression2);
          break;
        case DIVIDE:
          expression = DivisionOperator.create((Expression<Double>) expression, (Expression<Double>) expression2);
          break;
        default:
          throw new AssertionError("Expected the token to be " + MULTIPLY + " or " + DIVIDE);
      }
    }
    return expression;
  }
  
  @SuppressWarnings("unchecked")
  private Expression<?> unaryExpression() throws IOException {
    if (token.getType() == MINUS) {
      nextToken();
      expect(DOUBLE);
      Expression<?> expression = UnaryMinusOperator.create(NumericLiteral.create(DoubleToken.class.cast(token).value()));
      nextToken();
      return expression;
    } else if (token.getType() == NOT) {
      Token opToken = token;
      nextToken();
      Expression<?> expression = unaryExpression();
      if (expression.getType() != Boolean.class) {
        throw new SyntaxException("Syntax error at " + opToken.getPosition() + ": the logical " + opToken.getType() + " operator can only be used with boolean expressions");
      }
      return NotOperator.create((Expression<Boolean>) expression);
    }
    return factor();
  }
  
  private Expression<?> factor() throws IOException {
    Expression<?> expression = null;
    switch (token.getType()) {
      case LEFT_PAREN:
        nextToken();
        expression = ifExpression();
        expect(RIGHT_PAREN);
        nextToken();
        return expression;
      case VARIABLE:
        expression = Variable.create(((VariableToken) token).id());
        nextToken();
        return expression;
      case DOUBLE:
        expression = NumericLiteral.create(((DoubleToken) token).value());
        nextToken();
        return expression;
      case TRUE:
        expression = BooleanLiteral.TRUE;
        nextToken();
        return expression;
      case FALSE:
        expression = BooleanLiteral.FALSE;
        nextToken();
        return expression;
      default:
        throw new SyntaxException("Syntax error at " + token.getPosition() + ": found " + token.getType() + " when expecting a variable or literal");
    }
  }

  private void nextToken () throws IOException {
    token = lexer.nextToken();
  }

  private void expect(Type type) {
    if (token.getType() != type) {
      throw new SyntaxException("Syntax error at " + token.getPosition() + ": found " + token.getType() + " when expecting " + type);
    }
  }

}
