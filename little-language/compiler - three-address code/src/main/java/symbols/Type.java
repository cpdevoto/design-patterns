package symbols;

import lexer.Tag;
import lexer.Word;

public class Type extends Word {
  public static final Type 
    INT   = new Type("int", Tag.BASIC, 4),
    FLOAT = new Type("float", Tag.BASIC, 8),
    CHAR  = new Type("char", Tag.BASIC, 1),
    BOOL  = new Type("bool", Tag.BASIC, 1);
  
  public int width = 0;

  public static boolean numeric (Type p) {
    if (p == Type.CHAR || p == Type.INT || p == Type.FLOAT) {
      return true;
    }
    return false;
  }
  
  public static Type max(Type p1, Type p2) {
    if (!numeric(p1) || !numeric(p2)) {
      return null;
    } else if (p1 == Type.FLOAT || p2 == Type.FLOAT) {
      return Type.FLOAT;
    } else if (p1 == Type.INT || p2 == Type.INT) {
      return Type.INT;
    }
    return Type.CHAR; 
    
  }
  
  public Type(String s, int tag, int w) {
    super(s, tag);
    width = w;
  }

}
