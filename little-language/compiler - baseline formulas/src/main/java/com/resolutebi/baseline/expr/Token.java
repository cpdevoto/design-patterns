package com.resolutebi.baseline.expr;

import static java.util.Objects.requireNonNull;


class Token {
  static enum Type {
    IF("if"),
    ELSE("else"),
    FALSE("false"),
    TRUE("true"),
    LEFT_PAREN("'('"),
    RIGHT_PAREN("')'"),
    EQUALS("'=='"),
    NOT_EQUALS("'!='"),
    GREATER_THAN("'>'"),
    GREATER_THAN_OR_EQUALS("'>='"),
    LESS_THAN("'<'"),
    LESS_THAN_OR_EQUALS("'<='"),
    AND("'&&'"),
    OR("'||'"),
    NOT("'!'"),
    PLUS("'+'"),
    MINUS("'-'"),
    MULTIPLY("'*'"),
    DIVIDE("'/'"),
    VARIABLE("a variable"),
    WORD("an unrecognized string"),
    DOUBLE("a number"),
    EOF("end of baseline expression");
    
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

  Token(Type type, Position position) {
    this.type = requireNonNull(type, "type cannot be null");
    this.position = Position.copyOf(requireNonNull(position, "position cannot be null"));
  }
  
  Type getType() {
    return type;
  }
  
  Position getPosition() {
    return position;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((position == null) ? 0 : position.hashCode());
    result = prime * result + ((type == null) ? 0 : type.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Token other = (Token) obj;
    if (position == null) {
      if (other.position != null)
        return false;
    } else if (!position.equals(other.position))
      return false;
    if (type != other.type)
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "Token [type=" + type + ", position=" + position + "]";
  }
}
