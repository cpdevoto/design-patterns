package com.resolute.search;

import static java.util.Objects.requireNonNull;

import java.io.Reader;
import java.io.StringReader;

class Parser {

  private final LexicalAnalyzer lexer;
  private Token token;

  static Expression parse(String s) {
    requireNonNull(s, "s cannot be null");
    try (StringReader in = new StringReader(s)) {
      Parser parser = new Parser(in);
      return parser.parse();
    }
  }

  Parser(Reader in) {
    this.lexer = new LexicalAnalyzer(in);
  }

  Expression parse() {
    nextToken();
    Expression e = orExpression();
    expect(TokenType.EOS);
    return e;
  }

  private Expression orExpression() {
    Expression e = andExpression();
    while (token.getType() == TokenType.OR) {
      nextToken();
      e = new OrExpression(e, andExpression());
    }
    return e;
  }

  private Expression andExpression() {
    Expression e = notExpression();
    while (token.getType() == TokenType.AND) {
      nextToken();
      e = new AndExpression(e, notExpression());
    }
    return e;
  }

  private Expression notExpression() {
    if (token.getType() == TokenType.NOT) {
      nextToken();
      return new NotExpression(simpleExpression());
    }
    return simpleExpression();
  }

  private Expression simpleExpression() {
    if (token.getType() == TokenType.LEFT_PAREN) {
      nextToken();
      Expression c = orExpression();
      expect(TokenType.RIGHT_PAREN);
      nextToken();
      return c;
    }
    return wordExpression();
  }

  private Expression wordExpression() {
    expect(TokenType.WORD);
    Expression e = new WordExpression(token.getLexeme().get());
    nextToken();
    return e;
  }

  private void nextToken() {
    token = lexer.nextToken();
  }

  private void expect(TokenType expected) {
    if (token.getType() != expected) {
      throw new ParseException("Syntax error at " + token.getPosition() + ": found "
          + token.getType() + " when expecting " + expected);
    }
  }

}
