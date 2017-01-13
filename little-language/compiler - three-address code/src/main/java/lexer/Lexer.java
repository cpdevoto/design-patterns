package lexer;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import symbols.Type;

public class Lexer {
  public static int line = 1;
  
  private char peek = ' ';
  private Map<String, Word> words = new HashMap<>();
  private InputStream inStream;
  
  public Lexer (InputStream inStream) {
    this.inStream = Objects.requireNonNull(inStream, "inStream cannot be null");
    reserve(new Word("if", Tag.IF));
    reserve(new Word("else", Tag.ELSE));
    reserve(new Word("while", Tag.WHILE));
    reserve(new Word("do", Tag.DO));
    reserve(new Word("break", Tag.BREAK));
    reserve(Word.TRUE);
    reserve(Word.FALSE);
    reserve(Type.INT);
    reserve(Type.CHAR);
    reserve(Type.BOOL);
    reserve(Type.FLOAT);
  }
  
  public Token scan () throws IOException {
    // skip whitespace
    for (;; readch()) {
      if (peek == ' ' || peek == '\t') {
        continue;
      } else if (peek == '\n') {
        line += 1;
      } else {
        break;
      }
    }
    
    // Identify tokens representing operators with more than one character
    switch (peek) {
      case '&':
        if (readch('&')) {
          return Word.AND;
        }
        return new Token('&');
      case '|':
        if (readch('|')) {
          return Word.OR;
        }
        return new Token('|');
      case '=':
        if (readch('=')) {
          return Word.EQ;
        }
        return new Token('=');
      case '!':
        if (readch('=')) {
          return Word.NE;
        }
        return new Token('!');
      case '<':
        if (readch('=')) {
          return Word.LE;
        }
        return new Token('<');
      case '>':
        if (readch('=')) {
          return Word.GE;
        }
        return new Token('>');
    }
    
    // Identify tokens representing integers or float literals
    if (Character.isDigit(peek)) {
      int v = 0;
      do {
        v = (10 * v) + Character.digit(peek, 10);
        readch();
      } while (Character.isDigit(peek));
      if (peek !=  '.') {
        return new Num(v);
      }
      float x = v;
      float d = 10;
      while (true) {
        readch();
        if (!Character.isDigit(peek)) {
          break;
        }
        x += Character.digit(peek, 10) / d;
        d /= 10;
      }
      return new Real(x);
    }
    
    // Identify tokens representing identifiers or reserved words
    if (Character.isLetter(peek)) {
      StringBuilder buf = new StringBuilder();
      do {
        buf.append(peek);
        readch();
      } while (Character.isLetterOrDigit(peek));
      String s = buf.toString();
      Word w = words.get(s);
      if (w != null) {
        // It is a reserved word;
        return w;
      }
      w = new Word(s, Tag.ID);
      words.put(s,  w);  // Add the identifier to the reserved words
      return w;
    }
    
    // Any characters that don't conform to the above patterns, are returned as one-character tokens
    Token tok = new Token(peek);
    peek = ' ';
    return tok;
  }
  
  private void reserve (Word w) {
    words.put(w.lexeme, w);
  }
  
  private boolean readch (char c) throws IOException {
    readch();
    if (peek != c) {
      return false;
    }
    peek = ' ';
    return true;
  }
  
  private void readch () throws IOException {
    peek = (char) inStream.read();
  }

}
