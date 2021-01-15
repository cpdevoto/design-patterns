package com.resolute.pojo_generator_client.model;

import java.util.List;
import java.util.Map;

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
  }

  // @Pojo Commenting this spec out so that it doesn't get regenerated!
  class Employee {
    @Required
    String username;
    String firstname;
    String lastname;
    Map<Integer, List<String>> roles;
    int[] ids;
  }

}