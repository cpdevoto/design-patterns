package lexer;

public class Word extends Token {

  public static final Word
  AND = new Word("&&", Tag.AND),
  OR = new Word("||", Tag.OR),
  EQ = new Word("==", Tag.EQ),
  NE = new Word("!=", Tag.NE),
  LE = new Word("<=", Tag.LE),
  GE = new Word(">=", Tag.GE),
  MINUS = new Word("minus", Tag.MINUS),
  TRUE = new Word("true", Tag.TRUE),
  FALSE = new Word("false", Tag.FALSE),
  TEMP = new Word("t", Tag.TEMP);
  
  public final String lexeme;
  
  public Word (String s, int tag) {
    super(tag);
    lexeme = s;
  }
  
  public String toString() {
    return lexeme;
  }
  
}
