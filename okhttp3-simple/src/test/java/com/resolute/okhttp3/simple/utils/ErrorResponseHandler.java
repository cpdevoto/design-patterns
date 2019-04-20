package com.resolute.okhttp3.simple.utils;

import java.io.IOException;

import com.resolute.okhttp3.simple.HttpResponse;

@FunctionalInterface
public interface ErrorResponseHandler {
  public void handle(HttpResponse response) throws IOException;
}
