package com.resolutebi.baseline.expr;

import java.io.IOException;
import java.io.Reader;

interface Parser {
  
  public static Parser create () {
    return create(LexicalAnalyzerFactory.create());
  }
  
  public static Parser create (LexicalAnalyzerFactory factory) {
    return ParserImpl.create(factory);
  }
  
  public Expression<Double> parse (String expression);
  
  public Expression<Double> parse (Reader in) throws IOException;

}
