package lexer;

public class Num extends Token {
   
  public final int value;
  
  public Num (int v) {
    super(Tag.NUM);
    value = v;
  }
  
  public String toString() {
    return "" + value;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + value;
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
    Num other = (Num) obj;
    if (value != other.value)
      return false;
    return true;
  }
}
