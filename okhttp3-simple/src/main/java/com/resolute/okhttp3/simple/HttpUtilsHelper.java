package com.resolute.okhttp3.simple;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

import okhttp3.MediaType;

class HttpUtilsHelper {

  public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
  public static final MediaType TEXT = MediaType.parse("text/plain; charset=utf-8");
  public static final ObjectMapper MAPPER = new ObjectMapper();

  static {
    MAPPER.registerModule(new GuavaModule());
    MAPPER.registerModule(new Jdk8Module());
    MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
  }

  private HttpUtilsHelper() {}

}
