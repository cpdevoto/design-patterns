package org.devoware.dice;

enum TokenType {
  // @formatter:off
  PLUS("'+'"),
  MINUS("'-'"),
  MUTIPLY("'*'"),
  LEFT_PAREN("'('"),
  RIGHT_PAREN("')'"),
  DIE("'d'"),
  NUMBER("an integer"),
  FLOAT("a floating point number"),
  REROLL_ONCE("'ro<'"),
  NO_CRIT("'nc'"),
  EOE("end of expression");
  // @formatter:off
  
  private final String value;
  
  private TokenType(String value) {
    this.value = value;
  }
  
  String getValue() {
    return value;
  }
  
  public String toString() {
    return value;
  }
  
}
