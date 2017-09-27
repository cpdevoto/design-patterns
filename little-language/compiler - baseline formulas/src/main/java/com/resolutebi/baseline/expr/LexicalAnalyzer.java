package com.resolutebi.baseline.expr;

import java.io.IOException;

interface LexicalAnalyzer {
  
  static boolean isValidStringCharacter(char c) {
    return LexicalAnalyzerImpl.isValidStringCharacter(c);
  }
  
  public Token nextToken() throws IOException;
  
  public Position getPosition();
  
}
