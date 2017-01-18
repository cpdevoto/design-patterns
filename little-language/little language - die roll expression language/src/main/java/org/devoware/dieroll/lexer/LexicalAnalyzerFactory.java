package org.devoware.dieroll.lexer;

import java.io.Reader;

public interface LexicalAnalyzerFactory {
  
  public static LexicalAnalyzerFactory create () {
    return LexicalAnalyzerFactoryImpl.create();
  }
  
  public LexicalAnalyzer create (Reader in);

}
