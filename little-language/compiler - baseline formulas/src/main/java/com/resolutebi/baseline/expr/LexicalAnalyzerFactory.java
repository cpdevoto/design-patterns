package com.resolutebi.baseline.expr;

import java.io.Reader;

interface LexicalAnalyzerFactory {
  
  static LexicalAnalyzerFactory create () {
    return LexicalAnalyzerFactoryImpl.create();
  }
  
  public LexicalAnalyzer create (Reader in);

}
