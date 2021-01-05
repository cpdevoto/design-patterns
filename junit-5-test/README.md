# dice-golem

**Owner(s):** Carlos Devoto

A Java library for parsing dice roll expressions. 
  
### Sample Use

```java
import org.dicegolem.Dice;

public class Main {

  public static void main(String[] args) {
    // The following dice roll modifiers are supported:
    // 1. dlX: drop the lowest X rolls
    // 2. klX: keep the lowest X rolls
    // 3. dhX: drop the highest X rolls
    // 4. khX: keep the highest X rolls
    // 5. ro<X: reroll each die once if the first roll of that die is less than or equal to X

    // Roll 4d6 and drop the lowest roll
    System.out.println("RESULT1: " + Dice.roll("4d6dl1"));

    // Roll 2d6 and reroll each die once if the first roll of that die is less than or equal to 2
    System.out.println("RESULT2: " + Dice.roll("2d6ro<2"));

    // Roll 1d20 and add 5 to the result
    System.out.println("RESULT3: " + Dice.roll("1d20 + 7"));

  }

}
```  
