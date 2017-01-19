package org.devoware.json.lexer;

import java.io.Reader;

class LexicalAnalyzerFactoryImpl implements LexicalAnalyzerFactory {
  
  static LexicalAnalyzerFactoryImpl create () {
    return new LexicalAnalyzerFactoryImpl();
  }

  LexicalAnalyzerFactoryImpl() {}

  @Override
  public LexicalAnalyzer create(Reader in) {
    return new LexicalAnalyzerImpl(in);
  }

}
