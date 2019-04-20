package com.resolute.okhttp3.simple;

import static com.resolute.okhttp3.simple.HttpUtilsHelper.MAPPER;
import static java.util.Objects.requireNonNull;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;

import okhttp3.Headers;
import okhttp3.Response;

public final class HttpResponse {
  private final Response response;
  private final String body;

  public HttpResponse(Response response) {
    this.response = requireNonNull(response, "response cannot be null");
    String body;
    try {
      body = response.body().string();
    } catch (Exception e) {
      body = "";
    }
    this.body = body;
  }

  public boolean isSuccessful() {
    return response.isSuccessful();
  }

  public int code() {
    return response.code();
  }

  public String body() {
    return body;
  }

  public <T> T body(Class<T> klass) throws JsonParseException, JsonMappingException, IOException {
    requireNonNull(klass, "klass cannot be null");
    if (body.trim().isEmpty()) {
      return null;
    }
    T result = MAPPER.readValue(body, klass);
    return result;
  }

  public <T> T body(TypeReference<T> typeRef)
      throws JsonParseException, JsonMappingException, IOException {
    requireNonNull(typeRef, "typeRef cannot be null");
    if (body.trim().isEmpty()) {
      return null;
    }
    T result = MAPPER.readValue(body, typeRef);
    return result;
  }

  public String header(String name) {
    requireNonNull(name, "name cannot be null");
    return response.header(name);
  }

  public String header(String name, String defaultValue) {
    requireNonNull(name, "name cannot be null");
    requireNonNull(defaultValue, "defaultValue cannot be null");
    return response.header(name, defaultValue);
  }

  public Headers headers() {
    return response.headers();
  }
}
