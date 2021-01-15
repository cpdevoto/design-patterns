package com.resolute.pojo.processor.types;

import static com.resolute.pojo.processor.types.Token.Type.COMMA;
import static com.resolute.pojo.processor.types.Token.Type.EOF;
import static com.resolute.pojo.processor.types.Token.Type.LEFT_ANGLE_BRACKET;
import static com.resolute.pojo.processor.types.Token.Type.LEFT_SQUARE_BRACKET;
import static com.resolute.pojo.processor.types.Token.Type.RIGHT_ANGLE_BRACKET;
import static com.resolute.pojo.processor.types.Token.Type.RIGHT_SQUARE_BRACKET;
import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.StringReader;

import com.resolute.pojo.processor.types.Token.Type;

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
      case '<':
        return getToken(LEFT_ANGLE_BRACKET);
      case '>':
        return getToken(RIGHT_ANGLE_BRACKET);
      case '[':
        return getToken(LEFT_SQUARE_BRACKET);
      case ']':
        return getToken(RIGHT_SQUARE_BRACKET);
      case ',':
        return getToken(COMMA);
      case -1:
        return getToken(EOF);
    }

    // Identify numbers
    if (Character.isLetter(peek) || peek == '_') {
      StringBuilder buf = new StringBuilder();
      do {
        buf.append((char) peek);
        readChar();
      } while (Character.isLetterOrDigit(peek) || peek == '_' || peek == '.');
      return getIdentifier(buf.toString());
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

  private Token getIdentifier(String value) {
    return new IdentifierToken(startPosition, value);
  }

}
