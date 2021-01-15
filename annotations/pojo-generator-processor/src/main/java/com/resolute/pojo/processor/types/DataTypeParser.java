package com.resolute.pojo.processor.types;

import static com.google.common.base.Preconditions.checkArgument;
import static com.resolute.pojo.processor.types.Token.Type.EOF;
import static java.util.Objects.requireNonNull;

import java.io.StringReader;
import java.util.List;

import com.google.common.collect.Lists;
import com.resolute.pojo.processor.types.Token.Type;
import com.resolute.utils.simple.pojo_generator.DataType;

public class DataTypeParser {

  private final ImportExclusion importExclusion;
  private final LexicalAnalyzer lexer;
  private Token token;

  public static DataType parse(String expression, ImportExclusion importExclusion) {
    requireNonNull(expression, "expression cannot be null");
    checkArgument(expression.length() > 0, "expression cannot be an empty string");
    try (StringReader in = new StringReader(expression)) {
      LexicalAnalyzer lexer = new LexicalAnalyzer(in);
      DataTypeParser parser = new DataTypeParser(importExclusion, lexer);
      return parser.parse();
    }
  }

  private DataTypeParser(ImportExclusion importExclusion, LexicalAnalyzer lexer) {
    this.importExclusion = importExclusion;
    this.lexer = lexer;
  }

  private DataType parse() {
    nextToken();
    List<DataType> dataTypes = Lists.newArrayList();
    while (token.getType() != EOF) {
      DataType dataType = terminalExpression();
      dataTypes.add(dataType);
    }
    expect(EOF);
    return new CompositeDataType(dataTypes);
  }


  private DataType terminalExpression() {
    Token tok = token;
    nextToken();
    switch (tok.getType()) {
      case LEFT_ANGLE_BRACKET:
        return new LeftAngleBracket();
      case RIGHT_ANGLE_BRACKET:
        return new RightAngleBracket();
      case LEFT_SQUARE_BRACKET:
        return new LeftSquareBracket();
      case RIGHT_SQUARE_BRACKET:
        return new RightSquareBracket();
      case COMMA:
        return new Comma();
      case IDENTIFIER:
        String value = IdentifierToken.class.cast(tok).getValue();
        return new BasicDataType(importExclusion, value);
      default:
        // This can never happen because of the surrounding if clause
        throw new AssertionError("Unexpected type: " + tok.getType());
    }
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
