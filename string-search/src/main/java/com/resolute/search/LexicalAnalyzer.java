package com.resolute.search;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.Reader;
import java.util.Map;
import java.util.function.Function;

import com.google.common.collect.ImmutableMap;

class LexicalAnalyzer {

  private static final Map<String, Function<Position, Token>> RESERVED_WORDS =
      ImmutableMap.<String, Function<Position, Token>>builder()
          .put("and", pos -> new Token(pos, TokenType.AND))
          .put("or", pos -> new Token(pos, TokenType.OR))
          .put("not", pos -> new Token(pos, TokenType.NOT))
          .build();

  private final Reader in;
  private int peek = ' ';
  private Position position;
  private Position startPosition;

  LexicalAnalyzer(Reader in) {
    this.in = requireNonNull(in, "in cannot be null");
    this.position = new Position();
  }

  Token nextToken() {
    // skip whitespace
    while (Character.isWhitespace(peek)) {
      if (peek == '\n') {
        position = position.advanceLine();
      }
      readChar();
    }

    startPosition = Position.copyOf(position);
    // Identify individual characters representing operators
    switch (peek) {
      case '(':
        return getToken(TokenType.LEFT_PAREN);
      case ')':
        return getToken(TokenType.RIGHT_PAREN);
      case -1:
        return getToken(TokenType.EOS);
    }

    // It's not an individual character so let's extract an entire word
    StringBuilder buf = new StringBuilder();
    do {
      buf.append((char) peek);
      readChar();
    } while (!Character.isWhitespace(peek) && !isReservedCharacter());

    String lexeme = buf.toString();
    if (RESERVED_WORDS.containsKey(lexeme.toLowerCase())) {
      return RESERVED_WORDS.get(lexeme.toLowerCase()).apply(startPosition);
    }
    return getWordToken(lexeme);
  }

  private void readChar() {
    try {
      peek = in.read();
      position = position.advanceCharacter();
    } catch (IOException e) {
      throw new ParseException("A problem occurred while attempting to read from the input source",
          e);
    }
  }

  private Token getToken(TokenType type) {
    peek = ' ';
    return new Token(startPosition, type);
  }

  private Token getWordToken(String lexeme) {
    return new Token(startPosition, TokenType.WORD, lexeme);
  }

  private boolean isReservedCharacter() {
    return peek == -1 || peek == '(' || peek == ')';
  }
}
