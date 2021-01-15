package com.resolute.okhttp3.simple;

import com.fasterxml.jackson.core.JsonProcessingException;

@FunctionalInterface
public interface RequestBuilderFunction {

  public void accept(RequestBuilder builder) throws JsonProcessingException;


}
