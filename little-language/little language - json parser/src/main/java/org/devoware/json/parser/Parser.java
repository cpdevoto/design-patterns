package org.devoware.json.parser;

import java.io.Reader;

import org.devoware.json.lexer.LexicalAnalyzerFactory;
import org.devoware.json.model.JsonNode;

public interface Parser {
  
  public static Parser create () {
    return create(LexicalAnalyzerFactory.create());
  }
  
  public static Parser create (LexicalAnalyzerFactory factory) {
    return ParserImpl.create(factory);
  }
  
  public <T extends JsonNode> T parse (String expression);
  
  public <T extends JsonNode> T parse (Reader in);

}
