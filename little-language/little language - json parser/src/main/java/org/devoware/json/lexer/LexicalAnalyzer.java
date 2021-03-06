package org.devoware.json.lexer;

import java.io.IOException;

import org.devoware.json.symbols.Token;

public interface LexicalAnalyzer {
  
  public static boolean isValidStringCharacter(char c) {
    return LexicalAnalyzerImpl.isValidStringCharacter(c);
  }
  
  public Token nextToken() throws IOException;
  
  public Position getPosition();
  
}
