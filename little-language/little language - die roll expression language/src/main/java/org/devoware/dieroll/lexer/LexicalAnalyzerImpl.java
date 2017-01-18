package org.devoware.dieroll.lexer;

import static java.util.Objects.requireNonNull;
import static org.devoware.dieroll.lexer.Token.Type.DIE;
import static org.devoware.dieroll.lexer.Token.Type.DROP_HIGHEST;
import static org.devoware.dieroll.lexer.Token.Type.DROP_LOWEST;
import static org.devoware.dieroll.lexer.Token.Type.EOF;
import static org.devoware.dieroll.lexer.Token.Type.KEEP_HIGHEST;
import static org.devoware.dieroll.lexer.Token.Type.KEEP_LOWEST;
import static org.devoware.dieroll.lexer.Token.Type.LEFT_PAREN;
import static org.devoware.dieroll.lexer.Token.Type.MINUS;
import static org.devoware.dieroll.lexer.Token.Type.PLUS;
import static org.devoware.dieroll.lexer.Token.Type.RIGHT_PAREN;

import java.io.IOException;
import java.io.Reader;

import org.devoware.dieroll.lexer.Token.Type;

class LexicalAnalyzerImpl implements LexicalAnalyzer {

  private final Reader in;
  private int pos = -1;
  private int peek = ' ';

  public LexicalAnalyzerImpl(Reader in) {
    this.in = requireNonNull(in, "in cannot be null");
  }

  @Override
  public Token nextToken() {
    try {
      // skip whitespace
      for (;; readChar()) {
        if (peek == ' ' || peek == '\t' || peek == '\n' || peek == '\r') {
          continue;
        } else {
          break;
        }
      }
      
      // Identify tokens representing operators
      switch (peek) {
        case 'd':
        case 'D':
          readChar();
          if (peek == 'l' || peek == 'L') {
            return getToken(DROP_LOWEST);
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
          throw new LexicalAnalysisException("Unexpected character '" + peek + "' at position " + pos);
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
      
      throw new LexicalAnalysisException("Unexpected character '" + peek + "' at position " + pos);

    } catch (IOException io) {
      throw new LexicalAnalysisException(io);
    }
    

  }

  private void readChar () throws IOException {
    peek = in.read();
    pos += 1;
  }

  private Token getToken(Type type) {
    return getToken(type, true);
  }
  
  private Token getToken(Type type, boolean resetPeek) {
    if (resetPeek) {
      peek = ' ';
    }
    return new Token(type);
  }

  private Token getNumber(int value) {
    return new NumberToken(value);
  }
  
}
