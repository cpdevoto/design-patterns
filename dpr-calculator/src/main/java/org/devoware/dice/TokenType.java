package org.devoware.dice;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

enum TokenType {
  // @formatter:off
  PLUS("'+'"),
  MINUS("'-'"),
  DIE("'d'"),
  NUMBER("a number"),
  EOE("end of expression");
  // @formatter:off
  
  static final Set<Character> SPECIAL_CHARS = ImmutableSet.<Character>builder()
      .add('+')
      .add('-')
      .add('d')
      .add((char) -1)
      .build();
  
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
