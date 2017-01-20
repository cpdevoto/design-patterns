package org.devoware.json.symbols;

import static java.util.Objects.requireNonNull;

import org.devoware.json.lexer.Position;


public class Token {
  public static enum Type {
    NULL("null"),
    FALSE("false"),
    TRUE("true"),
    LEFT_SQUARE_BRACKET("'['"),
    RIGHT_SQUARE_BRACKET("']'"),
    LEFT_CURLY_BRACKET("'{'"),
    RIGHT_CURLY_BRACKET("'}'"),
    COLON("':'"),
    COMMA("','"),
    DOUBLE("a floating point number"),
    LONG("an integer"),
    STRING("a quoted string"),
    EOF("end of JSON string");
    
    private final String stringValue;
    
    private Type (String stringValue) {
      this.stringValue = stringValue;
    }
    
    @Override
    public String toString() {
      return stringValue;
    }
  }

  private final Type type;
  private final Position position;

  public Token(Type type, Position position) {
    this.type = requireNonNull(type, "type cannot be null");
    this.position = Position.copyOf(requireNonNull(position, "position cannot be null"));
  }
  
  public final Type getType() {
    return type;
  }
  
  public final Position getPosition() {
    return position;
  }

}
