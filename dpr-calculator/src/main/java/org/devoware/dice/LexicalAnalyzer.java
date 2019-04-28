package org.devoware.dice;

import static java.util.Objects.requireNonNull;
import static org.devoware.dice.TokenType.DIE;
import static org.devoware.dice.TokenType.EOE;
import static org.devoware.dice.TokenType.FLOAT;
import static org.devoware.dice.TokenType.LEFT_PAREN;
import static org.devoware.dice.TokenType.MINUS;
import static org.devoware.dice.TokenType.MUTIPLY;
import static org.devoware.dice.TokenType.NO_CRIT;
import static org.devoware.dice.TokenType.NUMBER;
import static org.devoware.dice.TokenType.PLUS;
import static org.devoware.dice.TokenType.REROLL_ONCE;
import static org.devoware.dice.TokenType.RIGHT_PAREN;

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

  Token<?> nextToken() {
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
      case '*':
        return getToken(MUTIPLY);
      case '(':
        return getToken(LEFT_PAREN);
      case ')':
        return getToken(RIGHT_PAREN);
      case 'd':
      case 'D':
        return getToken(DIE);
      case -1:
        return getToken(EOE, false);
    }

    if (Character.isLetter(peek)) {
      StringBuilder buf = new StringBuilder();
      do {
        buf.append((char) peek);
        readChar();
      } while (Character.isLetter(peek) || peek == '<');
      String lexeme = buf.toString();
      if (lexeme.equalsIgnoreCase("ro<")) {
        return getToken(REROLL_ONCE, false);
      } else if (lexeme.equalsIgnoreCase("nc")) {
        return getToken(NO_CRIT, false);
      } else {
        throw new LexicalAnalysisException(
            "Unexpected string '" + lexeme + "' at position " + startPosition);
      }
    }

    if (Character.isDigit(peek)) {
      int value = 0;
      do {
        value = (10 * value) + Character.digit(peek, 10);
        readChar();
      } while (Character.isDigit(peek));
      if (peek != '.') {
        return getNumberToken(value);
      }
      float x = value;
      float d = 10;
      while (true) {
        readChar();
        if (!Character.isDigit(peek)) {
          break;
        }
        x += Character.digit(peek, 10) / d;
        d *= 10;
      }
      return getFloatToken(x);
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

  private Token<String> getToken(TokenType type) {
    return getToken(type, true);
  }

  private Token<String> getToken(TokenType type, boolean resetPeek) {
    if (resetPeek) {
      peek = ' ';
    }
    return new Token<String>(startPosition, type);
  }

  private Token<Integer> getNumberToken(int value) {
    return new Token<Integer>(startPosition, NUMBER, value);
  }

  private Token<Double> getFloatToken(double value) {
    return new Token<Double>(startPosition, FLOAT, value);
  }

}
