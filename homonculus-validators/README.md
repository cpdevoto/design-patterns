# homonculus-validators

**Owner(s):** Carlos Devoto

The **homonculus-validators** library includes a collection of custom validation annotations together with their corresponding validation classes as
defined by [JSR 303 - Bean Validation](http://beanvalidation.org/1.0/spec/). The following validation annotations are included:

## List of validation annotations

**ContainsKeys**: The annotated element must be a ```java.util.Map``` that contains the specified string key(s).  You might use this
annotation, for instance, to ensure that a Map<String, String> property includes an entry with a key of "retrieveClaims" and an entry
with a key of "updateClaims" as follows:

```java
  @NotNull
  @ContainsKeys({"retrieveClaims","updateClaims"})
  @JsonProperty
  private Map<String, String> processPaths;
```

**FieldCompare**: A type-level validation annotation that can be used to enforce a constraint at the class level involving two different
properties of a ```Comparable<?>``` bean.  The comparison operators supported include LESS_THAN, LESS_THAN_OR_EQUALS, EQUALS, GREATER_THAN_OR_EQUALS,
GREATER_THAN, and NOT_EQUALS. Note that, in order for this annotation to work, each referenced property must expose a getter method in accordance
with the JavaBeans specification. You might use this annotation, for instance, to ensure that a property named minWorkers has a value
which is less than or equal to the value of the maxWorkers property as follows:

```java
@FieldCompare(first = "minWorkers", operator = ComparisonOperator.LESS_THAN_OR_EQUALS,
    second = "maxWorkers", fieldClass = Integer.class)
public class WorkerPoolConfiguration {

  @Min(0)
  @NotNull
  @JsonProperty
  private int minWorkers;

  @Min(1)
  @NotNull
  @JsonProperty
  private int maxWorkers;
  
  public int getMinWorkers() {
    return minWorkers;
  }

  public int getMaxWorkers() {
    return maxWorkers;
  }  
}
```

**MinDuration**: The annotated element must be a ```org.devoware.validators.util.Duration``` object whose value must be higher or 
equal to the specified minimum. You might use this annotation, for instance, to ensure that a property named maxIdleTime has a value
which is greater than or equal to 1 minute as follows:

```java
  @MinDuration(value=1, unit=TimeUnit.MINUTES)
  @NotNull
  @JsonProperty
  private Duration maxIdleTime;
```

**PortRange**: The annotated element must be an int value with a range that defaults to the range of allowable port numbers on a server
(1-65535).  You might use this annotation, for instance, to validate any property that represents a server port as follows:

```java
  @PortRange
  @JsonProperty
  private int port;
```
