package org.dicegolem.parser;

import static java.util.Objects.requireNonNull;
import static org.dicegolem.parser.Token.Type.DIE;
import static org.dicegolem.parser.Token.Type.DROP_HIGHEST;
import static org.dicegolem.parser.Token.Type.EOF;
import static org.dicegolem.parser.Token.Type.KEEP_HIGHEST;
import static org.dicegolem.parser.Token.Type.KEEP_LOWEST;
import static org.dicegolem.parser.Token.Type.LEFT_PAREN;
import static org.dicegolem.parser.Token.Type.MINUS;
import static org.dicegolem.parser.Token.Type.PLUS;
import static org.dicegolem.parser.Token.Type.REROLL_ONCE;
import static org.dicegolem.parser.Token.Type.RIGHT_PAREN;

import java.io.IOException;
import java.io.StringReader;

import org.dicegolem.parser.Token.Type;

class LexicalAnalyzer {

  private final StringReader in;
  private int peek = ' ';
  private Position position;
  private Position startPosition;


  LexicalAnalyzer(StringReader in) {
    this.in = requireNonNull(in, "in cannot be null");
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
      case 'd':
      case 'D':
        readChar();
        if (peek == 'l' || peek == 'L') {
          return getToken(Token.Type.DROP_LOWEST);
        } else if (peek == 'h' || peek == 'H') {
          return getToken(DROP_HIGHEST);
        }
        return getToken(DIE, false); // we already read an extra character, so don't reset peek
      case 'k':
      case 'K':
        readChar();
        if (peek == 'l' || peek == 'L') {
          return getToken(KEEP_LOWEST);
        } else if (peek == 'h' || peek == 'H') {
          return getToken(KEEP_HIGHEST);
        }
        throw new LexicalAnalysisException(
            "Unexpected character '" + (char) peek + "' at " + position);
      case 'r':
      case 'R':
        readChar();
        if (peek == 'o' || peek == 'O') {
          readChar();
          if (peek == '<') {
            return getToken(REROLL_ONCE);
          }
        }
        throw new LexicalAnalysisException(
            "Unexpected character '" + (char) peek + "' at " + position);
      case '+':
        return getToken(PLUS);
      case '-':
        return getToken(MINUS);
      case '(':
        return getToken(LEFT_PAREN);
      case ')':
        return getToken(RIGHT_PAREN);
      case -1:
        return getToken(EOF);
    }

    // Identify numbers
    if (Character.isDigit(peek)) {
      int value = 0;
      do {
        value = (10 * value) + Character.digit(peek, 10);
        readChar();
      } while (Character.isDigit(peek));
      return getNumber(value);
    }

    throw new LexicalAnalysisException(
        "Unexpected character '" + (char) peek + "' at " + startPosition);

  }

  private void readChar() {
    try {
      peek = in.read();
    } catch (IOException e) {
      // This should never happen, because we are only using StringReaders
      throw new AssertionError("Unexpected exception", e);
    }
    position.advanceCharacter();
  }

  private Token getToken(Type type) {
    return getToken(type, true);
  }

  private Token getToken(Type type, boolean resetPeek) {
    if (resetPeek) {
      peek = ' ';
    }
    return new Token(startPosition, type);
  }

  private Token getNumber(int value) {
    return new NumberToken(startPosition, value);
  }

}
