package org.devoware.simplesearch.parser;

import java.io.Reader;

import org.devoware.simplesearch.lexer.LexicalAnalyzerFactory;
import org.devoware.simplesearch.model.Expression;

public interface Parser {
  
  public static Parser create (LexicalAnalyzerFactory factory) {
    return ParserImpl.create(factory);
  }
  
  public Expression parse (String expression);
  
  public Expression parse (Reader in);

}
