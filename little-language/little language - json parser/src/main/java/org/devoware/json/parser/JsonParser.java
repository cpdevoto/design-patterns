package org.devoware.json.parser;

import java.io.IOException;
import java.io.Reader;

import org.devoware.json.model.JsonNode;

public class JsonParser {
  
  public static <T extends JsonNode> T parse (String expression) {
    Parser parser = Parser.create();
    return parser.parse(expression);
  }
  
  public static <T extends JsonNode> T parse (Reader in) throws IOException {
    Parser parser = Parser.create();
    return parser.parse(in);
  }


  private JsonParser() {}

}
