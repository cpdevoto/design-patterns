# dice-golem-2

**Owner(s):** Carlos Devoto

A library that can be used to parse and execute dice roll expressions of the form "1d20 + 5".  The latter expression, when parsed, would produce an ``Expression`` object with a ``roll()`` that, when called, would generate and return a random number between 6 and 25.  

This library exists for educational purposes, to show some best practices for automated testing gleaned from the "Software Engineering at Google" book.  Specific practices include:

  * **Coding for Testability:** The ``Die`` class uses an object that implements the ``RandomNumberGenerator`` interface instead of using the ``Random`` class directly.  This allows for the injection of a mock ``RandomNumberGenerator`` object that returns specific numbers instead of randomly generated numbers, which greatly simplifies many tests.  Random number generation is only used when testing that the ``Die`` class itself does what it is supposed to.

  * **Favor Real Implementations to Doubles:** We do not attempt to use doubles for testing anything except the ``RandomNumberGenerator`` used by the ``Die`` class.  Doubles include Fakes, Stubbing (i.e. Mockito.when().then()), and Interaction Testing (i.e. Mockito.verify()).  The latter two techniques break the encapsulation of the class under test which leads to very brittle test that tend to break any time a change is made to the class under test.
  
  * **Test Behaviors not Methods:** We want a concise test method for each behavior that comprises the public API of a given class.  The ratio of behaviors to methods in the class under test is generally many-to-many.  A good way to force yourself to test behaviors is to create test methods that follow a "should...when..." naming convention (e.g. should_return_false_when_credit_card_is_expired).  Each method should test a single thing and be separated into three sections delineated by a "Given" comment, and "When" comment, and a "Then" comment.  In certain circumstances, it is possible to have a sequence of "When" and "Then" sections, but having too many of these is a sign that you are testing too many behaviors in your test method.
  
  * **Avoid Retesting Things That you have Already Tested** Avoid retesting things that you already tested in downstream dependencies. For instance, if you have already tested that the ``Die`` class properly generates random numbers in a range that is determined by the ``Die`` object's type, then you can substitute a test double that generates specific numbers when you are writing tests for the ``roll()`` method for each ``Expression`` class.  Doing so will simplify those tests, improve their performance, and improve their readability.
  
  * **Test Readability 1:** In order to improve the readability of the tests I wrote to validate that each instance of the Die class was generating random numbers in an expected range, I created an ``org.devoware.dicegolem.testinfra.AssertRandomNumberGenerator`` class which exposes an API that allows for the following assertions:
  
  ```java
     assertThat(() -> Die.D4.roll())
        .generatesNumbersBetween(1, 4);
  ```

  * **Test Readability 2:** Comparing an Abstract Syntax Tree (AST) produced by a parse operation to an expected AST can be cumbersome and hard to follow.  In order to improve the readability of such tests, I created an overridden version of the ``hashCode()``, ``equals(Object)``, and ``toString()`` method for each model object used in the AST.  This allows for tests in which I manually construct an expected AST and compare it to the actual AST using a very simple ``assertThat(actual).isEqualTo(expected)`` method.  Moreover, the overridden. ``toString()`` method allows for good, descriptive error messages when a test fails such as "Expected (17 + (9 - (4 + 1))), but found ((((17 + 9) - 4) + 1))". Within these messages, an AST tree structure is depicted using nested parentheses. The entire approach is shown below:

```java
  @Test
  public void parentheses_can_override_default_precedence() {
    // When
    
    Expression actual = Parser.parse("17 + (9 - (4 + 1))");

    // Then -> expect the result to equal (17 + (9 - (4 + 1)))
    
    Expression expected = new PlusExpression(
        new ValueExpression(17),
        new MinusExpression(new ValueExpression(9),
            new PlusExpression(new ValueExpression(4), new ValueExpression(1))));
    
    assertThat(actual).isEqualTo(expected);

  }
```
  
  
