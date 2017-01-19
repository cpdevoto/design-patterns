package org.devoware.json.lexer;

import org.devoware.json.symbols.Token;

public interface LexicalAnalyzer {
  
  public static boolean isValidStringCharacter(char c) {
    return LexicalAnalyzerImpl.isValidStringCharacter(c);
  }
  
  public Token nextToken();
  
  public Position getPosition();
  
}
