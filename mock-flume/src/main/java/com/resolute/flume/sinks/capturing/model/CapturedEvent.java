package com.resolute.flume.sinks.capturing.model;

import static java.util.Objects.requireNonNull;

import java.util.Base64;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.flume.Event;

import com.google.common.base.Charsets;

public class CapturedEvent {
  private final Map<String, String> headers;
  private final String body;

  public CapturedEvent(Event event) {
    requireNonNull(event, "event cannot be null");
    this.headers = event.getHeaders();
    this.body = new String(Base64.getEncoder().encode(event.getBody()), Charsets.UTF_8);
  }

  public Map<String, String> getHeaders() {
    return headers;
  }

  public String getBody() {
    return body;
  }

  public String toString() {
    StringBuilder buf = new StringBuilder("{\"headers\":{");
    boolean firstLoop = true;
    for (Entry<String, String> entry : headers.entrySet()) {
      if (firstLoop) {
        firstLoop = false;
      } else {
        buf.append(",");
      }
      buf.append("\"").append(entry.getKey()).append("\":\"").append(entry.getValue()).append("\"");
    }
    buf.append("},\"body\":\"").append(body).append("\"}");
    return buf.toString();
  }

}
