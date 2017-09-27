package com.resolutebi.baseline.expr;



interface Position {
  
  static Position copyOf(Position position) {
    return new BasicPosition(position.getLine(), position.getCharacter());
  }

  public int getLine();
  public int getCharacter();
  
}
