package org.devoware.dicegolem.dice;

import static java.util.Objects.requireNonNull;
import static org.devoware.dicegolem.dice.Token.Type.DIE;
import static org.devoware.dicegolem.dice.Token.Type.DIVIDE;
import static org.devoware.dicegolem.dice.Token.Type.EOS;
import static org.devoware.dicegolem.dice.Token.Type.LEFT_PAREN;
import static org.devoware.dicegolem.dice.Token.Type.MINUS;
import static org.devoware.dicegolem.dice.Token.Type.MULTIPLY;
import static org.devoware.dicegolem.dice.Token.Type.PLUS;
import static org.devoware.dicegolem.dice.Token.Type.RIGHT_PAREN;

import java.io.IOException;
import java.io.StringReader;

import org.devoware.dicegolem.dice.Token.Type;

class LexicalAnalyzerImpl implements LexicalAnalyzer {

  private final StringReader in;
  private int peek = ' ';
  private Position position;
  private Position startPosition;


  LexicalAnalyzerImpl(StringReader in) {
    this.in = requireNonNull(in, "in cannot be null");
    this.position = new Position();
  }

  public Token nextToken() {
    if (peek == -1) {
      startPosition = Position.copyOf(position);
      return getToken(EOS, false);
    }

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
        return getToken(DIE);
      case '*':
        return getToken(MULTIPLY);
      case '/':
        return getToken(DIVIDE);
      case '+':
        return getToken(PLUS);
      case '-':
        return getToken(MINUS);
      case '(':
        return getToken(LEFT_PAREN);
      case ')':
        return getToken(RIGHT_PAREN);
      case -1:
        return getToken(EOS, false);
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
        (char) peek,
        startPosition);

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
