package com.resolute.pojo_generator_client;

import com.resolute.pojo_generator_client.user.model.Employee;
import com.resolute.pojo_generator_client.user.model.Manager;

public class Main {

  public static void main(String[] args) {

    Employee employee = Employee.builder()
        .withUsername("gdevoto")
        .build();

    Manager manager = Manager.builder()
        .withUsername("cdevoto")
        .withNumEmployees(12)
        .build();

    System.out.println(employee);
    System.out.println(manager);
  }

}
