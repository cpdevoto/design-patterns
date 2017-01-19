package org.devoware.json.lexer;

import static java.util.Objects.requireNonNull;
import static org.devoware.json.symbols.Token.Type.EOF;
import static org.devoware.json.symbols.Token.Type.FALSE;
import static org.devoware.json.symbols.Token.Type.LEFT_CURLY_BRACKET;
import static org.devoware.json.symbols.Token.Type.LEFT_SQUARE_BRACKET;
import static org.devoware.json.symbols.Token.Type.NULL;
import static org.devoware.json.symbols.Token.Type.TRUE;

import java.io.IOException;
import java.io.Reader;
import java.util.Set;

import org.devoware.json.symbols.NumberToken;
import org.devoware.json.symbols.StringToken;
import org.devoware.json.symbols.Token;
import org.devoware.json.symbols.Token.Type;

import com.google.common.collect.ImmutableRangeSet;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.Sets;
import com.google.common.collect.TreeRangeSet;

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
  public Token nextToken() {
    try {
      // skip whitespace
      for (;; readChar()) {
        if (peek == ' ' || peek == '\t' || peek == '\n' || peek == '\r') {
          if (peek == '\n' || peek == '\r') {
            position.advanceLine();
          }
          continue;
        } else {
          break;
        }
      }
      
      // Identify tokens representing operators
      switch (peek) {
        case '[':
          return getToken(LEFT_SQUARE_BRACKET, position);
        case ']':
          return getToken(Type.RIGHT_SQUARE_BRACKET, position);
        case '{':
          return getToken(LEFT_CURLY_BRACKET, position);
        case '}':
          return getToken(Type.RIGHT_CURLY_BRACKET, position);
        case ':':
          return getToken(Type.COLON, position);
        case ',':
          return getToken(Type.COMMA, position);
        case -1:
          return getToken(EOF, position);
      }

      // Identify tokens representing numbers
      if (Character.isDigit(peek) || peek == '-') {
        Position numberStart = Position.copyOf(position);
        boolean unaryMinus = false;
        if (peek == '-') {
          unaryMinus = true;
          readChar();
          if (!Character.isDigit(peek)) {
            throw new LexicalAnalysisException("Invalid number starting at " + numberStart + ": expected a digit after the minus symbol");
          }
        }
        int v = 0;
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
        if (unaryMinus) {
          v  = v * -1;
        }
        if (peek !=  '.' && peek != 'E' && peek != 'e') {
          return getNumberToken(v, numberStart);
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
          return getNumberToken(x, numberStart);
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
          return new NumberToken(Double.parseDouble(buf.toString()), numberStart);
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
        } while (Character.isLetterOrDigit(peek));
        String s = buf.toString();
        
        if ("true".equals(s)) {
          return getToken(TRUE, position, false);
        } else if ("false".equals(s)) {
          return getToken(FALSE, position, false);
        } else if ("null".equals(s)) {
          return getToken(NULL, position, false);
        }
        
        throw new LexicalAnalysisException("Unrecognized token starting at " + wordStart + ": " + s);
      }

      // Identify tokens representing quoted strings
      if (peek == '"') {
        Position wordStart = Position.copyOf(position);
        StringBuilder buf = new StringBuilder();
        readChar();
        while (peek == '\\' || STRING_CHARS.contains(peek)) {
          if (peek == '\\') {
            Position escapeStart = Position.copyOf(position);
            readChar();
            buf.append(convertEscapeSequence(escapeStart));
          } else {
            buf.append((char) peek);
          }
          readChar();
        }
        if (peek != '"') {
          throw new LexicalAnalysisException("Invalid quoted string starting at " + wordStart + ": expected a '\"\'");
        }
        readChar();
        return getStringToken(buf.toString(), wordStart);
      }

      if (peek == -1) {
        throw new LexicalAnalysisException("Unexpected end of string");
      }
      throw new LexicalAnalysisException("Unexpected character '" + (char) peek + "' at " + position);

    } catch (IOException io) {
      throw new LexicalAnalysisException(io);
    }
  }
  
  @Override
  public Position getPosition() {
    return position;
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

  private Token getStringToken(String value, Position position) {
    return new StringToken(value, position);
  }

  private Token getNumberToken(double value, Position position) {
    return new NumberToken(value, position);
  }
  
  private char convertEscapeSequence(Position escapeStart) throws IOException {
    switch (peek) {
      case '"':
      case '\\':
      case '/':
        return (char) peek;
      case 'b':
        return '\b';
      case 'f':
        return '\f';
      case 'n':
        return '\n';
      case 'r':
        return '\r';
      case 't':
        return '\t';
      case 'u':
         StringBuilder buf = new StringBuilder();
         for (int i = 0; i < 4; i++) {
           readChar();
           if (peek == -1) {
             throw new LexicalAnalysisException("Invalid escape sequence starting at " + escapeStart + ": unexpected end of of stream");
           }
           buf.append((char) peek);
         }
         try {
           int value = Integer.parseInt(buf.toString(), 16);
           return (char) value;
         } catch (NumberFormatException ex) {
           throw new LexicalAnalysisException("Invalid escape sequence starting at " + escapeStart + ": '\\u" + buf.toString() + "'");
         }
      case -1:
        throw new LexicalAnalysisException("Unterminated escape sequence starting at " + escapeStart);
      default:
        throw new LexicalAnalysisException("Invalid escape sequence starting at " + escapeStart + ":  '\\" + (char) peek + "'");
    }
  }

}
