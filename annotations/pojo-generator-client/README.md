# utils-simple

**Owner(s):** Carlos Devoto

A client project with a Main class that can be modified to generate different POJO classes using the ``PojoGenerator`` API exposed by ``utils-simple``.  Make changes to this project locally, but don't ever commit them!

## Sample Code

```java
package com.resolute.user;

import com.resolute.pojo.annotations.PojoModule;
import com.resolute.pojo.annotations.Pojo;
import com.resolute.pojo.annotations.Required;

@PojoModule
public class UserModel {

  @Pojo(json = true) // Generate Jackson annotations (default is false)
  class Manager {
    @Required // Any field with annotation is considered a required field.
    String username;
    String firstname;
    String lastname;
    @Required
    int numEmployees;
  }

  @Pojo
  class Employee {
    @Required
    String username;
    String firstname;
    String lastname;
    private int invalidField; // Field cannot be private!
  }

  @Pojo 
  static class InvalidPojo1 { // Class cannot be static!
    @Required
    String username;
    String firstname;
    String lastname;
  }

  class InvalidPojo2 { // Class missing Pojo annotation!
    @Required
    String username;
    String firstname;
    String lastname;
  }

}
```