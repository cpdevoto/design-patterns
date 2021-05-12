package com.resolute.dataset.cloner.engine.script;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.util.function.Consumer;

class ScriptBuffer {
  private final StringBuilder buf = new StringBuilder();
  private final StringBuilder lineBuffer = new StringBuilder();

  private final int executionThreshold;
  private final Consumer<String> consumer;

  ScriptBuffer(int executionThreshold, Consumer<String> consumer) {
    checkArgument(executionThreshold > 0, "expected a positive integer execution threshold");
    this.executionThreshold = executionThreshold;
    this.consumer = requireNonNull(consumer, "consumer cannot be null");
  }

  void processLine(String line) {
    requireNonNull(line, "line cannot be null");
    if (line.trim().isEmpty()) {
      return;
    }
    lineBuffer.append(line).append("\n");
    if (!line.trim().endsWith(";")) {
      return;
    }
    if (buf.length() + lineBuffer.length() > executionThreshold) {
      if (buf.length() > 0) {
        String sql = buf.toString().trim();
        if (!sql.isEmpty()) {
          consumer.accept(buf.toString());
        }
      }
      buf.setLength(0);
    }
    buf.append(lineBuffer.toString()).append("\n");
    lineBuffer.setLength(0);
  }


  public void flush() {
    if (buf.length() > 0) {
      String sql = buf.toString().trim();
      if (!sql.isEmpty()) {
        consumer.accept(buf.toString());
      }
    }
  }

}
