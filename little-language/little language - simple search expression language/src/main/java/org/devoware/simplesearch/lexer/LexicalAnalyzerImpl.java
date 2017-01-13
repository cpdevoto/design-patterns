package org.devoware.simplesearch.lexer;

import static java.io.StreamTokenizer.TT_EOF;
import static java.io.StreamTokenizer.TT_WORD;
import static java.util.Objects.requireNonNull;
import static org.devoware.simplesearch.lexer.Token.Type.AND;
import static org.devoware.simplesearch.lexer.Token.Type.NOT;
import static org.devoware.simplesearch.lexer.Token.Type.OR;
import static org.devoware.simplesearch.lexer.Token.Type.QUOTED_STRING;
import static org.devoware.simplesearch.lexer.Token.Type.WORD;

import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;

class LexicalAnalyzerImpl implements LexicalAnalyzer {

  private final StreamTokenizer input;

  public LexicalAnalyzerImpl(Reader in) {
    requireNonNull(in, "in cannot be null");
    input = new StreamTokenizer(in);
    input.resetSyntax();
    input.eolIsSignificant(false);
    input.whitespaceChars(0, ' ');
    input.wordChars('a', 'z');
    input.wordChars('A', 'Z');
    input.wordChars('0', '9');
    input.ordinaryChar('(');
    input.ordinaryChar(')');
    input.quoteChar('"');
  }

  @Override
  public Token nextToken() {
    try {
      switch (input.nextToken()) {
        case TT_EOF:
          return Token.EOF;
        case TT_WORD:
          if (input.sval.equalsIgnoreCase("or")) {
            return Token.create(OR, input.sval);
          } else if (input.sval.equalsIgnoreCase("and")) {
            return Token.create(AND, input.sval);
          } else if (input.sval.equalsIgnoreCase("not")) {
            return Token.create(NOT, input.sval);
          } else {
            return Token.create(WORD, input.sval);
          }
        case '"':
          return Token.create(QUOTED_STRING, input.sval);
        case '(':
          return Token.LEFT_PAREN;
        case ')':
          return Token.RIGHT_PAREN;
        default:
          throw new LexicalAnalysisException("Invalid character: " + input.sval);
      }
    } catch (IOException io) {
      throw new LexicalAnalysisException(io);
    }
  }

}
