package org.devoware.dice;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

enum TokenType {
  // @formatter:off
  PLUS("'+'"),
  MINUS("'-'"),
  DIE("'d'"),
  NUMBER("a number"),
  REROLL_ONCE("'ro<"),
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
