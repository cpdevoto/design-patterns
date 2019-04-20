package com.resolutebi.testutils.docker.flume;

import static java.util.Objects.requireNonNull;

import java.util.Base64;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.flume.Event;
import org.apache.flume.event.EventBuilder;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = CapturedEvent.Builder.class)
public class CapturedEvent {
  private final Map<String, String> headers;
  private final byte[] body;

  @JsonCreator
  public static Builder builder() {
    return new Builder();
  }

  public static Builder builder(CapturedEvent capturedEvent) {
    return new Builder(capturedEvent);
  }

  private CapturedEvent(Builder builder) {
    this.headers = builder.headers;
    this.body = Base64.getDecoder().decode(builder.body);
  }

  public Map<String, String> getHeaders() {
    return headers;
  }

  public byte[] getBody() {
    return body;
  }

  public String getBodyAsString() {
    return new String(body, Charsets.UTF_8);
  }

  public Event toEvent() {
    return EventBuilder.withBody(body, headers);
  }

  @JsonPOJOBuilder
  public static class Builder {
    private Map<String, String> headers;
    private String body;

    private Builder() {}

    private Builder(CapturedEvent capturedEvent) {
      requireNonNull(capturedEvent, "capturedEvent cannot be null");
      this.headers = capturedEvent.headers;
      this.body = Base64.getEncoder().encodeToString(capturedEvent.body);
    }

    public Builder with(Consumer<Builder> consumer) {
      requireNonNull(consumer, "consumer cannot be null");
      consumer.accept(this);
      return this;
    }

    public Builder withHeaders(Map<String, String> headers) {
      requireNonNull(headers, "headers cannot be null");
      this.headers = ImmutableMap.copyOf(headers);
      return this;
    }

    public Builder withBody(String body) {
      requireNonNull(body, "body cannot be null");
      this.body = body;
      return this;
    }

    public CapturedEvent build() {
      requireNonNull(headers, "headers cannot be null");
      requireNonNull(body, "body cannot be null");
      return new CapturedEvent(this);
    }
  }
}
