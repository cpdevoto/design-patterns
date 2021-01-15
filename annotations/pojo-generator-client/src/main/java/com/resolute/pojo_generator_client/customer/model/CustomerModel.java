package com.resolute.pojo_generator_client.customer.model;

import com.resolute.pojo.annotations.Pojo;
import com.resolute.pojo.annotations.PojoModule;
import com.resolute.pojo.annotations.Required;

@PojoModule
public class CustomerModel {

  @Pojo(json = true)
  class Customer {
    @Required
    int id;
    @Required
    String name;

    String displayName;
  }

}
