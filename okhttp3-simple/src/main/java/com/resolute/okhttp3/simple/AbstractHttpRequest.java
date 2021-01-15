package com.resolute.okhttp3.simple;

import static com.google.common.base.Preconditions.checkArgument;
import static com.resolute.okhttp3.simple.HttpUtilsHelper.MAPPER;
import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Lists;

import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public abstract class AbstractHttpRequest<R extends AbstractHttpRequest<R>>
    implements HttpRequestUrlBuilder<R> {

  private static final String BAD_RELATIVE_PATH_MESSAGE =
      "If you want to add a query string, pass in a Consumer<HttpUrl.Builder> object as a second argument "
          + "and invoke the addQueryParameter(String, String) method on the HttpUrl.Builder object.";
  private final String baseUrl;
  private final OkHttpClient client;

  private String relativePath;
  private Consumer<HttpUrl.Builder> buildUrlHandler;
  private List<RequestBuilderFunction> buildRequestHandlers = Lists.newArrayList();
  private boolean throwBadResponse = true;


  protected AbstractHttpRequest(AbstractHttpRequestFactory factory) {
    this.baseUrl = factory.getBaseUrl();
    this.client = factory.getClient();
  }

  public final R doNotThrowBadResponse() {
    this.throwBadResponse = false;
    return getThis();
  }

  @Override
  public final R withUrl(String relativePath) {
    requireNonNull(relativePath, "relativePath cannot be null");
    checkArgument(!relativePath.contains("?"),
        BAD_RELATIVE_PATH_MESSAGE);
    this.relativePath = relativePath;
    return getThis();
  }

  @Override
  public final R withUrl(String relativePath, Consumer<HttpUrl.Builder> buildUrlHandler) {
    this.relativePath = requireNonNull(relativePath, "relativePath cannot be null");
    checkArgument(!relativePath.contains("?"),
        BAD_RELATIVE_PATH_MESSAGE);
    this.buildUrlHandler = requireNonNull(buildUrlHandler, "buildUrlHandler cannot be null");
    return getThis();
  }

  public final R withRequest(RequestBuilderFunction buildRequestHandler) {
    requireNonNull(buildRequestHandler, "buildRequestHandler cannot be null");
    this.buildRequestHandlers.add(buildRequestHandler);
    return getThis();
  }

  public final R addHeader(String name, String value) {
    requireNonNull(name, "name cannot be null");
    requireNonNull(value, "value cannot be null");
    this.buildRequestHandlers.add(builder -> {
      builder.addHeader(name, value);
    });
    return getThis();
  }

  public final R delete() {
    this.buildRequestHandlers.add(builder -> {
      builder.delete();
    });
    return getThis();
  }

  public final <T> R delete(T value) throws JsonProcessingException {
    requireNonNull(value, "value cannot be null");
    this.buildRequestHandlers.add(builder -> {
      builder.delete(value);
    });
    return getThis();
  }

  public final R delete(MediaType mediaType, String content) throws JsonProcessingException {
    requireNonNull(mediaType, "mediaType cannot be null");
    requireNonNull(content, "content cannot be null");
    this.buildRequestHandlers.add(builder -> {
      builder.delete(mediaType, content);
    });
    return getThis();
  }

  public final R get() {
    this.buildRequestHandlers.add(builder -> {
      builder.get();
    });
    return getThis();
  }

  public final R head() {
    this.buildRequestHandlers.add(builder -> {
      builder.head();
    });
    return getThis();
  }

  public final R header(String name, String value) {
    requireNonNull(name, "name cannot be null");
    requireNonNull(value, "value cannot be null");
    this.buildRequestHandlers.add(builder -> {
      builder.header(name, value);
    });
    return getThis();
  }

  public final R headers(Headers headers) {
    requireNonNull(headers, "headers cannot be null");
    this.buildRequestHandlers.add(builder -> {
      builder.headers(headers);
    });
    return getThis();
  }

  public final <T> R patch(T value) throws JsonProcessingException {
    requireNonNull(value, "value cannot be null");
    this.buildRequestHandlers.add(builder -> {
      builder.patch(value);
    });
    return getThis();
  }

  public final R patch(MediaType mediaType, String content) throws JsonProcessingException {
    requireNonNull(mediaType, "mediaType cannot be null");
    requireNonNull(content, "content cannot be null");
    this.buildRequestHandlers.add(builder -> {
      builder.patch(mediaType, content);
    });
    return getThis();
  }

  public final <T> R post(T value) throws JsonProcessingException {
    requireNonNull(value, "value cannot be null");
    this.buildRequestHandlers.add(builder -> {
      builder.post(value);
    });
    return getThis();
  }

  public final R post(MediaType mediaType, String content) throws JsonProcessingException {
    requireNonNull(mediaType, "mediaType cannot be null");
    requireNonNull(content, "content cannot be null");
    this.buildRequestHandlers.add(builder -> {
      builder.post(mediaType, content);
    });
    return getThis();
  }

  public final <T> R put(T value) throws JsonProcessingException {
    requireNonNull(value, "value cannot be null");
    this.buildRequestHandlers.add(builder -> {
      builder.put(value);
    });
    return getThis();
  }

  public final R put(MediaType mediaType, String content) throws JsonProcessingException {
    requireNonNull(mediaType, "mediaType cannot be null");
    requireNonNull(content, "content cannot be null");
    this.buildRequestHandlers.add(builder -> {
      builder.put(mediaType, content);
    });
    return getThis();
  }

  public final R removeHeader(String name) {
    requireNonNull(name, "name cannot be null");
    this.buildRequestHandlers.add(builder -> {
      builder.removeHeader(name);
    });
    return getThis();
  }

  public void execute() throws IOException {
    execute(response -> response.isSuccessful());
  }


  public <E> E execute(Class<E> klass) throws IOException {
    requireNonNull(klass, "klass cannot be null");
    return executeJsonDeserialize(body -> MAPPER.readValue(body, klass));
  }

  public <E> E execute(TypeReference<E> typeRef) throws IOException {
    requireNonNull(typeRef, "typeRef cannot be null");
    return executeJsonDeserialize(body -> MAPPER.readValue(body, typeRef));
  }

  public <E> E execute(ResponseHandlerFunction<E> responseHandler) throws IOException {
    requireNonNull(relativePath, "You must set the URL");
    requireNonNull(responseHandler, "responseHandler cannot be null");

    HttpUrl.Builder urlBuilder = HttpUrl
        .parse(baseUrl + relativePath)
        .newBuilder();

    if (buildUrlHandler != null) {
      buildUrlHandler.accept(urlBuilder);
    }

    HttpUrl url = urlBuilder.build();

    Request.Builder requestBuilder = new Request.Builder().url(url);

    if (!buildRequestHandlers.isEmpty()) {
      RequestBuilder builderDecorator = new RequestBuilder(requestBuilder);
      for (RequestBuilderFunction buildRequestHandler : buildRequestHandlers) {
        buildRequestHandler.accept(builderDecorator);
      }
    }

    Request request = requestBuilder.build();

    try (Response response = client.newCall(request).execute()) {
      if (!response.isSuccessful() && throwBadResponse)
        throw new BadResponseException("Unexpected code " + response, new HttpResponse(response));

      E result = responseHandler.apply(response);
      if (result == response) {
        // Don't allow people to just return the response; it is bad practice. At least wrap it with
        // an HttpResponse
        throw new IllegalArgumentException(
            "You attempted to return the raw Response object! This is a code smell. You should rethink what you are trying to test.");
      }
      return result;
    }
  }



  protected abstract R getThis();

  private <E> E executeJsonDeserialize(JsonMapperFunction<E> jsonDeserializer) throws IOException {
    return execute(response -> {
      E result = null;
      if (response.isSuccessful()) {
        String body = response.body().string();
        if (!StringUtils.isEmpty(body)) {
          result = jsonDeserializer.apply(body);
        }
      }
      return result;
    });
  }


  @FunctionalInterface
  private static interface JsonMapperFunction<R> {

    R apply(String json) throws IOException;

  }
}
