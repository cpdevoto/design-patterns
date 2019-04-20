package org.devoware.dice;

import static java.util.Objects.requireNonNull;
import static org.devoware.dice.TokenType.DIE;
import static org.devoware.dice.TokenType.EOE;
import static org.devoware.dice.TokenType.MINUS;
import static org.devoware.dice.TokenType.NUMBER;
import static org.devoware.dice.TokenType.PLUS;

import java.io.IOException;
import java.io.Reader;

class LexicalAnalyzer {

  private final Reader input;
  private int peek = ' ';
  private Position position;
  private Position startPosition;


  LexicalAnalyzer(Reader input) {
    requireNonNull(input, "input cannot be null");
    this.input = input;
    this.position = new Position();
  }

  Token nextToken() {
    // skip whitespace
    for (;; readChar()) {
      if (peek == ' ' || peek == '\t' || peek == '\n' || peek == '\r') {
        if (peek == '\n') {
          position.advanceLine();
        }
        continue;
      } else {
        break;
      }
    }
    startPosition = Position.copyOf(position);
    // Identify tokens representing operators
    switch (peek) {
      case '+':
        return getToken(PLUS);
      case '-':
        return getToken(MINUS);
      case 'd':
      case 'D':
        return getToken(DIE);
      case -1:
        return getToken(EOE, false);
    }

    if (Character.isDigit(peek)) {
      int value = 0;
      do {
        value = (10 * value) + Character.digit(peek, 10);
        readChar();
      } while (Character.isDigit(peek));
      return getNumberToken(value);
    }

    throw new LexicalAnalysisException(
        "Unexpected character '" + peek + "' at position " + startPosition);
  }

  private void readChar() {
    try {
      peek = input.read();
    } catch (IOException e) {
      // Wrap the checked exception as an unchecked exception
      throw new LexicalAnalysisException(e);
    }
    position.advanceCharacter();
  }

  private Token getToken(TokenType type) {
    return getToken(type, true);
  }

  private Token getToken(TokenType type, boolean resetPeek) {
    if (resetPeek) {
      peek = ' ';
    }
    return new Token(startPosition, type);
  }

  private Token getNumberToken(int value) {
    return new Token(startPosition, NUMBER, String.valueOf(value));
  }


}
