package com.resolute.pojo_generator_client.user.model;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.resolute.pojo.annotations.Pojo;
import com.resolute.pojo.annotations.PojoModule;
import com.resolute.pojo.annotations.Required;

@PojoModule
public class UserModel {

  @Pojo(json = true)
  class Manager {
    @Required
    String username;
    String firstname;
    String lastname;
    @Required
    int numEmployees;
    Set<Department> departments;
  }

  @Pojo // Commenting this spec out so that it doesn't get regenerated!
  class Employee {
    @Required
    String username;
    String firstname;
    String lastname;
    Map<Integer, List<String>> roles;
  }

  @Pojo(json = true)
  class Department {
    @Required
    int id;
    @Required
    String name;
  }

  @Pojo
  class Foo {
    @Required
    int id;
    @Required
    String name;
  }

  @Pojo
  class Bar {
    @Required
    int id;
    @Required
    String name;
  }
}
