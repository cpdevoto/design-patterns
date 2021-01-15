package com.resolute.okhttp3.simple;

import java.io.IOException;

@FunctionalInterface
public interface HttpCall {
  public void execute() throws IOException;
}
