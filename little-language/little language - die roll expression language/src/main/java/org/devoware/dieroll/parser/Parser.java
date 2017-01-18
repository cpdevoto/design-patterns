package org.devoware.dieroll.parser;

import java.io.Reader;

import org.devoware.dieroll.lexer.LexicalAnalyzerFactory;
import org.devoware.dieroll.model.ValueGenerator;

public interface Parser {
  
  public static Parser create (LexicalAnalyzerFactory factory) {
    return ParserImpl.create(factory);
  }
  
  public ValueGenerator parse (String expression);
  
  public ValueGenerator parse (Reader in);

}
