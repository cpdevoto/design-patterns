package org.devoware.json.model;

import static java.util.Objects.requireNonNull;

import org.devoware.json.lexer.LexicalAnalyzer;

public class StringValue extends AbstractValue<String> implements JsonNode {

  public StringValue(String value) {
    super(Type.STRING, requireNonNull(value, "value cannot be null"));
  }

  @Override
  public String toString() {
    return "\"" + escape(value) + "\"";
  }

  private String escape(String value) {
    StringBuilder buf = new StringBuilder();
    for (int i = 0; i < value.length(); i++) {
      String s = escape(value.charAt(i));
      buf.append(s);
    }
    return buf.toString();
  }

  private String escape(char c) {
    switch (c) {
      case '"':
        return "\\\"";
      case '\\':
        return "\\\\";
      case '/':
        return "/";
      case '\b':
        return "\\b";
      case '\f':
        return "\\f";
      case '\n':
        return "\\n";
      case '\r':
        return "\\r";
      case '\t':
        return "\\t";
      default:
        if (LexicalAnalyzer.isValidStringCharacter(c)) {
          return String.valueOf(c);
        }
        return "\\u" + Integer.toHexString(c | 0x10000).substring(1);
    }
  }
}
