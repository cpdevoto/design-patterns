package com.resolute.okhttp3.simple;

import static com.resolute.okhttp3.simple.HttpUtilsHelper.JSON;
import static com.resolute.okhttp3.simple.HttpUtilsHelper.MAPPER;
import static java.util.Objects.requireNonNull;

import com.fasterxml.jackson.core.JsonProcessingException;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

public class RequestBuilder {

  private final Request.Builder builder;

  RequestBuilder(Request.Builder builder) {
    this.builder = requireNonNull(builder, "builder cannot be null");
  }

  private static <T> RequestBody toRequestBody(T object) throws JsonProcessingException {
    String content;
    if (object instanceof String) {
      content = (String) object;
    } else {
      content = MAPPER.writeValueAsString(object);
    }
    RequestBody body = RequestBody.create(JSON, content);
    return body;
  }

  private static <T> RequestBody toRequestBody(MediaType mediaType, String content)
      throws JsonProcessingException {
    RequestBody body = RequestBody.create(mediaType, content);
    return body;
  }

  public RequestBuilder addHeader(String name, String value) {
    builder.addHeader(name, value);
    return this;
  }

  public RequestBuilder delete() {
    builder.delete();
    return this;
  }

  public <T> RequestBuilder delete(T object) throws JsonProcessingException {
    RequestBody body = toRequestBody(object);
    builder.post(body);
    return this;
  }

  public <T> RequestBuilder delete(MediaType mediaType, String content)
      throws JsonProcessingException {
    RequestBody body = toRequestBody(mediaType, content);
    builder.delete(body);
    return this;
  }

  public RequestBuilder get() {
    builder.get();
    return this;
  }

  public RequestBuilder head() {
    builder.head();
    return this;
  }

  public RequestBuilder header(String name, String value) {
    builder.header(name, value);
    return this;
  }

  public RequestBuilder headers(Headers headers) {
    builder.headers(headers);
    return this;
  }

  public <T> RequestBuilder patch(T object) throws JsonProcessingException {
    RequestBody body = toRequestBody(object);
    builder.patch(body);
    return this;
  }

  public <T> RequestBuilder patch(MediaType mediaType, String content)
      throws JsonProcessingException {
    RequestBody body = toRequestBody(mediaType, content);
    builder.patch(body);
    return this;
  }

  public <T> RequestBuilder post(T object) throws JsonProcessingException {
    RequestBody body = toRequestBody(object);
    builder.post(body);
    return this;
  }

  public <T> RequestBuilder post(MediaType mediaType, String content)
      throws JsonProcessingException {
    RequestBody body = toRequestBody(mediaType, content);
    builder.post(body);
    return this;
  }


  public <T> RequestBuilder put(T object) throws JsonProcessingException {
    RequestBody body = toRequestBody(object);
    builder.put(body);
    return this;
  }

  public <T> RequestBuilder put(MediaType mediaType, String content)
      throws JsonProcessingException {
    RequestBody body = toRequestBody(mediaType, content);
    builder.put(body);
    return this;
  }

  public RequestBuilder removeHeader(String name) {
    builder.removeHeader(name);
    return this;
  }

}
