package org.devoware.simplesearch.parser;

import static java.util.Objects.requireNonNull;
import static org.devoware.simplesearch.lexer.Token.Type.AND;
import static org.devoware.simplesearch.lexer.Token.Type.EOF;
import static org.devoware.simplesearch.lexer.Token.Type.LEFT_PAREN;
import static org.devoware.simplesearch.lexer.Token.Type.NOT;
import static org.devoware.simplesearch.lexer.Token.Type.OR;
import static org.devoware.simplesearch.lexer.Token.Type.QUOTED_STRING;
import static org.devoware.simplesearch.lexer.Token.Type.RIGHT_PAREN;
import static org.devoware.simplesearch.lexer.Token.Type.WORD;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.devoware.simplesearch.lexer.LexicalAnalyzer;
import org.devoware.simplesearch.lexer.LexicalAnalyzerFactory;
import org.devoware.simplesearch.lexer.Token;
import org.devoware.simplesearch.lexer.Token.Type;
import org.devoware.simplesearch.model.AndExpression;
import org.devoware.simplesearch.model.Expression;
import org.devoware.simplesearch.model.NotExpression;
import org.devoware.simplesearch.model.OrExpression;
import org.devoware.simplesearch.model.WordExpression;

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
  public Expression parse(String expression) {
    try (Reader in = new StringReader(expression)) {
      return parse(in);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Expression parse(Reader in) {
    lexer = this.factory.create(in);
    nextToken();
    Expression c = orCombination();
    expect(EOF);
    return c;
  }
  
  private Expression orCombination() {
    Expression c = andCombination();
    while (token.getType() == OR) {
      nextToken();
      c = new OrExpression(c, andCombination());
    }
    return c;
  }

  private Expression andCombination() {
    Expression c = notCombination();
    while (token.getType() == AND) {
      nextToken();
      c = new AndExpression(c, notCombination());
    }
    return c;
  }
  
  private Expression notCombination() {
    if (token.getType() == NOT) {
      nextToken();
      return new NotExpression(simpleCombination());
    }
    return simpleCombination();
  }

  private Expression simpleCombination() {
    if (token.getType() == LEFT_PAREN) {
      nextToken();
      Expression c = orCombination();
      expect(RIGHT_PAREN);
      nextToken();
      return c;
    }
    return wordCombination();
  }

  private Expression wordCombination() {
    if (token.getType() != WORD && token.getType() != QUOTED_STRING) {
      expect(WORD);
    }
    Expression c = new WordExpression(token.getLexeme());
    nextToken();
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
