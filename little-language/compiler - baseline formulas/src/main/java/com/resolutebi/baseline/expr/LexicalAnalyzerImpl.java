package com.resolutebi.baseline.expr;

import static com.resolutebi.baseline.expr.Token.Type.AND;
import static com.resolutebi.baseline.expr.Token.Type.DIVIDE;
import static com.resolutebi.baseline.expr.Token.Type.ELSE;
import static com.resolutebi.baseline.expr.Token.Type.EOF;
import static com.resolutebi.baseline.expr.Token.Type.EQUALS;
import static com.resolutebi.baseline.expr.Token.Type.FALSE;
import static com.resolutebi.baseline.expr.Token.Type.GREATER_THAN;
import static com.resolutebi.baseline.expr.Token.Type.GREATER_THAN_OR_EQUALS;
import static com.resolutebi.baseline.expr.Token.Type.IF;
import static com.resolutebi.baseline.expr.Token.Type.LEFT_PAREN;
import static com.resolutebi.baseline.expr.Token.Type.LESS_THAN;
import static com.resolutebi.baseline.expr.Token.Type.LESS_THAN_OR_EQUALS;
import static com.resolutebi.baseline.expr.Token.Type.MINUS;
import static com.resolutebi.baseline.expr.Token.Type.MULTIPLY;
import static com.resolutebi.baseline.expr.Token.Type.NOT;
import static com.resolutebi.baseline.expr.Token.Type.NOT_EQUALS;
import static com.resolutebi.baseline.expr.Token.Type.OR;
import static com.resolutebi.baseline.expr.Token.Type.PLUS;
import static com.resolutebi.baseline.expr.Token.Type.TRUE;
import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.Reader;
import java.util.Set;

import com.google.common.collect.ImmutableRangeSet;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.Sets;
import com.google.common.collect.TreeRangeSet;
import com.resolutebi.baseline.expr.Token.Type;

class LexicalAnalyzerImpl implements LexicalAnalyzer {

  private static final RangeSet<Integer> STRING_CHARS;
  private static final Set<Character> ESCAPE_CHARS;

  private final Reader in;
  private int peek = ' ';
  private BasicPosition position;

  
  static {
    RangeSet<Integer> stringChars = TreeRangeSet.create();
    stringChars.add(Range.closed((int) '\u0020', (int) '\u0021'));
    stringChars.add(Range.closed((int) '\u0023', (int) '\u005B'));
    stringChars.add(Range.closed((int) '\u005D', (int) '\uFFFF'));
    STRING_CHARS = ImmutableRangeSet.copyOf(stringChars);
    
    Set<Character> escapeChars = Sets.newHashSet();
    escapeChars.add('"');
    escapeChars.add('\\');
    escapeChars.add('/');
    escapeChars.add('\b');
    escapeChars.add('\f');
    escapeChars.add('\n');
    escapeChars.add('\r');
    escapeChars.add('\t');
    ESCAPE_CHARS = ImmutableSet.copyOf(escapeChars);
  }
  
  public static boolean isValidStringCharacter(char c) {
    return STRING_CHARS.contains((int) c) || ESCAPE_CHARS.contains(c);
  }

  
  public LexicalAnalyzerImpl(Reader in) {
    this.in = requireNonNull(in, "in cannot be null");
    this.position = new BasicPosition();
  }

  @Override
  public Token nextToken() throws IOException {
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
    
    // Identify tokens representing operators
    switch (peek) {
      case '(':
        return getToken(LEFT_PAREN, position);
      case ')':
        return getToken(Type.RIGHT_PAREN, position);
      case '=':
        if (readChar('=')) {
          return getToken(EQUALS, position);
        }
      case '!':
        if (readChar('=')) {
          return getToken(NOT_EQUALS, position);
        }
        return getToken(NOT, position, false);
      case '&':
        if (readChar('&')) {
          return getToken(AND, position);
        }
      case '|':
        if (readChar('|')) {
          return getToken(OR, position);
        }
      case '>':
        if (readChar('=')) {
          return getToken(GREATER_THAN_OR_EQUALS, position);
        }
        return getToken(GREATER_THAN, position, false);
      case '<':
        if (readChar('=')) {
          return getToken(LESS_THAN_OR_EQUALS, position);
        }
        return getToken(LESS_THAN, position, false);
      case '+':
        return getToken(PLUS, position);
      case '-':
        return getToken(MINUS, position);
      case '*':
        return getToken(MULTIPLY, position);
      case '/':
        return getToken(DIVIDE, position);
      case -1:
        return getToken(EOF, position);
    }

    // Identify tokens representing numbers
    if (Character.isDigit(peek)) {
      Position numberStart = Position.copyOf(position);
      long v = 0;
      boolean firstLoop = true;
      do {
        if (firstLoop) {
          firstLoop = false;
        } else if (v == 0) {
          throw new LexicalAnalysisException("Invalid number starting at " + numberStart + ": numbers with multiple digits cannot start with 0");
        }
        v = (10 * v) + Character.digit(peek, 10);
        readChar();
      } while (Character.isDigit(peek));
      if (peek !=  '.' && peek != 'E' && peek != 'e') {
        return getDoubleToken(v, numberStart);
      }
      double x = v;
      if (peek == '.') {
        StringBuilder buf = new StringBuilder(String.valueOf(v)).append('.');
        int fracDigits = 0;
        while (true) {
          readChar();
          if (!Character.isDigit(peek)) {
            break;
          }
          buf.append((char) peek);
          fracDigits += 1;
        }
        if (fracDigits == 0) {
          throw new LexicalAnalysisException("Invalid number starting at " + numberStart + ": expected at least one digit after the '.'");
        }
        try {
          x = Double.parseDouble(buf.toString());
        } catch (NumberFormatException ex) {
          throw new LexicalAnalysisException(ex);
        }
      }
      if (peek != 'E' && peek != 'e') {
        return getDoubleToken(x, numberStart);
      } 

      StringBuilder buf = new StringBuilder(String.valueOf(x));
      buf.append((char) peek);
      readChar();
      if (peek == '+' || peek == '-') {
        buf.append((char) peek);
        readChar();
      }
      if (!Character.isDigit(peek)) {
        throw new LexicalAnalysisException("Invalid number starting at " + numberStart + ": expected at least one digit after the 'E'");
      }
      while (Character.isDigit(peek)) {
        buf.append((char) peek);
        readChar();
      }
      try {
        x = Double.parseDouble(buf.toString());
        return getDoubleToken(x, numberStart);
      } catch (NumberFormatException ex) {
        throw new LexicalAnalysisException(ex);
      }
    }
    
    // Identify tokens representing reserved words
    if (Character.isLetter(peek)) {
      Position wordStart = Position.copyOf(position);
      StringBuilder buf = new StringBuilder();
      
      do {
        buf.append((char) peek);
        readChar();
      } while (Character.isLetterOrDigit(peek) || peek == '_');
      String s = buf.toString().toLowerCase();
      
      if ("true".equals(s)) {
        return getToken(TRUE, position, false);
      } else if ("false".equals(s)) {
        return getToken(FALSE, position, false);
      } else if ("if".equals(s)) {
        return getToken(IF, position, false);
      } else if ("else".equals(s)) {
        return getToken(ELSE, position, false);
      } else {
        VariableId<?> id = VariableId.get(s);
        if (id != null) {
          return getVariableToken(id, position);
        }
      }
      
      throw new LexicalAnalysisException("Unrecognized token starting at " + wordStart + ": " + s);
    }

    if (peek == -1) {
      throw new LexicalAnalysisException("Unexpected end of string");
    }
    throw new LexicalAnalysisException("Unexpected character '" + (char) peek + "' at " + position);
  }
  
  @Override
  public Position getPosition() {
    return position;
  }
  
  private boolean readChar (char c) throws IOException {
    readChar();
    if (peek != c) {
      return false;
    }
    peek = ' ';
    return true;
  }
  

  private void readChar () throws IOException {
    peek = in.read();
    position.advanceCharacter();
  }

  private Token getToken(Type type, Position position) {
    return getToken(type, position, true);
  }
  
  private Token getToken(Type type, Position position, boolean resetPeek) {
    if (resetPeek) {
      peek = ' ';
    }
    return new Token(type, position);
  }

  private Token getVariableToken(VariableId<?> value, Position position) {
    return new VariableToken(value, position);
  }

  private Token getDoubleToken(double value, Position position) {
    return new DoubleToken(value, position);
  }

}